/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Range;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Builder
//@Getter
@Setter
@Accessors(chain = true)
public class TaskConfig<T> {

	@SuppressWarnings("unused")
	private static Logger					LOG			= Logger.getLogger(TaskConfig.class
																.getName());

	@lombok.NonNull
	@Setter(AccessLevel.NONE)
	private Class<T>						taskClass;

	private String							nameSuffix;

	private String							queueName;

	private long							delayMillis;

	private boolean							addTimestamp;

	private boolean							useAutoNaming;

	@Singular()
	@Setter(AccessLevel.NONE)
	private Map<String, String>				strParams;

	@Singular
	@Setter(AccessLevel.NONE)
	private Map<String, byte[]>				byteParams;

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
		TaskConfig<T> config = new TaskConfig<T>(cls, nameSuffix, queueName, 500, addTimestamp,
				useAutoNaming, new HashMap<String, String>(2), new HashMap<String, byte[]>(1));
		return config;
	}

	public TaskConfig<T> strParam(String key, String value) {
		if (key != null) {
			this.strParams.put(key, value);
		}
		return this;
	}

	public TaskConfig<T> byteParam(String key, byte[] value) {
		if (null != key) {
			this.byteParams.put(key, value);
		}
		return this;
	}

	public void addToQueue() throws IllegalArgumentException {
		Queue q = null;
		if (null == this.queueName) {
			q = QueueFactory.getDefaultQueue();
		} else {
			q = QueueFactory.getQueue(this.queueName);
		}
		checkArgument(null != q, "Couldn't find a queue with name '" + this.queueName + "'.");
		String normalizedTaskName = normalizedTaskName();
		TaskOptions taskOptions = TaskOptions.Builder.withUrl(taskUrl(normalizedTaskName));
		if (!this.useAutoNaming) {
			StringBuilder taskName = new StringBuilder(50).append(normalizedTaskName);
			if (isNotBlank(this.nameSuffix)) {
				taskName.append('_').append(defaultString(this.nameSuffix));
			}
			if (this.addTimestamp) {
				TasksHelper.TASK_DATE.setTimeZone(Constants.IST);
				taskName.append('_').append(TasksHelper.TASK_DATE.format(new Date()));
			}
			taskOptions = taskOptions.taskName(taskName.toString());
		}
		if (null != this.strParams) {
			for (String key : this.strParams.keySet()) {
				String value = this.strParams.get(key);
				if (null != value) {
					taskOptions = taskOptions.param(key, value);
				}
			}
		}
		if (null != this.byteParams) {
			for (String key : this.byteParams.keySet()) {
				byte[] value = this.byteParams.get(key);
				if (null != value) {
					taskOptions = taskOptions.param(key, value);
				}
			}
		}
		if (DELAY_RANGE.contains(this.delayMillis)) {
			taskOptions = taskOptions.countdownMillis(this.delayMillis);
		}
		q.add(taskOptions);
	}

	private String normalizedTaskName() {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.taskClass.getSimpleName());
	}

	private static String taskUrl(String normalizedName) {
		return Constants.pathJoiner.join(MAPPING, normalizedName);
	}

	public static class TaskConfigBuilder<T> {

		private boolean	addTimestamp	= true;

		private long	delayMillis		= 500;

		private boolean	useAutoNaming	= false;

	}

}
