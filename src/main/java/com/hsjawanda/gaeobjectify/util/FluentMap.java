/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnull;

/**
 * A {@code Map<K, V>}-compatible {@code Map} implementation (some methods are still missing) that is type-safe and
 * allows user to choose the desired key-ordering. However, this class <b><i>does not</i></b> actually implement the
 * {@code Map<K, V>} interface as that would require losing the type safety. The setter methods are fluent where
 * possible.
 *
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 */
public class FluentMap<K, V> {

	private Map<K, V> map;

	private FluentMap() {
	}

	public static <K, V> FluentMap<K, V> create(@Nonnull KeyOrdering order) throws NullPointerException {
		if (null == order)
			throw new NullPointerException("order cannot be null");
		FluentMap<K, V> modMap = new FluentMap<>();
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

	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	public boolean containsValue(V obj) {
		return this.map.containsValue(obj);
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return this.map.entrySet();
	}

	public V get(K key) {
		return this.map.get(key);
	}

	public V getOrDefault(K key, V defaultValue) {
		return this.map.containsKey(key) ? this.map.get(key) : defaultValue;
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	public Set<K> keySet() {
		return this.map.keySet();
	}

	public Map<K, V> map() {
		return this.map;
	}

	public V put(K key, V value) {
		V prevValue = this.map.get(key);
		this.map.put(key, value);
		return prevValue;
	}

	public FluentMap<K, V> putFluent(K key, V value) {
		this.map.put(key, value);
		return this;
	}

	public V putIfAbsent(K key, V value) {
		synchronized (this.map) {
			V prevValue = this.map.get(key);
			if (null == prevValue) {
				this.map.put(key, value);
			}
			return prevValue;
		}
	}

	public V remove(K key) {
		synchronized (this.map) {
			return this.map.remove(key);
		}
	}

	public boolean remove(K key, V value) {
		synchronized (this.map) {
			if (this.map.containsKey(key) && Objects.equals(this.map.get(key), value)) {
				this.map.remove(key);
				return true;
			} else
				return false;
		}
	}

	public V replace(K key, V value) {
		synchronized (this.map) {
			return this.map.containsKey(key) ? this.map.put(key, value) : null;
		}
	}

	public boolean replace(K key, V oldValue, V newValue) {
		synchronized (this.map) {
			if (this.map.containsKey(key) && Objects.equals(this.map.get(key), oldValue)) {
				this.map.put(key, newValue);
				return true;
			} else
				return false;
		}
	}

	public FluentMap<K, V> replaceFluent(K key, V oldValue, V newValue) {
		replace(key, oldValue, newValue);
		return this;
	}

	public int size() {
		return this.map.size();
	}

	public Collection<V> values() {
		return this.map.values();
	}

}
