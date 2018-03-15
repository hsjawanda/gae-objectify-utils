/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.hsjawanda.gaeobjectify.util.Holdall;

/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public final class CollectionHelper {

	private static final int MAX_ADVISABLE_ITEMS = 100;

	private static final Logger log = Logger.getLogger(CollectionHelper.class.getName());

	private CollectionHelper() {
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable) {
		return toImmutableList(iterable, Integer.MAX_VALUE);
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable, int limit) {
		if (null == iterable)
			return ImmutableList.of();
		return toImmutableList(iterable.iterator(), limit);
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable, int offset, int numElements) {
		int size = Integer.MAX_VALUE;
		if (null == iterable)
			return ImmutableList.of();
		if (iterable instanceof Collection) {
			Collection<T> coll = (Collection<T>) iterable;
			if (coll.isEmpty())
				return ImmutableList.of();
			size = coll.size();
		}
		offset = Math.max(0, offset);
		numElements = (int) Holdall.constrainToRange(0, 2000, numElements);
		if (size <= offset)
			return ImmutableList.of();
		int count = 0, upperLimit = offset + numElements;
		ImmutableList.Builder<T> bildr = ImmutableList.builder();
		for (T element : iterable) {
			if (count >= offset && count <= upperLimit) {
				bildr.add(element);
			}
			count++;
		}
		return bildr.build();
	}

	public static <T> ImmutableList<T> toImmutableList(Iterator<T> iterator, int limit) {
		if (null == iterator)
			return ImmutableList.of();
		Builder<T> bildr = ImmutableList.builder();
		for (int i = 0; iterator.hasNext() && i < limit; i++) {
			bildr.add(iterator.next());
		}
		return bildr.build();

	}

	public static <T> List<T> toList(Iterable<T> iterable) {
		return toList(iterable, Integer.MAX_VALUE);
	}

	public static <T> List<T> toList(Iterable<T> iterable, int maxItems) {
		if (null == iterable)
			return Collections.emptyList();
		return toList(iterable.iterator(), maxItems);
	}

	public static <T> List<T> toList(Iterator<T> iterator) {
		return toList(iterator, Integer.MAX_VALUE);
	}

	public static <T> List<T> toList(Iterator<T> iterator, int maxItems) throws IllegalArgumentException {
		if (null == iterator)
			return Collections.emptyList();
		if (maxItems < 1)
			throw new IllegalArgumentException("maxItems must be >= 1.");
		if (maxItems > MAX_ADVISABLE_ITEMS) {
			log.warning("maxItems = " + maxItems + ". It is advisable to not use a value greater than "
					+ MAX_ADVISABLE_ITEMS + ".");
		}
		List<T> retList = new ArrayList<>(maxItems);
		while (--maxItems >= 0 && iterator.hasNext()) {
			retList.add(iterator.next());
		}
		return retList;
	}

}
