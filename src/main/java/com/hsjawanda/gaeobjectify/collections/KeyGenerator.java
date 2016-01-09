/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

/**
 * @author harsh.deep
 *
 */
public interface KeyGenerator<K, V> {

	K keyFor(V value);

}
