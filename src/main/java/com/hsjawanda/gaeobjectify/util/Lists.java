/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public final class Lists {

	private static Logger LOG;


	private Lists() {
	}

	public static <T> T get(List<T> list) {
		return get(list, 0, null);
	}

	public static <T> T get(List<T> list, int position) {
		return get(list, position, null);
	}

	public static <T> T get(List<T> list, int position, T defaultValue) {
		if (null != list && position >= 0 && position < list.size())
			return list.get(position);
		return defaultValue;
	}

	@SuppressWarnings("unused")
	private static Logger log() {
		if (null == LOG) {
			LOG = Logger.getLogger(Lists.class.getName());
		}
		return LOG;
	}

}
