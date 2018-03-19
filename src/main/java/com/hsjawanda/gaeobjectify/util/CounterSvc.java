/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.hsjawanda.gaeobjectify.data.ObjectifyDao;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class CounterSvc {

	static final ObjectifyDao<Counter> COUNTER = new ObjectifyDao<>(Counter.class);

	@Nonnull
	public static Counter get(@Nonnull final String counterName, final Integer numShards)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(counterName), "counterName" + Constants.NOT_BLANK);
		Counter counter = ofy().transact(new Work<Counter>() {
			@Override
			public Counter run() {
				Counter counter = COUNTER.getByIdThrow(counterName).orNull();
				if (null == counter) {
					counter = Counter.create(counterName, numShards);
					COUNTER.deferredSaveEntity(counter);
				}
				return counter;
			}
		});
		return counter;
	}

	public static Optional<Counter> getIfExists(@Nonnull final String counterName)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(counterName), "counterName" + Constants.NOT_BLANK);
		return COUNTER.getByIdThrow(counterName);
	}

	/**
	 * Cannot be used to decrease the number of shards. The total number of
	 * shards will not be allowed to go above {@link Counter#MAX_SHARDS}.
	 *
	 * @param counter
	 * @param delta
	 * @throws NullPointerException
	 */
	public static void incrementNumShards(@Nonnull final Counter counter, final int delta)
			throws NullPointerException {
		checkNotNull(counter);
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				Optional<Counter> cntr = COUNTER.getById(counter.getId());
				if (cntr.isPresent()) {
					cntr.get().incrementNumShards(delta);
					COUNTER.saveEntity(cntr.get());
				}
			}
		});
	}

}
