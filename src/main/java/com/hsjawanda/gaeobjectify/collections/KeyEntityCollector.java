/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

import com.googlecode.objectify.Key;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;


/**
 * @author harsh.deep
 *
 */
public class KeyEntityCollector<T> extends EntityCollector<Key<T>, T> {

	public KeyEntityCollector(Class<T> cls) {
		super(cls);
	}

	public void addEntity(T entity) {
		if (null == entity)
			return;
		super.addEntity(GaeDataUtil.getKeyFromPojo(entity), entity);
	}

	public T removeEntity(Key<T> entityKey) {
		return super.removeEntityByKey(entityKey);
	}

	public T removeEntity(String webKey) {
		return this.removeEntity(GaeDataUtil.<T> getKeyFromWebKey(webKey));
	}

	public T removeEntity(T entity) {
		return this.removeEntity(GaeDataUtil.getKeyFromPojo(entity));
	}

}
