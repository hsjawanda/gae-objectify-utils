/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import com.googlecode.objectify.Key;
import com.hsjawanda.gaeobjectify.collections.KeyGenerator;
import com.hsjawanda.gaeobjectify.util.Holdall;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 * @param <V>
 */
public class WebSafeKeyGen<V> implements KeyGenerator<String, V> {

	private WebSafeKeyGen() {
	}

	public static <V> WebSafeKeyGen<V> instance(Class<V> cls) {
		Holdall.checkIfObjectifyEntity(cls);
		return new WebSafeKeyGen<V>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hsjawanda.gaeobjectify.collections.KeyGenerator#keyFor(java.lang.Object)
	 */
	@Override
	public String keyFor(V value) {
		if (null == value)
			return null;
		return Key.create(value).toWebSafeString(); // Let exception be thrown if there's error
	}

}
