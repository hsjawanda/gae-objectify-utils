/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import com.hsjawanda.gaeobjectify.collections.KeyGenerator;
import com.hsjawanda.gaeobjectify.models.StringIdEntity;
import com.hsjawanda.gaeobjectify.util.Holdall;

/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class IdKeyGen<V extends StringIdEntity> implements KeyGenerator<String, V> {

	private IdKeyGen() {
	}

	public static <V extends StringIdEntity> IdKeyGen<V> instance(Class<V> cls) {
		Holdall.checkIfObjectifyEntity(cls);
		return new IdKeyGen<V>();
	}

	@Override
	public String keyFor(V value) {
		return null == value ? null : value.getId();
	}

}
