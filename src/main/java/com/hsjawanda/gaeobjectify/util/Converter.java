/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hsjawanda.gaeobjectify.collections.KeyGenerator;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Converter {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Converter.class.getName());

	protected Converter() {
	}

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

	public static <K, V> List<V> mapToList(Map<K, V> map) {
		return mapToList(map, null);
	}

	public static <K, V> Map<K, V> listToMap(List<V> list, KeyGenerator<K, V> keyGen,
			Map<K, V> existingMap) throws NullPointerException {
		checkNotNull(keyGen, "keyGen" + Constants.notNull);
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

	public static <K, V> Map<K, V> listToMap(List<V> list, KeyGenerator<K, V> keyGen) {
		return listToMap(list, keyGen, null);
	}

}
