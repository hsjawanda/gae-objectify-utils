/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.base.Joiner;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Not thread-safe and isn't meant to be shared by threads.
 *
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 */
@Data
@Accessors(chain = true, fluent = true)
public class PullTaskConfig {

	public static final String CLASS_NAME = "className";

	public static final String ID = "id";

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PullTaskConfig.class.getName());

	private static final String NAME_JOIN_SEPARATOR = "-";

	private static final Joiner NAME_JOINER = Joiner.on(NAME_JOIN_SEPARATOR).skipNulls();

	private Class<?> cls;

	private long delayForMillis = -1;

	/**
	 * In seconds
	 */
	private Long maxFrequency;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Queue queue;

	private long runAtMillis = -1;

	private Map<String, String> strParams = new LinkedHashMap<>();

	private String tag;

	private String taskName;

	private PullTaskConfig() {
	}

	public static PullTaskConfig create(@NonNull Class<?> cls) {
		return new PullTaskConfig().cls(cls);
	}

	public void addToQueue(@NonNull String queueName) throws IllegalArgumentException {
		TaskOptions opts = queueName(queueName).buildTaskOptions();
		this.queue.add(opts);
	}

	public Future<TaskHandle> addToQueueAsync(@NonNull String queueName) throws IllegalArgumentException {
		TaskOptions opts = queueName(queueName).buildTaskOptions();
		return this.queue.addAsync(opts);
	}

	public PullTaskConfig param(String key, boolean value) {
		param(key, Boolean.toString(value));
		return this;
	}

	public PullTaskConfig param(String key, Enum<?> value) {
		param(key, value.name());
		return this;
	}

	public PullTaskConfig param(String key, int number) {
		if (null != key) {
			this.strParams.put(key, Integer.toString(number));
		}
		return this;
	}

	public PullTaskConfig param(String key, @Nullable String value) {
		if (null != key) {
			this.strParams.put(key, value);
		}
		return this;
	}

	public void tryAddToQueue(@NonNull String queueName) {
		try {
			addToQueue(queueName);
		} catch (TaskAlreadyExistsException e) {
			// Do nothing
		}
	}

	private TaskOptions buildTaskOptions() {
		TaskOptions pullTask = TaskOptions.Builder.withMethod(Method.PULL).param(CLASS_NAME, this.cls.getName());
		List<String> nameFragments = new ArrayList<>(3);
		String freqFragment = null;
		long timeMillis = 0;
		if (null != this.maxFrequency) {
			freqFragment = Numbers.maxFrequencyFragment(this.maxFrequency.longValue());
		} else if (this.runAtMillis >= 0) {
			pullTask = pullTask.etaMillis(this.runAtMillis);
		} else if (this.delayForMillis >= 0) {
			pullTask = pullTask.countdownMillis(this.delayForMillis);
		}
		if (isNotBlank(this.taskName)) {
			nameFragments.add(this.taskName);
		}
		if (null != freqFragment) {
			if (nameFragments.isEmpty()) {
				nameFragments.add(this.cls.getName().replace('.', '_'));
				nameFragments.add(this.strParams.getOrDefault(ID, null));
			}
			nameFragments.add(freqFragment);
			pullTask = pullTask.etaMillis(Math.max(timeMillis - 250, 0));
		}
		if (!nameFragments.isEmpty()) {
			pullTask = pullTask.taskName(NAME_JOINER.join(nameFragments));
		}
		if (null != this.tag) {
			pullTask = pullTask.tag(this.tag);
		}
		for (String key : this.strParams.keySet()) {
			pullTask = pullTask.param(key, this.strParams.get(key));
		}
		return pullTask;
	}

	private PullTaskConfig queueName(String queueName) {
		this.queue = QueueFactory.getQueue(queueName);
		checkArgument(null != this.queue, "Couldn't retrieve a valid queue for '" + queueName + "'");
		return this;
	}

}
