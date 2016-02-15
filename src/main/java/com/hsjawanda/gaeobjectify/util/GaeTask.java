/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.Queue;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public interface GaeTask {

	void performTask(HttpServletRequest req);

	void addToQueue(@Nonnull Queue queueName, long delayMillis);

}
