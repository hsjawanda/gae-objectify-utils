/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import com.hsjawanda.gaeobjectify.collections.KeyGenerator;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class TagStoreKeyGen implements KeyGenerator<String, Tag> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hsjawanda.gaeobjectify.collections.KeyGenerator#keyFor(java.lang.Object)
	 */
	@Override
	public String keyFor(Tag value) {
		return null == value ? null : value.name();
	}

}
