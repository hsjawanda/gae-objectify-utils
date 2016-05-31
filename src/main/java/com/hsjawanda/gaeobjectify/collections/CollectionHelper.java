/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class CollectionHelper {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CollectionHelper.class.getName());

	private CollectionHelper() {}

	public static <T> List<T> toList(Iterable<T> iterable) {
		if (null == iterable)
			return Collections.emptyList();
		List<T> retList = new LinkedList<>();
		for (T item : iterable) {
			retList.add(item);
		}
		return retList;
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable, int limit) {
		Iterator<T> itr = iterable.iterator();
		Builder<T> bildr = ImmutableList.builder();
		for (int i = 0; itr.hasNext() && i < limit; i++) {
			bildr.add(itr.next());
		}
		return bildr.build();
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable) {
		return toImmutableList(iterable, Integer.MAX_VALUE);
	}

}
