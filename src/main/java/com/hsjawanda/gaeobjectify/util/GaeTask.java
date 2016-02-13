/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.Queue;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public interface GaeTask {

	void performTask(HttpServletRequest req);

	void addToQueue(@Nonnull Queue queueName, @Nonnull Map<String, String> stringParams,
			@Nullable Map<String, byte[]> byteParams, long delayMillis);

}
