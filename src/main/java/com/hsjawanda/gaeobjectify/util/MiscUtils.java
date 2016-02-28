/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public final class MiscUtils {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(MiscUtils.class.getName());

	private MiscUtils() {
	}

	public static String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter(500);
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
