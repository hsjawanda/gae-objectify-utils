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

	public static <T> T orDefault(T obj, T defaultValue) {
		return null == obj ? defaultValue : obj;
	}
}
