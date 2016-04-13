/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import java.util.Date;
import java.util.logging.Logger;

import com.googlecode.objectify.annotation.OnSave;


/**
 * @author harsh.deep
 * @param <T>
 *
 */
public abstract class RootEntity<T extends RootEntity<T>> {

	protected static final Logger log = Logger.getLogger(RootEntity.class.getName());

//	@JsonIgnore
	protected Date dateCreated = new Date();

//	@JsonIgnore
	protected Date dateLastModified;

	@OnSave
	protected void saveActions() {
		this.dateLastModified = new Date();
	}

	/**
	 * @return the dateLastModified
	 */
	public Date getDateLastModified() {
		return this.dateLastModified;
	}

	/**
	 * @return the dateCreated
	 */
//	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT")
	public Date getDateCreated() {
		return this.dateCreated;
	}

//	@JsonIgnore
	protected int version = 1;

	/**
	 * @return the version
	 */
	public int getVersion() {
		return this.version;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.version;
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RootEntity))
			return false;
		@SuppressWarnings("rawtypes")
		RootEntity other = (RootEntity) obj;
		if (this.version != other.version)
			return false;
		return true;
	}
}
