/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class IterablesUtil {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(IterablesUtil.class.getName());

	private IterablesUtil() {}

	public static <T> List<T> toList(Iterable<T> iterable) {
		if (null == iterable)
			return Collections.emptyList();
		List<T> retList = new LinkedList<>();
		for (T item : iterable) {
			retList.add(item);
		}
		return retList;
	}

}
