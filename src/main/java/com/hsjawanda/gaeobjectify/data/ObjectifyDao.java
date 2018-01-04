/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static java.util.logging.Level.WARNING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Range;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.SaveException;
import com.googlecode.objectify.cmd.Query;
import com.hsjawanda.gaeobjectify.collections.CollectionHelper;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Holdall;
import com.hsjawanda.gaeobjectify.util.Pager;
import com.hsjawanda.gaeobjectify.util.PagingData;
import com.hsjawanda.gaeobjectify.util.Tracer;

import lombok.NonNull;


/**
 * @author harsh.deep
 *
 */
public class ObjectifyDao<T> {

	protected Class<T> cls;

	protected final Logger log;

	protected static int DEFAULT_LIMIT = 20;

	protected static final Range<Integer> MAX_TRIES_RANGE = Range.closed(1, 15);

	protected static final Range<Integer> WAIT_TIME_RANGE = Range.closed(10, 200);

	public ObjectifyDao(Class<T> cls) {
		this.cls = cls;
		this.log = Logger.getLogger(ObjectifyDao.class.getSimpleName() + ":" + this.cls.getName());
	}

	public Optional<T> getByKey(Key<T> key, int maxTries, int waitMillis) {
		if (null == key)
			return Optional.absent();
		maxTries = Holdall.constrainToRange(MAX_TRIES_RANGE, Integer.valueOf(maxTries));
		waitMillis = Holdall.constrainToRange(WAIT_TIME_RANGE, Integer.valueOf(waitMillis));
		T entity = null;
		try {
			int i;
			for (i = 0; i < maxTries && null == entity; i++) {
				try {
					entity = ofy().load().key(key).now();
					if (null == entity) {
						break;
					}
				} catch (ConcurrentModificationException e) {
					Holdall.sleep(waitMillis);
					waitMillis = waitMillis << 1;
				}
			}
			if (i > 1) {
				this.log.info(String.format("%2d tries to retrieve by " + key
						+ ". Succeeded: %s. Last waitMillis: %d. Stacktrace:\n%s", i, (null != entity), waitMillis,
						Tracer.partialTrace(null, 0, 7)));
			}
			return Optional.fromNullable(entity);
		} catch (Exception e) {
			this.log.log(Level.WARNING, "Error getting by Key<T>", e);
			return Optional.absent();
		}
	}

	public Optional<T> getByKey(Key<T> key) {
		return getByKey(key, 4, 100);
	}

	public Optional<T> getByWebKey(String webKey) {
		if (isBlank(webKey))
			return Optional.absent();
		Key<T> key = null;
		try {
			key = Key.create(webKey);
			return this.getByKey(key);
		} catch (Exception e) {
			this.log.log(WARNING, "Error creating webKey from '" + webKey + "'. Stacktrace:", e);
			return Optional.absent();
		}
	}

	public Optional<T> getByRef(Ref<T> entityRef) {
		if (null == entityRef)
			return Optional.absent();
		T retVal = null;
		try {
			retVal = ofy().load().ref(entityRef).now();
		} catch (Exception e) {
			this.log.log(WARNING, "Error loading entity from Ref<" + this.cls.getClass().getName()
					+ ">. Stacktrace:", e);
		}
		return Optional.fromNullable(retVal);
	}

	public Map<Key<T>, T> getByRefs(Iterable<Ref<T>> entities) {
		if (null == entities)
			return Collections.emptyMap();
		return ofy().load().refs(entities);
	}

	public Optional<T> getById(String id, int maxTries, int waitMillis) {
		if (isBlank(id))
			return Optional.absent();
		Key<T> key = Key.create(this.cls, id);
		return getByKey(key, maxTries, waitMillis);
	}

	public Optional<T> getById(String id) {
		return getById(id, 4, 100);
	}

	public Optional<T> getByIdThrow(String id) {
		if (isBlank(id))
			return Optional.absent();
		return Optional.fromNullable(ofy().load().type(this.cls).id(id).now());
	}

	public Map<String, T> getByStringIds(Iterable<String> ids) {
		if (null == ids)
			return Collections.emptyMap();
		return ofy().load().type(this.cls).ids(ids);
	}

	public Map<Key<T>, T> getByKeys(Iterable<Key<T>> keys) {
		if (null == keys)
			return Collections.emptyMap();
		return ofy().load().keys(keys);
	}

	public Optional<T> getById(long id) {
		Key<T> key = null;
		try {
			key = Key.create(this.cls, id);
			return this.getByKey(key);
		} catch (Exception e) {
			this.log.log(Level.WARNING,
					"Unexpected exception getting Optional<" + this.cls.getSimpleName()
							+ "> from long id", e);
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
			this.log.log(
					Level.SEVERE,
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
			this.log.log(Level.WARNING,
					"Unexpected exception getting Key<" + this.cls.getSimpleName()
							+ "> from webKey", e);
		}
		return key;
	}

	public Key<T> getKeyFromId(String id) {
		if (isBlank(id))
			return null;
		Key<T> key = null;
		try {
			key = Key.create(this.cls, id);
		} catch (Exception e) {
			this.log.log(Level.WARNING,
					"Unexpected exception getting Key<" + this.cls.getSimpleName() + "> for ID "
							+ id, e);
		}
		return key;
	}

	public String getWebKeyFor(T pojo) {
		Key<T> key = this.getKeyFromPojo(pojo);
		return (null == key) ? EMPTY : key.toWebSafeString();
	}

	public String getNullableWebKeyFor(T pojo) {
		Key<T> key = this.getKeyFromPojo(pojo);
		return (null == key) ? null : key.toWebSafeString();
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

	public QueryResultIterable<T> getByProjection(@Nonnull Pager<T> pgr, Iterable<Filter> filters,
			Iterable<String> sorts, String... propNames) {
		checkNotNull(pgr, "pgr" + Constants.NOT_NULL);
		Query<T> qry = ofy().load().type(this.cls).project(propNames);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		if (null != sorts) {
			for (String sort : sorts) {
				if (isNotBlank(sort)) {
					qry = qry.order(sort);
				}
			}
		}
//		if (pgr.isGenTotalResults()) {
//			qry = qry.limit(pgr.getCountLimit());
//			pgr.setTotalResults(qry.count());
//		}
		qry = qry.limit(pgr.getLimit());
		if (null != pgr.getCursor()) {
			try {
				qry = qry.startAt(pgr.getCursor());
			} catch (Exception e) {
				this.log.log(Level.WARNING,
						"Exceptiong creating Datastore startCursor. Stacktrace:", e);
			}
		}
		return qry.iterable();
	}

	public QueryResultIterable<T> getByProjection(@Nonnull Pager<T> pgr, Filter filter,
			String sort, String... propNames) {
		Iterable<Filter> filters = filter == null ? null : Arrays.asList(filter);
		Iterable<String> sorts = sort == null ? null : Arrays.asList(sort);
		return getByProjection(pgr, filters, sorts, propNames);
	}

	public Optional<Key<T>> saveEntity(T entity) throws SaveException {
		Optional<Result<Key<T>>> result = this.saveEntityAsync(entity);
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
			this.log.warning("Failed to save entity because: " + e.getMessage());
			throw e;
		}
	}

	public boolean saveEntities(@SuppressWarnings("unchecked") T... entities) {
		if (null == entities)
			return true;
		return this.saveEntities(Arrays.asList(entities));
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
			Key<T> key = this.keyFor(id);
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
			this.deferredDeleteByKey(ref.getKey());
		}
	}

	public boolean deleteEntity(T entity) {
		if (null == entity)
			return true;
		Key<T> key = this.getKeyFromPojo(entity);
		if (null == key)
			return true;
		else
			return this.deleteByKey(key);
	}

	public boolean deleteById(String id) {
		return deleteByKey(Key.create(this.cls, id));
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

	public void deleteByKeys(Iterable<Key<T>> keys) {
		ofy().delete().keys(keys).now();
	}

	public boolean deleteByRef(Ref<T> ref) {
		if (null == ref)
			return true;
		return this.deleteByKey(ref.key());
	}

	public boolean deleteByWebKey(String webKey) {
		if (isBlank(webKey))
			return true;
		return this.deleteByKey(Key.<T> create(webKey));
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
		return this.deleteEntities(Arrays.asList(entities));
	}

	/**
	 * Find all the entities that satisfy a given query and then delete them.
	 *
	 * @param pgr
	 *            the {@link Pager} to use. Shouldn't be {@code null}.
	 * @param filters
	 *            the {@code Filter}s to use
	 * @param deferDeletion
	 *            whether deletion should be deferred
	 * @return {@code true} if another iteration may be needed, {@code false}
	 *         otherwise
	 */
	public boolean deleteByQuery(@NonNull Pager<?> pgr, Iterable<Filter> filters,
			boolean deferDeletion) {
		QueryResultIterable<Key<T>> iterable = getKeysByQuery(pgr, filters, (Iterable<String>) null);
		if (deferDeletion) {
			deferredDeleteByKeys(iterable);
		} else {
			deleteByKeys(iterable);
		}
		QueryResultIterator<Key<T>> itr = iterable.iterator();
		int numKeys = Iterators.size(itr);
		this.log.info("Deleted " + numKeys + " " + this.cls.getName() + " entities (limit: "
				+ pgr.getLimit() + "). Deferred: " + deferDeletion + ".");
		if (numKeys > 0) {
			pgr.setCursor(itr.getCursor());
			return true; // another iteration may be needed
		}
		return false;
	}

	public List<T> filteredQuery(Filter... filters) {
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

	public List<T> filteredQuery(Map<String, Object> filters) {
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (String condition : filters.keySet()) {
				qry = qry.filter(condition, filters.get(condition));
			}
		}
		return qry.list();
	}

	public Ref<T> getNullableRefFromPojo(T entity) {
		Ref<T> ref = null;
		if (null != entity) {
			try {
				ref = Ref.create(entity);
			} catch (NullPointerException e) {
				this.log.warning("NullPointerException because: " + e.getMessage());
			} catch (Exception e) {
				this.log.log(Level.WARNING, "Error creating a Ref<" + this.cls.getSimpleName()
						+ "> to a POJO", e);
			}
		}
		return ref;
	}

	/**
	 * @param pd
	 *            the {@link PagingData} object that specifies paging-related
	 *            options, including the cursor
	 * @param filters
	 *            the {@link Filter}s to use. Can be {@code null}.
	 * @param sorts
	 *            the sorting to use. Can be {@code null}.
	 * @throws NullPointerException
	 *             if {@code pd} is {@code null}
	 */
	public void getPaginatedEntities(PagingData<T> pd, Iterable<? extends Filter> filters,
			Iterable<String> sorts) throws NullPointerException {
		checkNotNull(pd, "The PagingData object can't be null.");
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		if (null != sorts) {
			for (String sort : sorts) {
				if (isNotBlank(sort)) {
					qry = qry.order(sort);
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

	public void getPaginatedEntities(PagingData<T> pd, Filter... filters) {
		getPaginatedEntities(pd, Arrays.asList(filters), null);
	}

	public void getPaginatedEntities(PagingData<T> pd, Iterable<? extends Filter> filters) {
		getPaginatedEntities(pd, filters, null);
	}

	public void getPaginatedEntities(PagingData<T> pd, Filter filter, String sort) {
		getPaginatedEntities(pd, Arrays.asList(filter), Arrays.asList(sort));
	}

	public List<T> getPaginatedEntities(Pager<?> pgr, Iterable<? extends Filter> filters,
			Iterable<String> sorts) throws NullPointerException {
		QueryResultIterator<T> qryItr = getResults(pgr, filters, sorts);
		List<T> retList = CollectionHelper.toImmutableList(qryItr, pgr.getLimit());
		pgr.setCursor(retList.size() > 0 ? qryItr.getCursor() : null);
		return retList;
	}

	public List<T> getPaginatedEntities(Pager<?> pgr, Filter filter, String sort) {
		Iterable<Filter> filters = filter == null ? null : Arrays.asList(filter);
		Iterable<String> sorts = sort == null ? null : Arrays.asList(sort);
		return getPaginatedEntities(pgr, filters, sorts);
	}

	public QueryResultIterator<T> getResults(Pager<?> pgr, Iterable<? extends Filter> filters,
			Iterable<String> sorts) throws NullPointerException {
		if (null == pgr) {
			pgr = Pager.builder().limit(DEFAULT_LIMIT).build();
		}
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		if (null != sorts) {
			for (String sort : sorts) {
				if (isNotBlank(sort)) {
					qry = qry.order(sort);
				}
			}
		}
		this.log.fine("genTotalResults: " + pgr.isGenTotalResults() + "; nextCursor: "
				+ (null == pgr.getCursor() ? "null" : pgr.getCursor().toWebSafeString()));
		if (pgr.isGenTotalResults()) {
			qry = qry.limit(pgr.getCountLimit());
			pgr.setTotalResults(qry.count());
		}
		qry = qry.limit(pgr.getLimit());
		if (null != pgr.getCursor()) {
			try {
				qry = qry.startAt(pgr.getCursor());
			} catch (Exception e) {
				this.log.log(Level.WARNING,
						"Exceptiong creating Datastore startCursor. Stacktrace:", e);
			}
		}
		return qry.iterator();
	}

	public QueryResultIterator<T> getResults(Pager<?> pgr, Filter filter, String sort) {
		Iterable<Filter> filters = filter == null ? null : Arrays.asList(filter);
		Iterable<String> sorts = sort == null ? null : Arrays.asList(sort);
		return getResults(pgr, filters, sorts);
	}

	public QueryResultIterable<T> getIterableResults(@Nonnull Pager<?> pgr,
			Iterable<? extends Filter> filters, Iterable<String> sorts) throws NullPointerException {
		checkNotNull(pgr, "pgr" + Constants.NOT_NULL);
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		if (null != sorts) {
			for (String sort : sorts) {
				if (isNotBlank(sort)) {
					qry = qry.order(sort);
				}
			}
		}
		this.log.fine("genTotalResults: " + pgr.isGenTotalResults() + "; nextCursor: "
				+ (null == pgr.getCursor() ? "null" : pgr.getCursor().toWebSafeString()));
		if (pgr.isGenTotalResults()) {
			qry = qry.limit(pgr.getCountLimit());
			pgr.setTotalResults(qry.count());
		}
		qry = qry.limit(pgr.getLimit());
		if (null != pgr.getCursor()) {
			try {
				Cursor startCursor = pgr.getCursor();
				qry = qry.startAt(startCursor);
			} catch (Exception e) {
				this.log.log(Level.WARNING,
						"Exceptiong creating Datastore startCursor. Stacktrace:", e);
			}
		}
		return qry.iterable();
	}

	public QueryResultIterable<T> getIterableResults(@Nonnull Pager<?> pgr, Filter filter,
			String sort) throws NullPointerException {
		checkNotNull(pgr, "pgr" + Constants.NOT_NULL);
		List<Filter> filters = null == filter ? null : Arrays.asList(filter);
		List<String> sorts = null == sort ? null : Arrays.asList(sort);
		return getIterableResults(pgr, filters, sorts);
	}

	public int getEntityCount(Iterable<? extends Filter> filters, int limit) {
		limit = Math.max(1, limit);
		Query<T> qry = ofy().transactionless().load().type(this.cls).limit(limit);
		if (null != filters) {
			for (Filter filter : filters) {
				if (null != filter) {
					qry = qry.filter(filter);
				}
			}
		}
		return qry.count();
	}

	public int getEntityCount(Filter filter, int limit) {
		Iterable<Filter> filters = filter == null ? null : Arrays.asList(filter);
		return getEntityCount(filters, limit);
	}

	public int getEntityCount(Filter filter) {
		return getEntityCount(filter, 20_000);
	}

	public QueryResultIterable<Key<T>> getKeysByQuery(@NonNull Pager<?> pgr,
			Iterable<Filter> filters, Iterable<String> sorts) {
		Query<T> qry = ofy().load().type(this.cls);
		if (null != filters) {
			for (Filter filter : filters) {
				qry = qry.filter(filter);
			}
		}
		if (null != sorts) {
			for (String sort : sorts) {
				if (isNotBlank(sort)) {
					qry = qry.order(sort);
				}
			}
		}
		Cursor curs = pgr.getCursor();
		if (null != curs) {
			qry = qry.startAt(curs);
		}
		return qry.limit(pgr.getLimit()).keys().iterable();
	}

	public QueryResultIterable<Key<T>> getKeysByQuery(@NonNull Pager<?> pgr,
			Iterable<Filter> filters, String sort) {
		Iterable<String> sorts = null == sort ? null : Arrays.asList(sort);
		return getKeysByQuery(pgr, filters, sorts);
	}

	public QueryResultIterable<Key<T>> getKeysByQuery(@NonNull Pager<?> pgr,
			Filter filter, String sort) {
		Iterable<String> sorts = null == sort ? null : Arrays.asList(sort);
		Iterable<Filter> filters = null == filter ? null : Arrays.asList(filter);
		return getKeysByQuery(pgr, filters, sorts);
	}

	public boolean entityExists(@NonNull String id) throws IllegalArgumentException {
		checkArgument(isNotBlank(id), "id" + Constants.NOT_BLANK);
		return entityExists(Key.create(this.cls, id));
	}

	public boolean entityExists(@NonNull Key<T> key) {
		return null != ofy().load().filterKey(key).keys().first().now();
	}

}
