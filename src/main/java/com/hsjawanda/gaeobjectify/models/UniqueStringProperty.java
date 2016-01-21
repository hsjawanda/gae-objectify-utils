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
public class UniqueStringProperty<T> {

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

	// public String getValue() {
	// return StringUtils.defaultString(this.value);
	// }
	//
	// public String getNamespace() {
	// return StringUtils.defaultString(this.namespace);
	// }
	//
	// @OnLoad
	// protected void tokenize() {
	// List<String> parts = SplitJoin.split(getId());
	// int size;
	// if (null != parts && (size = parts.size()) > 0) {
	// if (1 == size) {
	// this.value = parts.get(0);
	// } else {
	// this.namespace = parts.get(0);
	// this.value = parts.get(1);
	// }
	// }
	// }

	// protected void setId(String id) {
	// this.id = id;
	// }

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

	// public Key<? extends UniqueStringProperty<T>> getKey() {
	// @SuppressWarnings("unchecked")
	// Key<? extends UniqueStringProperty<T>> key = (Key<? extends UniqueStringProperty<T>>) Key
	// .create(this.getClass(), getId());
	// return key;
	// }

	// public static <T> Builder<T> builder() {
	// return new Builder<>();
	// }
	//
	// public static class Builder<T> {
	// private Builder() {
	// }
	//
	// public T build() {
	// T retVal = new T();
	// return null;
	// }
	// }

}
