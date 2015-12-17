/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;


/**
 * @author harsh.deep
 *
 */
public class EntityCollector<K, V> {

	private static final int INITIAL_CAPACITY = 3;

	protected List<V> entities;

	protected List<Ref<V>> entityRefs;

	protected Map<K, V> entityMap;

	protected boolean entityMapModified = false;

	protected boolean hasEntityAnnotation = true;

	Class<V> cls;

	public EntityCollector(Class<V> cls) {
		checkNotNull(cls);
		this.cls = cls;
	}

	public void addEntity(K key, V entity) {
		if (null == key || null == entity)
			return;
		if (null == this.entityMap) {
			this.allocateMap();
		}
		this.entityMap.put(key, entity);
		this.entityMapModified = true;
	}

	public V removeEntityByKey(K key) {
		if (null == key || null == this.entityMap)
			return null;
		V removedEntity = this.entityMap.remove(key);
		if (null != removedEntity) {
			this.entityMapModified = true;
		}
		return removedEntity;
	}

	public List<Ref<V>> asRefs() {
		if (!this.cls.isAnnotationPresent(Entity.class))
			throw new UnsupportedOperationException("The class " + this.cls.getName()
					+ " doesn't have Entity annotation, therefore a List<Ref<"
					+ this.cls.getSimpleName() + ">> can't be generated.");
		if (null == this.entityMap || this.entityMap.size() < 1)
			return Collections.emptyList();
		if (this.entityMapModified) {
			if (null == this.entityRefs) {
				this.entityRefs = new ArrayList<>(this.entityMap.size());
			}
			this.entityRefs.clear();
			for (K key : this.entityMap.keySet()) {
				Ref<V> ref = GaeDataUtil.getNullableRefFromPojo(this.entityMap.get(key));
				if (null != ref) {
					this.entityRefs.add(ref);
				}
			}
			this.entityMapModified = false;
		}
		return this.entityRefs;
	}

	public List<V> asList() {
		if (null == this.entityMap || this.entityMap.size() < 1)
			return Collections.emptyList();
		if (this.entityMapModified) {
			if (null == this.entities) {
				this.entities = new ArrayList<>(this.entityMap.size());
			}
			this.entities.clear();
			for (K key : this.entityMap.keySet()) {
				this.entities.add(this.entityMap.get(key));
			}
			this.entityMapModified = false;
		}
		return this.entities;
	}

	public void loadFromEntityRefs(Class<V> cls, List<Ref<V>> refs) {
		throw new NotImplementedException("Not yet implemented.");
	}

	public Map<K, V> loadFromEmbeddedEntities(Class<V> cls, List<V> entities) {
		throw new NotImplementedException("Not yet implemented.");
	}

	protected void allocateMap() {
		if (null == this.entityMap) {
			this.entityMap = new LinkedHashMap<>(INITIAL_CAPACITY);
		}
	}
}
