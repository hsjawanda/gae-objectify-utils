/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class FluentMap<T> {

	@SuppressWarnings("unused")
	private static final String NEWLINE = System.lineSeparator();

	@SuppressWarnings("unused")
	private static final String SEPARATOR = " : ";

	private Map<String, Object> map;

	private FluentMap() {
	}

	public static <T> FluentMap<T> create(@Nonnull KeyOrdering order) throws NullPointerException {
		if (null == order)
			throw new NullPointerException("order cannot be null");
		FluentMap<T> modMap = new FluentMap<>();
		switch (order) {
		case NONE:
			modMap.map = new HashMap<>();
			break;
		case IN_ORDER:
			modMap.map = new LinkedHashMap<>();
			break;
		case SORTED:
			modMap.map = new TreeMap<>();
			break;
		}
		return modMap;
	}

	public boolean containsKey(String key) {
		return this.map.containsKey(key);
	}

	public boolean containsValue(Object obj) {
		return this.map.containsValue(obj);
	}

	public T get(String key) {
		@SuppressWarnings("unchecked")
		T value = (T) this.map.get(key);
		return value;
	}

	public Set<String> keySet() {
		return this.map.keySet();
	}

	public FluentMap<T> put(String key, T value) {
		this.put(key, value);
		return this;
	}

	public int size() {
		return this.map.size();
	}

	public Collection<Object> values() {
		return this.map.values();
	}

}
