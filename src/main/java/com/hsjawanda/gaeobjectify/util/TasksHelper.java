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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.CaseFormat;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public final class TasksHelper {

	public static final String TASK_MAPPING = "tasks.mapping";

	public static final String MAPPING = Config.get(TASK_MAPPING).or("/tasks");

	public static final long MAX_DELAY = 60 * 1000L;

	protected static final SimpleDateFormat TASK_DATE = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss_SSS");

	static {
		TasksHelper.TASK_DATE.setTimeZone(TimeZone.getTimeZone("IST"));
	}

	private TasksHelper() {
	}

	public static String normalizedTaskName(Class<? extends GaeTask> clazz) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazz.getSimpleName());
	}

	public static String taskUrl(String name) {
		return Constants.pathJoiner.join(MAPPING, name);
	}

	public static void addToQueue(@Nonnull Queue queueName, @Nonnull String taskName,
			@Nullable Map<String, String> stringParams, @Nullable Map<String, byte[]> byteParams,
			long delayMillis) {
		TaskOptions taskOptions = TaskOptions.Builder.withUrl(taskUrl(taskName))
				.taskName(taskName + "_" + TasksHelper.TASK_DATE.format(new Date()));
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
		if (delayMillis > 0) {
			taskOptions = taskOptions.countdownMillis(Math.min(MAX_DELAY, delayMillis));
		}
		queueName.add(taskOptions);
	}

}
