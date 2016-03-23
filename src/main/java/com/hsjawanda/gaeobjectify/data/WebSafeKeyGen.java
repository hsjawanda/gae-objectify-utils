/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import com.hsjawanda.gaeobjectify.collections.KeyGenerator;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 * @param <V>
 */
public class WebSafeKeyGen<V> implements KeyGenerator<String, V> {

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hsjawanda.gaeobjectify.collections.KeyGenerator#keyFor(java.lang.Object)
	 */
	@Override
	public String keyFor(V value) {
		return GaeDataUtil.getWebKeyFromPojo(value);
	}

}
