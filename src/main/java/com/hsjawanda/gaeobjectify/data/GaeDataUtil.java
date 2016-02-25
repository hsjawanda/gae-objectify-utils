/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;
import com.hsjawanda.gaeobjectify.models.GaeEntity;
import com.hsjawanda.gaeobjectify.util.PagingData;


/**
 * @author Harsh.Deep
 *
 */
public class GaeDataUtil {

	private static final Logger log = Logger.getLogger(GaeDataUtil.class.getName());

	private GaeDataUtil() {
	}

	public static <T> Optional<T> getByKey(Key<T> key) {
		return Optional.fromNullable(ofy().load().key(key).now());
	}

	public static <T> Optional<T> getByWebKey(String webKey) {
		if (isBlank(webKey))
			return Optional.absent();
		Key<T> key = Key.create(webKey);
		return getByKey(key);
	}

	public static <T> Optional<T> getByRef(Ref<T> entityRef) {
		if (null == entityRef)
			return Optional.absent();
		return Optional.fromNullable(ofy().load().ref(entityRef).now());
	}

	public static <T> Map<Key<T>, T> getByRefs(Iterable<Ref<T>> entities) {
		if (null == entities)
			return Collections.emptyMap();
		return ofy().load().refs(entities);
	}

	public static <T> Optional<T> getById(Class<T> cls, String id) {
		Key<T> key = Key.create(cls, id);
		return getByKey(key);
	}

	public static <T> Optional<T> getById(Class<T> cls, long id) {
		Key<T> key = null;
		try {
			key = Key.create(cls, id);
			return getByKey(key);
		} catch (Exception e) {
			log.log(Level.WARNING, "Unexpected exception getting Optional<T> from long id", e);
		}
		return Optional.absent();
	}

	public static <T> Key<T> getKeyFromPojo(T pojo) {
		Key<T> key = null;
		try {
			key = Key.create(pojo);
		} catch (NullPointerException npe) {
			log.info("Reason: " + npe.getMessage());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Unexpected exception creating Key<T> from POJO", e);
		}
		return key;
	}

	public static <T> Key<T> getKeyFromWebKey(String webKey) {
		Key<T> key = null;
		try {
			key = Key.create(webKey);
		} catch (Exception e) {
			log.log(Level.WARNING, "Unexpected exception getting Key<T> from webKey", e);
		}
		return key;
	}

	public static <T> String getWebKeyFromPojo(T pojo) {
		Key<T> key = getKeyFromPojo(pojo);
		return (null == key) ? EMPTY : key.toWebSafeString();
	}

	public static <T> T getByProjection(Class<T> cls, String... propNames) {
		ofy().load().type(cls).project(propNames);
		return null;
	}

	public static <T> Optional<Key<T>> saveEntity(T entity) throws SaveException {
		Optional<Result<Key<T>>> result = saveEntityAsync(entity);
		if (result.isPresent())
			return Optional.fromNullable(result.get().now());
		else
			return Optional.absent();
		// try {
		// return Optional.fromNullable(ofy().save().entity(entity).now());
		// } catch (Exception e) {
		// log.log(Level.WARNING, "Failed to save entity: " + entity.toString(),
		// e);
		// return Optional.absent();
		// }
	}

	public static <T> Optional<Result<Key<T>>> saveEntityAsync(T entity) throws SaveException {
		if (entity instanceof Optional)
			throw new IllegalArgumentException(
					"Can't save an Optional. Instead use entity.get() in call to this method.");
		if (null == entity)
			return Optional.absent();
		try {
			return Optional.fromNullable(ofy().save().entity(entity));
		} catch (Exception e) {
			if (e instanceof SaveException) {
				log.warning("Failed to save entity: " + e.getMessage());
			} else {
				log.log(Level.WARNING, "Failed to save entity.", e);
			}
			throw e;
		}
	}

	@SafeVarargs
	public static <T extends GaeEntity> boolean saveEntities(T... entities) {
		try {
			ofy().save().entities(entities).now();
			return true;
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception saving entities.", e);
			return false;
		}
	}

	@SafeVarargs
	public static <T extends GaeEntity> boolean saveEntitiesTransactional(final T... entities) {
		try {
			ofy().transact(new VoidWork() {

				@Override
				public void vrun() {
					try {
						log.info("About to save " + entities.length + " entities transactionally.");
						ofy().save().entities(entities).now();
					} catch (Exception e) {
						log.log(Level.WARNING,
								"Error saving entities transactionally. Stacktrace below:", e);
					}
				}
			});
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception saving entities transactionally", e);
		}
		return false;
	}

	public static <T extends GaeEntity> boolean saveEntities(Iterable<T> entities) {
		try {
			ofy().save().entities(entities).now();
			return true;
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception saving Iterable entities...", e);
			return false;
		}
	}

	@SafeVarargs
	public static <T> void deferredSaveEntities(T... ts) {
		ofy().defer().save().entities(ts);
	}

	public static <T> void deferredSaveEntities(Iterable<T> ts) {
		ofy().defer().save().entities(ts);
	}

	public static <T> void deferredSaveEntity(T t) {
		ofy().defer().save().entity(t);
	}

	@SafeVarargs
	public static <T> void deferredDeleteEntities(T... ts) {
		ofy().defer().delete().entities(ts);
	}

	public static <T> void deferredDeleteEntities(Iterable<T> ts) {
		ofy().defer().delete().entities(ts);
	}

	public static <T> void deferredDeleteEntity(T t) {
		ofy().defer().delete().entity(t);
	}

	public static <T> boolean deleteEntity(T entity) {
		if (null == entity)
			return true;
		Key<T> key = getKeyFromPojo(entity);
		if (null == key)
			return true;
		else
			return deleteEntity(key);
	}

	public static <T> boolean deleteEntity(Key<T> key) {
		if (null == key)
			return true;
		try {
			ofy().delete().key(key).now();
			return true;
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to delete entity: " + key + " ...", e);
			return false;
		}
	}

	public static <T> boolean deleteEntity(Ref<T> ref) {
		if (null == ref)
			return true;
		return deleteEntity(ref.key());
	}

	public static <T> boolean deleteEntityAsync(T pojo) {
		if (null == pojo)
			return true;
		Result<Void> result = ofy().delete().entity(pojo);
		return (result != null);
	}

	public static <T> boolean deleteEntities(Iterable<T> entities) {
		try {
			ofy().delete().entities(entities);
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception while deleting entities...", e);
			return false;
		}
		return true;
	}

	public static <T> List<T> queryByType(Class<T> cls, Filter... filters) {
		checkNotNull(cls);
		Query<T> qry = ofy().load().type(cls);
		for (Filter filter : filters) {
			if (null != filter) {
				qry = qry.filter(filter);
			}
		}
		return qry.list();
	}

	public static <T> Ref<T> getNullableRefFromPojo(T entity) {
		Ref<T> ref = null;
		if (null != entity && Check.idNotNull(entity)) {
			try {
				ref = Ref.create(entity);
			} catch (Exception e) {
				log.log(Level.WARNING, "Error creating a Ref<T> to a POJO", e);
			}
		}
		return ref;
	}

	public static <T> PagingData<T> getPaginatedEntities(PagingData<T> pd, Class<T> cls,
			Filter... filters) {
		checkNotNull(cls);
		if (null == pd) {
			pd = PagingData.<T> builder().build();
		}
		Query<T> qry = ofy().load().type(cls).offset(pd.getOffset()).limit(pd.getItemsPerPage());
		for (Filter filter : filters) {
			if (null != filter) {
				qry = qry.filter(filter);
			}
		}
		pd.setResults(qry.list());
		return pd;
	}
}
