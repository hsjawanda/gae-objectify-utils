/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Range;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public final class TasksHelper {

	private static final Logger log = Logger.getLogger(TasksHelper.class.getName());

	public static final String TASK_MAPPING = "tasks.mapping";

	public static final String MAPPING = Config.get(TASK_MAPPING).or("/tasks");

	public static final Range<Long> DELAY_RANGE = Range.closed(1L, 60 * 60 * 1000L);

	protected static final SimpleDateFormat TASK_DATE = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss_SSS");

	static {
		TasksHelper.TASK_DATE.setTimeZone(TimeZone.getTimeZone("IST"));
	}

	private TasksHelper() {
	}

//	@SuppressWarnings("null")
	public static @Nonnull String normalizedTaskName(@Nonnull Class<? extends GaeTask> clazz) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazz.getSimpleName());
	}

	public static String taskUrl(String name) {
		return Constants.pathJoiner.join(MAPPING, name);
	}

	public static void addToQueue(@Nonnull Queue queue, @Nonnull String taskName,
			@Nullable Map<String, String> stringParams, @Nullable Map<String, byte[]> byteParams,
			long delayMillis) {
		StringBuilder name = new StringBuilder(30).append(taskName).append('_')
				.append(TasksHelper.TASK_DATE.format(new Date()));
		TaskOptions taskOptions = TaskOptions.Builder.withUrl(taskUrl(taskName))
				.taskName(name.toString());
		log.info("Adding task with name: '" + name + "' of length " + name.length());
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
		if (DELAY_RANGE.contains(delayMillis)) {
			taskOptions = taskOptions.countdownMillis(delayMillis);
		}
		queue.add(taskOptions);
	}

}
