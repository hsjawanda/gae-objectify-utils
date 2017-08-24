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

import com.google.api.client.util.Lists;
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
		List<T> retList = new ArrayList<>();
		for (T item : iterable) {
			retList.add(item);
		}
		return retList;
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable, int offset,
			int numElements) {
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
		numElements = Math.max(0, numElements);
		numElements = Math.min(2000, numElements);
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

	public static <T> List<T> toList(Iterator<T> iterator) {
		if (null == iterator)
			return Collections.emptyList();
		List<T> retList = Lists.newArrayList();
		while (iterator.hasNext()) {
			retList.add(iterator.next());
		}
		return retList;
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

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable, int limit) {
		if (null == iterable)
			return ImmutableList.of();
		return toImmutableList(iterable.iterator(), limit);
	}

	public static <T> ImmutableList<T> toImmutableList(Iterable<T> iterable) {
		return toImmutableList(iterable, Integer.MAX_VALUE);
	}

}
