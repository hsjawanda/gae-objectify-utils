/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import com.google.common.base.Optional;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


/**
 * @author harsh.deep
 *
 */
@Entity
public abstract class UniqueStringProperty<T> {

	@Id
	private String id;

	private Ref<T> reference;

	protected UniqueStringProperty() {
	}

	public UniqueStringProperty(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the reference
	 */
	public Optional<T> getReferenced() {
		if (null == this.reference)
			return Optional.absent();
		return Optional.of(this.reference.get());
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReferenced(T reference) {
		this.reference = (null == reference) ? null : Ref.create(reference);
	}

}
