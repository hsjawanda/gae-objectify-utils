/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public interface GaeTask {

	void performTask(HttpServletRequest req);

//	void addToQueue(@Nonnull Queue queueName, long delayMillis);

}
