/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.hsjawanda.gaeobjectify.data.ObjectifyDao;
import com.hsjawanda.gaeobjectify.models.StringIdEntity;
import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.mutable.MutableInt;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;



/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Entity
@Cache(expirationSeconds = 600)
@Data
@EqualsAndHashCode(of = {"name"})
@Accessors(chain = true)
public class Counter implements Serializable, StringIdEntity {

	public static final int							COUNT_EXPIRATION	= 120;

	public static final int							DEFAULT_NUM_SHARDS	= 3;

	public static final int							DEFAULT_SB_LEN		= 30;

	public static final int							MAX_SHARDS			= 50;

	public static final String						SEPARATOR			= "-";

	private static AsyncMemcacheService				ASYNC_MEMCACHE;

	private static Logger							LOG					= Logger.getLogger(Counter.class
																				.getName());

	private static MemcacheService					MEMCACHE;

	/**
	 * 05 Jun 2017
	 */
	private static final long						serialVersionUID	= 1L;

	private static final ObjectifyDao<CounterShard>	SHARD				= new ObjectifyDao<>(
																				CounterShard.class);

	@Ignore
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private transient final Random					CHOOSER				= new Random();

	private String									description;

	@Id
	@Setter(AccessLevel.PRIVATE)
	private String									name;

	private int										numShards;

	private Counter() {
	}

	public static long getCount(String name, boolean skipCache) {
		return getCount(name, skipCache, true);
	}

	public static long getCount(String name, boolean skipCache, boolean createCounter) throws IllegalArgumentException {
		checkArgument(isNotBlank(name), "name" + Constants.NOT_BLANK);
		Long countObj = (Long) memcache().get(name);
		if (null != countObj && !skipCache)
			return countObj.longValue();
		else {
			Counter counter = createCounter ? CounterSvc.get(name, null) : CounterSvc.getIfExists(name).orNull();
			if (null == counter)
				throw new IllegalArgumentException("Couldn't find Counter with name '" + name + "'.");
			return counter.getCount(skipCache);
		}
	}

	static Counter create(@Nonnull String name, @Nullable Integer initialNumShards)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(name), "name" + Constants.NOT_BLANK);
		Counter counter = new Counter().setName(name).setNumShards(initialNumShards);
		return counter;
	}

	private static AsyncMemcacheService asyncMemcache() {
		if (null == ASYNC_MEMCACHE) {
			ASYNC_MEMCACHE = MemcacheServiceFactory.getAsyncMemcacheService();
		}
		return ASYNC_MEMCACHE;
	}

	private static MemcacheService memcache() {
		if (null == MEMCACHE) {
			MEMCACHE = MemcacheServiceFactory.getMemcacheService();
		}
		return MEMCACHE;
	}

	public long getCount() {
		return getCount(false);
	}

	public long getCount(boolean skipCache) {
		return skipCache ? getCountFromDs() : getCountFromCache();
	}

	@Override
	public String getId() {
		return this.name;
	}

	public void increment(final long delta) {
		final int shardNum = this.CHOOSER.nextInt(this.numShards);
		final String shardId = new StringBuilder(DEFAULT_SB_LEN).append(this.name).append(SEPARATOR).append(shardNum)
				.toString();
		memcache().delete(this.name);
		final MutableInt numTries = new MutableInt();
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				CounterShard shard = SHARD.getById(shardId).orNull();
				numTries.increment();
				if (null != shard) {
					shard.increment(delta);
				} else {
					shard = CounterShard.create(shardId, delta);
				}
				SHARD.deferredSaveEntity(shard);
			}
		});
		if (numTries.intValue() > 1) {
			LOG.warning("It took " + numTries.intValue() + " tries to increment counter '" + this.name + "'.");
		}
	}

	public void printAllShards() {
		System.out.println(Constants.commaJoiner.join(getAllShards()));
	}

	/**
	 * Cannot be used to decrease the number of shards. The total number of shards will not be
	 * allowed to go above MAX_SHARDS.
	 *
	 * @param delta the number to increase shards by
	 * @return {@code this}, for method chaining
	 */
	Counter incrementNumShards(int delta) {
		int intermediateVal = this.numShards + Math.abs(delta);
		this.numShards = Math.min(intermediateVal, MAX_SHARDS);
		return this;
	}

	Counter setNumShards(Integer numShards) {
		this.numShards = null == numShards ? DEFAULT_NUM_SHARDS : numShards.intValue();
		return this;
	}

	private Collection<CounterShard> getAllShards() {
		StringBuilder shardId = new StringBuilder(DEFAULT_SB_LEN).append(this.name).append(SEPARATOR);
		int initialSize = shardId.length();
		List<String> shardIds = new ArrayList<>(this.numShards);
		for (int i = 0; i < this.numShards; i++) {
			shardIds.add(shardId.append(i).toString());
			shardId.setLength(initialSize);
		}
		return ofy().transactionless().load().type(CounterShard.class).ids(shardIds).values();
	}

	private long getCountFromCache() {
		Long countObj = (Long) memcache().get(this.name);
		return null == countObj ? getCountFromDs() : countObj.longValue();
	}

	private long getCountFromDs() {
		long count = getAllShards().stream().filter(Objects::nonNull).mapToLong(x -> x.getCount()).sum();
		asyncMemcache().put(this.name, Long.valueOf(count), Expiration.byDeltaSeconds(COUNT_EXPIRATION),
				SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
		return count;
	}

}
