/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.text.SimpleDateFormat;


/**
 * @author harsh.deep
 *
 */
public class Defaults {

	public static final double geoSearchRadius = 16093;

	public static final int pgNum = 1;

	public static final int itemsPerPage = 10;

	public static final SimpleDateFormat dateFmt = new SimpleDateFormat(
			"EEE dd MMM yyyy HH:mm:ss.SSS zzz");

	private Defaults() {
	}

	/**
	 * Return a default value if the input object is {@code null}.
	 *
	 * @param obj the input object
	 * @param defaultValue the default value to use
	 * @return {@code obj} if it is not-null, {@code defaultValue} otherwise
	 */
	public static <T> T or(T obj, T defaultValue) {
		return null == obj ? defaultValue : obj;
	}

	public static int asInt(String intString, int defaultVal) {
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	public static int asInt(Integer input, int defaultVal) {
		return null == input ? defaultVal : input.intValue();
	}
}
