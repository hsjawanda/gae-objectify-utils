/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.defaultString;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Setter
@Accessors(chain = true)
public class TaskConfig<T> {

	private static Logger					LOG			= Logger.getLogger(TaskConfig.class
																.getName());

	@lombok.NonNull
	@Setter(AccessLevel.PRIVATE)
	private Class<T>						taskClass;

	private String							nameSuffix;

	@Setter(AccessLevel.NONE)
	private Queue							queue;

	private String							host;

	private RetryOptions					retryOptions;

	private long							delayMillis		= -1;

	private long							startMillis;

	private boolean							addTimestamp;

	private boolean							useAutoNaming;

	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.NONE)
	private String							taskName;

	/**
	 * In seconds.
	 */
	@Getter(AccessLevel.NONE)
	private Long							maxFrequency;

	@Singular()
	@Setter(AccessLevel.PRIVATE)
	private Map<String, String>				strParams;

	@Singular
	@Setter(AccessLevel.PRIVATE)
	private Map<String, byte[]>				byteParams;

	private byte[]							payload;

	private static final int DEF_RETRY_LIMIT = 2;

	public static final Range<Long>			DELAY_RANGE	= Range.closed(1L, 60 * 60 * 1000L);

	public static final String				MAPPING		= Config.get("tasks.mapping").or("/task");

	protected static final SimpleDateFormat	TASK_DATE	= new SimpleDateFormat(
																"yyyy-MM-dd_HH-mm-ss_SSS");

	static {
		TasksHelper.TASK_DATE.setTimeZone(TimeZone.getTimeZone("IST"));
	}

//	private TaskConfig() {}

	public static <T> TaskConfig<T> create(@NonNull Class<T> cls) {
		final boolean useAutoNaming = false, addTimestamp = true;
		final String nameSuffix = null, queueName = null;
		TaskConfig<T> config = new TaskConfig<T>().setTaskClass(cls).setNameSuffix(nameSuffix)
				.setQueueName(queueName).setDelayMillis(500).setAddTimestamp(addTimestamp)
				.setUseAutoNaming(useAutoNaming).setStrParams(new HashMap<String, String>(2))
				.setByteParams(new HashMap<String, byte[]>(1));
		return config;
	}

	public TaskConfig<T> strParam(String key, String value) {
		if (key != null && null != value) {
			this.strParams.put(key, value);
		}
		return this;
	}

	public TaskConfig<T> byteParam(String key, byte[] value) {
		if (null != key && null != value) {
			this.byteParams.put(key, value);
		}
		return this;
	}

	private TaskOptions buildTaskOptions() throws IllegalStateException, TaskAlreadyExistsException {
		checkState(null != this.queue, "No valid queue has been set.");
		String normalizedTaskName = normalizedTaskName();
		TaskOptions taskOptions = TaskOptions.Builder.withUrl(taskUrl(normalizedTaskName));
		RetryOptions ro = null == this.retryOptions ? RetryOptions.Builder.withTaskRetryLimit(
				DEF_RETRY_LIMIT).maxDoublings(2) : this.retryOptions;
		taskOptions = taskOptions.retryOptions(ro);
		if (null != this.host) {
			taskOptions = taskOptions.header("Host", this.host);
		}
		if (!this.useAutoNaming) {
			StringBuilder taskName = new StringBuilder(50).append(normalizedTaskName);
			if (isNotBlank(this.nameSuffix)) {
				taskName.append('_').append(defaultString(this.nameSuffix));
			}
			if (null != this.maxFrequency) {
				taskName.append('_').append(Numbers.maxFrequencyFragment(this.maxFrequency.longValue()));
			} else if (this.addTimestamp) {
				TasksHelper.TASK_DATE.setTimeZone(Constants.IST);
				taskName.append('_').append(TasksHelper.TASK_DATE.format(new Date()));
			}
			this.taskName = taskName.toString();
			taskOptions = taskOptions.taskName(this.taskName);
		}
		if (null != this.strParams) {
			for (String key : this.strParams.keySet()) {
				taskOptions = taskOptions.param(key, this.strParams.get(key));
			}
		}
		if (null != this.byteParams) {
			for (String key : this.byteParams.keySet()) {
				taskOptions = taskOptions.param(key, this.byteParams.get(key));
			}
		}
		if (null != this.payload) {
			taskOptions = taskOptions.payload(this.payload);
		}
		if (this.startMillis > 0) {
			taskOptions = taskOptions.etaMillis(this.startMillis);
		} else if (null != this.maxFrequency) {
			taskOptions = taskOptions.countdownMillis(this.maxFrequency.longValue() * 1000 - 500);
		} else if (this.delayMillis > 0) {
			taskOptions = taskOptions.countdownMillis(this.delayMillis);
		}
		return taskOptions;
	}

	public TaskHandle addToQueue() throws IllegalStateException, TaskAlreadyExistsException {
		return this.queue.add(buildTaskOptions());

	}

	public Future<TaskHandle> addToQueueAsync() {
		return this.queue.addAsync(buildTaskOptions());
	}

	public void tryAddToQueue() throws IllegalStateException {
		try {
			addToQueue();
		} catch (TaskAlreadyExistsException e) {
			LOG.info("Task named " + this.taskName + " already exists. Ignoring this task addition.");
		}
	}

	public TaskConfig<T> setQueueName(String qName) throws IllegalArgumentException {
		this.queue = null == qName ? QueueFactory.getDefaultQueue() : QueueFactory.getQueue(qName);
		checkArgument(null != this.queue, "Couldn't find a queue with name '" + qName + "'.");
		return this;
	}

	private String normalizedTaskName() {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.taskClass.getSimpleName());
	}

	private static String taskUrl(String normalizedName) {
		return Constants.pathJoiner.join(MAPPING, normalizedName);
	}

}
