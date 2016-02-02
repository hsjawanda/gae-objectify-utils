/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.SaveException;
import com.googlecode.objectify.cmd.Query;
import com.hsjawanda.gaeobjectify.util.PagingData;


/**
 * @author harsh.deep
 *
 */
public class ObjectifyDao<T> {

	private Class<T> cls;

	protected final Logger log;

	public ObjectifyDao(Class<T> cls) {
		this.cls = cls;
		this.log = Logger.getLogger(ObjectifyDao.class.getSimpleName() + ":" + this.cls.getName());
	}

	public Optional<T> getByKey(Key<T> key) {
		if (null == key)
			return Optional.absent();
		return Optional.fromNullable(ofy().load().key(key).now());
	}

	public Optional<T> getByWebKey(String webKey) {
		if (isBlank(webKey))
			return Optional.absent();
		Key<T> key = Key.create(webKey);
		return getByKey(key);
	}

	public Optional<T> getByRef(Ref<T> entityRef) {
		if (null == entityRef)
			return Optional.absent();
		return Optional.fromNullable(ofy().load().ref(entityRef).now());
	}

	public Map<Key<T>, T> getByRefs(Iterable<Ref<T>> entities) {
		if (null == entities)
			return Collections.emptyMap();
		return ofy().load().refs(entities);
	}

	public Optional<T> getById(String id) {
		if (isBlank(id))
			return Optional.absent();
		Key<T> key = null;
		try {
			key = Key.create(this.cls, id);
		} catch (Exception e) {
			this.log.warning("Error creating key: " + e.getMessage());
			return Optional.absent();
		}
		return getByKey(key);
	}

	public Map<String, T> getByStringIds(Iterable<String> ids) {
		if (null == ids)
			return Collections.emptyMap();
		return ofy().load().type(this.cls).ids(ids);
	}

	public Optional<T> getById(long id) {
		Key<T> key = null;
		try {
			key = Key.create(this.cls, id);
			return getByKey(key);
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Unexpected exception getting Optional<"
					+ this.cls.getSimpleName() + "> from long id", e);
		}
		return Optional.absent();
	}

	public Map<Long, T> getByLongIds(Iterable<Long> ids) {
		if (null == ids)
			return Collections.emptyMap();
		return ofy().load().type(this.cls).ids(ids);
	}

	public Key<T> getKeyFromPojo(T pojo) {
		Key<T> key = null;
		try {
			key = Key.create(pojo);
		} catch (NullPointerException npe) {
			this.log.info("Reason: " + npe.getMessage());
		} catch (Exception e) {
			this.log.log(Level.SEVERE,
					"Unexpected exception creating Key<" + this.cls.getSimpleName() + "> from POJO",
					e);
		}
		return key;
	}

	public Key<T> getKeyFromWebKey(String webKey) {
		Key<T> key = null;
		try {
			key = Key.create(webKey);
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Unexpected exception getting Key<"
					+ this.cls.getSimpleName() + "> from webKey", e);
		}
		return key;
	}

	public String getWebKeyFor(T pojo) {
		Key<T> key = getKeyFromPojo(pojo);
		return (null == key) ? EMPTY : key.toWebSafeString();
	}

	public Key<T> keyFor(String id) {
		Key<T> key = null;
		try {
			key = Key.create(this.cls, id);
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Error creating Key<" + this.cls.getSimpleName() + ">...",
					e);
		}
		return key;
	}

	public List<T> getAll() {
		return ofy().load().type(this.cls).list();
	}

	public T getByProjection(Class<T> cls, String... propNames) {
		ofy().load().type(cls).project(propNames);
		return null;
	}

	public Optional<Key<T>> saveEntity(T entity) throws SaveException {
		Optional<Result<Key<T>>> result = saveEntityAsync(entity);
		if (result.isPresent())
			return Optional.fromNullable(result.get().now());
		else
			return Optional.absent();
	}

	public Optional<Result<Key<T>>> saveEntityAsync(T entity) throws SaveException {
		if (entity instanceof Optional)
			throw new IllegalArgumentException(
					"Can't save an Optional. Instead use entity.get() in call to this method.");
		if (null == entity)
			return Optional.absent();
		try {
			return Optional.fromNullable(ofy().save().entity(entity));
		} catch (Exception e) {
			if (e instanceof SaveException) {
				this.log.warning("Failed to save entity because: " + e.getMessage());
			} else {
				this.log.log(Level.WARNING, "Failed to save entity.", e);
			}
			throw e;
		}
	}

	public boolean saveEntities(@SuppressWarnings("unchecked") T... entities) {
		if (null == entities)
			return true;
		return saveEntities(Arrays.asList(entities));
	}

	public boolean saveEntities(Iterable<T> entities) {
		try {
			ofy().save().entities(entities).now();
			return true;
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Exception saving entities...", e);
			return false;
		}
	}

	public void deferredSaveEntities(@SuppressWarnings("unchecked") T... ts) {
		ofy().defer().save().entities(ts);
	}

	public void deferredSaveEntities(Iterable<T> ts) {
		ofy().defer().save().entities(ts);
	}

	public void deferredSaveEntity(T t) {
		if (null != t) {
			ofy().defer().save().entity(t);
		}
	}

	public void deferredDeleteEntities(@SuppressWarnings("unchecked") T... ts) {
		ofy().defer().delete().entities(ts);
	}

	public void deferredDeleteEntities(Iterable<T> ts) {
		ofy().defer().delete().entities(ts);
	}

	public void deferredDeleteEntity(T t) {
		if (null != t) {
			ofy().defer().delete().entity(t);
		}
	}

	public void deferredDeleteById(String id) {
		if (isNotBlank(id)) {
			Key<T> key = keyFor(id);
			if (null != key) {
				ofy().defer().delete().key(key);
			}
		}
	}

	public void deferredDeleteByKeys(Iterable<Key<T>> keys) {
		ofy().defer().delete().keys(keys);
	}

	public void deferredDeleteByKey(Key<T> key) {
		if (null != key) {
			ofy().defer().delete().key(key);
		}
	}

	public void deferredDeleteByRef(Ref<T> ref) {
		if (null != ref) {
			deferredDeleteByKey(ref.getKey());
		}
	}

	public boolean deleteEntity(T entity) {
		if (null == entity)
			return true;
		Key<T> key = getKeyFromPojo(entity);
		if (null == key)
			return true;
		else
			return deleteByKey(key);
	}

	public boolean deleteByKey(Key<T> key) {
		if (null == key)
			return true;
		try {
			ofy().delete().key(key).now();
			return true;
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Failed to delete entity: " + key + " ...", e);
			return false;
		}
	}

	public boolean deleteByRef(Ref<T> ref) {
		if (null == ref)
			return true;
		return deleteByKey(ref.key());
	}

	public boolean deleteByWebKey(String webKey) {
		if (isBlank(webKey))
			return true;
		return deleteByKey(Key.<T> create(webKey));
	}

	public boolean deleteEntityAsync(T pojo) {
		if (null == pojo)
			return true;
		Result<Void> result = ofy().delete().entity(pojo);
		return (result != null);
	}

	public boolean deleteEntities(Iterable<T> entities) {
		try {
			ofy().delete().entities(entities);
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Exception while deleting entities...", e);
			return false;
		}
		return true;
	}

	public boolean deleteEntities(@SuppressWarnings("unchecked") T... entities) {
		return deleteEntities(Arrays.asList(entities));
	}

	public List<T> queryByType(Filter... filters) {
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		return qry.list();
	}

	public Ref<T> getNullableRefFromPojo(T entity) {
		Ref<T> ref = null;
		if (null != entity /* && Check.idNotNull(entity) */) {
			try {
				ref = Ref.create(entity);
			} catch (NullPointerException e) {
				this.log.warning("NullPointerException because: " + e.getMessage());
			} catch (Exception e) {
				this.log.log(Level.WARNING,
						"Error creating a Ref<" + this.cls.getSimpleName() + "> to a POJO", e);
			}
		}
		return ref;
	}

	public void getPaginatedEntities(PagingData<T> pd, Filter... filters) {
		checkNotNull(pd, "The PagingData object can't be null.");
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		if (pd.isGenTotalResults()) {
			qry = qry.limit(pd.getCountLimit());
			pd.setTotalResults(qry.count());
		}
		qry = qry.offset(pd.getOffset()).limit(pd.getItemsPerPage());
		pd.setResults(qry.list());
	}

}
