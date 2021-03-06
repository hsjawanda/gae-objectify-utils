/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.hsjawanda.gaeobjectify.models.UniqueStringProperty;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class IndexProperty<T extends UniqueStringProperty<K>, K> implements Work<Key<T>> {

	private static Logger log = Logger.getLogger(IndexProperty.class.getName());

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
		T entity = null;
		try {
			entity = ofy().load().type(this.cls).id(this.property.getId()).now();
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to retrieve " + this.cls.getSimpleName() + " by key '"
					+ this.property.getId() + "'. Reason: " + e.getMessage());
		}
		if (null == entity) {
			if (Check.idNotNull(this.referenced)) {
				this.property.setReferenced(this.referenced);
			}
			key = ofy().save().entity(this.property).now();
			log.info("Entity " + this.property + " saved.");
		} else {
			log.info("Entity with id '" + this.property.getId() + "' already exists.");
		}
		return key;
	}

}
