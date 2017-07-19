/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.annotation.Nonnull;


/**
 * @author harsh.deep
 *
 */
public class Defaults {

	private static Logger LOG = Logger.getLogger(Defaults.class.getName());

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
//	@SuppressWarnings("null")
	@Nonnull
	public static <T> T or(T obj, @Nonnull T defaultValue) {
		return null == obj ? defaultValue : obj;
	}

	public static int asInt(String intString, int defaultVal) {
		if (null == intString)
			return defaultVal;
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			LOG.info(e.getMessage());
			return defaultVal;
		}
	}

	public static int asInt(Integer input, int defaultVal) {
		return null == input ? defaultVal : input.intValue();
	}
}
