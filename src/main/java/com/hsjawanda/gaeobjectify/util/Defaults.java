/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

/**
 * @author harsh.deep
 *
 */
public class Defaults {

	public static final double geoSearchRadius = 16093;

	public static final int pgNum = 1;

	public static final int itemsPerPage = 10;

	private Defaults() {
	}

	public static <T> T orDefault(T obj, T defaultValue) {
		return null == obj ? defaultValue : obj;
	}
}
