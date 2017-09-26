/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

	/**
	 * 05 Jun 2017
	 */
	private static final long						serialVersionUID	= 1L;

	@SuppressWarnings("unused")
	private static Logger							LOG					= Logger.getLogger(Counter.class
																				.getName());

	@Id
	@Setter(AccessLevel.PRIVATE)
	private String									name;

	private int										numShards;

	private String									description;

	@Ignore
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private transient final Random					CHOOSER				= new Random();

	public static final int							DEFAULT_NUM_SHARDS	= 5;

	public static final int							MAX_SHARDS			= 50;

	public static final String						SEPARATOR			= "-";

	public static final int							COUNT_EXPIRATION	= 120;

	public static final int							DEFAULT_SB_LEN		= 30;

	private static final MemcacheService			MEMCACHE			= MemcacheServiceFactory
																				.getMemcacheService();

	private static final AsyncMemcacheService ASYNC_MEMCACHE = MemcacheServiceFactory
			.getAsyncMemcacheService();

	private static final ObjectifyDao<CounterShard>	SHARD				= new ObjectifyDao<>(
																				CounterShard.class);

	private Counter() {
	}

	static Counter create(@Nonnull String name, @Nullable Integer initialNumShards)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(name), "name" + Constants.NOT_BLANK);
		Counter counter = new Counter().setName(name).setNumShards(initialNumShards);
		return counter;
	}

	Counter setNumShards(Integer numShards) {
		this.numShards = null == numShards ? DEFAULT_NUM_SHARDS : numShards.intValue();
		return this;
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

	@Override
	public String getId() {
		return this.name;
	}

	public void increment(final long delta) {
		final int shardNum = this.CHOOSER.nextInt(this.numShards);
		final String shardId = new StringBuilder(DEFAULT_SB_LEN).append(this.name).append(SEPARATOR)
				.append(shardNum).toString();
		MEMCACHE.delete(this.name);
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				CounterShard shard = SHARD.getById(shardId).orNull();
				if (null != shard) {
					shard.increment(delta);
				} else {
					shard = CounterShard.create(shardId, delta);
				}
				SHARD.deferredSaveEntity(shard);
			}
		});
	}

	public long getCount(boolean skipCache) {
		long count = 0;
		Long countObj = (Long) MEMCACHE.get(this.name);
		if (null != countObj && !skipCache) {
			count = countObj.longValue();
		} else {
			// TODO: maybe use BigInteger to avoid overflow?
			for (CounterShard shard : getAllShards()) {
				if (null != shard) {
					count += shard.getCount();
				}
			}
			ASYNC_MEMCACHE.put(this.name, Long.valueOf(count),
					Expiration.byDeltaSeconds(COUNT_EXPIRATION), SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
		}
		return count;
	}

	public long getCount() {
		return getCount(false);
	}

	public static long getCount(String name, boolean skipCache) throws IllegalArgumentException {
		checkArgument(isNotBlank(name), "name" + Constants.NOT_BLANK);
		long count = 0;
		Long countObj = (Long) MEMCACHE.get(name);
		if (null != countObj && !skipCache) {
			count = countObj.longValue();
		} else {
			// TODO: maybe use BigInteger to avoid overflow?
			Counter counter = CounterSvc.get(name, null);
			count = counter.getCount();
		}
		return count;
	}

	public void printAllShards() {
		System.out.println(Constants.commaJoiner.join(getAllShards()));
	}

	private Collection<CounterShard> getAllShards() {
		StringBuilder shardId = new StringBuilder(DEFAULT_SB_LEN).append(this.name).append(
				SEPARATOR);
		int initialSize = shardId.length();
		List<String> shardIds = new ArrayList<>(this.numShards);
		for (int i = 0; i < this.numShards; i++) {
			shardIds.add(shardId.append(i).toString());
			shardId.setLength(initialSize);
		}
		Map<String, CounterShard> shardMap = SHARD.getByStringIds(shardIds);
		return shardMap.values();
	}

}
