/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import lombok.AccessLevel;
import lombok.Builder;
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

	@lombok.NonNull
	@Setter(AccessLevel.NONE)
	private Class<T>						taskClass;

	private String							nameSuffix;

	private String							queueName;

	private long							delayMillis	= 0;

	@Singular()
	@Setter(AccessLevel.NONE)
	private Map<String, String>				strParams	= new HashMap<>(2);

	@Singular
	@Setter(AccessLevel.NONE)
	private Map<String, byte[]>				byteParams	= new HashMap<>(1);

	public static final Range<Long> DELAY_RANGE = Range.closed(1L, 60 * 60 * 1000L);

	public static final String				MAPPING		= Config.get("tasks.mapping").or("/task");

	protected static final SimpleDateFormat	TASK_DATE	= new SimpleDateFormat(
																"yyyy-MM-dd_HH-mm-ss_SSS");

	static {
		TasksHelper.TASK_DATE.setTimeZone(TimeZone.getTimeZone("IST"));
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
		StringBuilder taskName = new StringBuilder(30).append(normalizedTaskName).append('_')
				.append(defaultString(this.nameSuffix)).append('_')
				.append(TasksHelper.TASK_DATE.format(new Date()));
		TaskOptions taskOptions = TaskOptions.Builder.withUrl(taskUrl(normalizedTaskName))
				.taskName(taskName.toString());
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

}
