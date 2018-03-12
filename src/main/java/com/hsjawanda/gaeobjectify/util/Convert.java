/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.hsjawanda.gaeobjectify.collections.KeyGenerator;


/**
 * Utilities for doing conversions.
 *
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 */
public class Convert {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Convert.class.getName());

	protected Convert() {
	}

	/**
	 * Convert a {@code Map} to a {@code List}, returning the {@code values} in the {@code Map} in
	 * the returned list.
	 *
	 * @param map
	 *            the {@code map} to use.
	 * @param existingList
	 *            use this {@code List} instead of allocating a new one (it will be {@code clear()}
	 *            -ed). If {@code null}, a new {@code List} is allocated.
	 * @return the {@code values} in the {@code Map} as a list.
	 */
	public static <K, V> List<V> mapToList(Map<K, V> map, List<V> existingList) {
		List<V> retVal = existingList;
		if (null != map && !map.isEmpty()) {
			if (null != retVal) {
				retVal.clear();
				if (retVal instanceof ArrayList) {
					((ArrayList<V>) retVal).ensureCapacity(map.size());
				}
			} else {
				retVal = new ArrayList<>(map.size());
			}
			for (K key : map.keySet()) {
				retVal.add(map.get(key));
			}
		}
		return retVal;
	}

	/**
	 * Same as {@link #mapToList(Map, List)}, except that a new {@code List} is always allocated.
	 *
	 * @see #mapToList(Map, List)
	 *
	 * @param map
	 * @return
	 */
	public static <K, V> List<V> mapToList(Map<K, V> map) {
		return mapToList(map, null);
	}

	/**
	 * The reverse of {@link #mapToList(Map, List)}. A {@link KeyGenerator} has to be supplied to
	 * generate the keys corresponding to the elements of {@code list}.
	 *
	 * @param list
	 *            the {@link List} to use.
	 * @param keyGen
	 *            the {@link KeyGenerator} to use for generating keys corresponding to the
	 *            {@code values} in {@code list}. Cannot be {@code null}.
	 * @param existingMap
	 *            use this {@link Map} instead of allocating a new one (it will be {@code clear()}
	 *            -ed). If {@code null}, a new {@code Map} is allocated.
	 * @return the converted {@code Map}.
	 * @throws NullPointerException
	 *             if {@code keyGen} is {@code null}.
	 */
	public static <K, V> Map<K, V> listToMap(List<V> list, KeyGenerator<K, V> keyGen,
			Map<K, V> existingMap) throws NullPointerException {
		checkNotNull(keyGen, "keyGen" + Constants.NOT_NULL);
		Map<K, V> retVal = existingMap;
		if (null != list && !list.isEmpty()) {
			if (null != retVal) {
				retVal.clear();
			} else {
				retVal = new LinkedHashMap<>(list.size());
			}
			for (V value : list) {
				retVal.put(keyGen.keyFor(value), value);
			}
		}
		return retVal;
	}

	/**
	 * Same as {@link #listToMap(List, KeyGenerator, Map)}, except that a new {@code Map} is always
	 * allocated.
	 *
	 * @see #listToMap(List, KeyGenerator, Map)
	 *
	 * @param list
	 * @param keyGen
	 * @return
	 */
	public static <K, V> Map<K, V> listToMap(List<V> list, KeyGenerator<K, V> keyGen) {
		return listToMap(list, keyGen, null);
	}

	@Nonnull
	public static <T> Optional<T> toJavaOptional(com.google.common.base.Optional<T> guavaOptional) {
		return null != guavaOptional ? Optional.ofNullable(guavaOptional.orNull()) : Optional.<T>empty();
	}

}
