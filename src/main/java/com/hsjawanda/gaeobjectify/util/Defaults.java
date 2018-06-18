/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;


/**
 * @author harsh.deep
 *
 */
public class Defaults {

	public static final SimpleDateFormat dateFmt = new SimpleDateFormat(
			"EEE dd MMM yyyy HH:mm:ss.SSS zzz");

	public static final double geoSearchRadius = 16093;

	public static final int itemsPerPage = 10;

	public static final int pgNum = 1;

	private static Logger LOG = Logger.getLogger(Defaults.class.getName());

	private Defaults() {
	}

	public static int asInt(Integer input, int defaultVal) {
		return null == input ? defaultVal : input.intValue();
	}

	public static int asInt(Object obj, int defaultValue) {
		if (null != obj) {
			try {
				return ((Integer) obj).intValue();
			} catch (Exception e) {
				LOG.log(Level.WARNING, "Error getting int value from Object", e);
			}
		}
		return defaultValue;
	}

	public static int asInt(String intString, int defaultVal) {
		if (null == intString)
			return defaultVal;
		try {
			return Integer.parseInt(intString);
		} catch (Exception e) {
			if (e instanceof NumberFormatException) {
				LOG.warning(e.getMessage());
			} else {
				LOG.log(Level.WARNING, "Couldn't parse <" + intString + "> as a valid number. Returned default.", e);
			}
			return defaultVal;
		}
	}

	public static long asLong(Long value, long defaultVal) {
		return null == value ? defaultVal : value.longValue();
	}

	public static long asLong(String longString, long defaultVal) {
		if (null == longString)
			return defaultVal;
		try {
			return Long.parseLong(longString);
		} catch (Exception e) {
			if (e instanceof NumberFormatException) {
				LOG.warning(e.getMessage());
			} else {
				LOG.log(Level.WARNING, "Couldn't parse <" + longString + "> as a valid number. Returned default.", e);
			}
			return defaultVal;
		}
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
}
