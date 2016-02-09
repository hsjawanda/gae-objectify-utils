/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public abstract class GaeTask {

	public static final String TASK_MAPPING = "tasks.mapping";

	protected String mapping = "/tasks";

	protected String name;

	protected String taskUrl;

	protected static final SimpleDateFormat TASK_DATE = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss_SSS");

	static {
		TASK_DATE.setTimeZone(TimeZone.getTimeZone("IST"));
	}

	protected void init(String taskName) {
		this.name = taskName;
	}

	public abstract void performTask(HttpServletRequest req);

	public String taskUrl() {
		if (null == this.taskUrl) {
			this.taskUrl = Constants.pathJoiner.join(this.mapping, this.name);
		}
		return this.taskUrl;
	}

	public void addToQueue(@Nonnull Queue queueName, @Nullable Map<String, String> stringParams,
			@Nullable Map<String, byte[]> byteParams) {
		TaskOptions taskOptions = TaskOptions.Builder.withUrl(taskUrl())
				.taskName(this.name + "_" + TASK_DATE.format(new Date()));
		if (null != stringParams) {
			for (String key : stringParams.keySet()) {
				taskOptions = taskOptions.param(key, stringParams.get(key));
			}
		}
		if (null != byteParams) {
			for (String key : byteParams.keySet()) {
				taskOptions = taskOptions.param(key, byteParams.get(key));
			}
		}
		queueName.add(taskOptions);
	}

	public void addToQueue(@Nonnull String queueName, @Nullable Map<String, String> stringParams,
			@Nullable Map<String, byte[]> byteParams) {
		Queue q = QueueFactory.getQueue(queueName);
		if (null != q) {
			addToQueue(q, stringParams, byteParams);
		}
	}

}
