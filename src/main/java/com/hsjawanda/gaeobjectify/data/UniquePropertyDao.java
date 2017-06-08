/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.hsjawanda.gaeobjectify.models.UniqueProperty;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class UniquePropertyDao {

	@SuppressWarnings("unused")
	private static Logger								LOG			= Logger.getLogger(UniquePropertyDao.class
																			.getName());

	private static final ObjectifyDao<UniqueProperty>	OBJ_DAO		= new ObjectifyDao<UniqueProperty>(
																			UniqueProperty.class);

	private static final UniquePropertyDao				INSTANCE	= new UniquePropertyDao();

	private UniquePropertyDao() {
	}

	public static UniquePropertyDao instance() {
		return INSTANCE;
	}

	/**
	 * @param id
	 * @return
	 * @see com.hsjawanda.gaeobjectify.data.ObjectifyDao#getById(java.lang.String)
	 */
	public Optional<UniqueProperty> getByNamespacedValue(String namespace, String value)
			throws IllegalArgumentException {
		String id = UniqueProperty.genId(namespace, value);
		return OBJ_DAO.getById(id);
	}

	/**
	 * @param key
	 * @return
	 * @see com.hsjawanda.gaeobjectify.data.ObjectifyDao#getByKey(com.googlecode.objectify.Key)
	 */
	public Optional<UniqueProperty> getByKey(Key<UniqueProperty> key) {
		return OBJ_DAO.getByKey(key);
	}

	/**
	 * @param entity
	 * @return
	 * @see com.hsjawanda.gaeobjectify.data.ObjectifyDao#deleteEntity(java.lang.Object)
	 */
	public boolean deleteEntity(UniqueProperty entity) {
		return OBJ_DAO.deleteEntity(entity);
	}

	/**
	 * @param key
	 * @return
	 * @see com.hsjawanda.gaeobjectify.data.ObjectifyDao#deleteByKey(com.googlecode.objectify.Key)
	 */
	public boolean deleteByKey(Key<UniqueProperty> key) {
		return OBJ_DAO.deleteByKey(key);
	}

	/**
	 * @param id
	 * @return
	 * @see com.hsjawanda.gaeobjectify.data.ObjectifyDao#deleteById(java.lang.String)
	 */
	public boolean deleteByNamespacedValue(String namespace, String value)
			throws IllegalArgumentException {
		String id = UniqueProperty.genId(namespace, value);
		return OBJ_DAO.deleteById(id);
	}

}
