/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

import com.hsjawanda.gaeobjectify.models.UniqueStringProperty;


/**
 * @author harsh.deep
 *
 */
public class IndexProperty<T extends UniqueStringProperty<K>, K> implements Work<Key<T>> {

	private T property;

	private Class<T> cls;

	private K referenced;

	public IndexProperty(Class<T> clazz, T property, K referenced) {
		this.property = property;
		this.cls = clazz;
		this.referenced = referenced;
	}

	@Override
	public Key<T> run() {
		Key<T> key = null;
		T entity = ofy().load().type(this.cls).id(this.property.getId()).now();
		if (null == entity) {
			if (Check.idNotNull(this.referenced)) {
				this.property.setReferenced(this.referenced);
			}
			key = ofy().save().entity(this.property).now();
		}
		return key;
	}

}
