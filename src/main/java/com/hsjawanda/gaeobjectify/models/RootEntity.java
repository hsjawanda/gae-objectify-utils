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

	// public boolean saveOld() throws SaveException {
	// Optional<Key<RootEntity>> optKey = Optional.absent();
	// try {
	// optKey = GaeDataUtil.saveEntity(this);
	// } catch (Exception e) {
	// log.log(Level.WARNING, "Failed to save this entity.", e);
	// throw e;
	// }
	// return optKey.isPresent();
	// }

	// @SuppressWarnings("unchecked")
	// public Optional<Key<T>> save() throws SaveException {
	// Key<T> key = null;
	// try {
	// key = (Key<T>) ofy().save().entity(this).now();
	// } catch (SaveException se) {
	// log.log(Level.WARNING, "Couldn't save this entity", se);
	// throw se;
	// }
	// return Optional.of(key);
	// }

	// @SuppressWarnings("unchecked")
	// public Optional<Result<Key<T>>> saveAsync() throws SaveException {
	// Result<Key<T>> result = null;
	// try {
	// result = ofy().save().entity((T) this);
	// } catch (SaveException se) {
	// log.log(Level.WARNING, "Couldn't async save this entity", se);
	// throw se;
	// }
	// return Optional.of(result);
	// }

//	public void delete() {
//		GaeDataUtil.deleteEntity(this);
//	}
//
//	public static <T extends RootEntity<T>> Optional<T> getByKey(Key<T> key) {
//		try {
//			T entity = ofy().load().key(key).now();
//			return Optional.fromNullable(entity);
//		} catch (Exception e) {
//			log.log(Level.SEVERE, "Exception while trying to get by Key", e);
//		}
//		return Optional.absent();
//	}
//
//	public static <T extends RootEntity<T>> Optional<T> getById(Class<T> cls, String id) {
//		Key<T> key = Key.create(cls, id);
//		T entity = ofy().load().key(key).now();
//		return Optional.fromNullable(entity);
//	}
//
//	public static <T> Optional<T> getByWebKey(String webKey) {
//		try {
//			return GaeDataUtil.getByWebKey(webKey);
//		} catch (Exception e) {
//			log.log(Level.WARNING, "Exception while trying to get by webKey", e);
//		}
//		return Optional.absent();
//	}
//
//	@JsonProperty("uniqueId")
//	public String getWebKey() {
//		if (Check.idNotNull(this))
//			return GaeDataUtil.getKeyFromPojo(this).toWebSafeString();
//		return null;
//	}

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
