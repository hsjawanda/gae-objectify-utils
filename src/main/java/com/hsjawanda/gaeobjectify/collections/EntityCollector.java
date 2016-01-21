/**
 *
 */
package com.hsjawanda.gaeobjectify.collections;

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

	protected KeyGenerator<K, V> keyGen;

	protected boolean entityMapModified = true;

	protected boolean hasEntityAnnotation = true;

	Class<V> cls;

	public static <K, V> EntityCollector<K, V> instance(Class<V> cls, KeyGenerator<K, V> keyGen) {
		checkNotNull(cls);
		checkNotNull(keyGen);
		EntityCollector<K, V> ec = new EntityCollector<>();
		ec.cls = cls;
		ec.keyGen = keyGen;
		return ec;
	}

	public void addEntity(V entity) {
		if (null == entity)
			return;
		if (null == this.entityMap) {
			allocateMap();
		}
		this.entityMap.put(this.keyGen.keyFor(entity), entity);
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
		if (null == this.entityMap || this.entityMap.isEmpty())
			return Collections.emptyList();
		if (null == this.entities) {
			this.entities = new ArrayList<>(this.entityMap.size());
		}
		if (this.entityMapModified) {
			this.entities.clear();
			for (K key : this.entityMap.keySet()) {
				this.entities.add(this.entityMap.get(key));
			}
			this.entityMapModified = false;
		}
		return this.entities;
	}

	public Map<K, V> asMap() {
		return Collections.unmodifiableMap(this.entityMap);
	}

	public void loadFromEntityRefs(List<Ref<V>> refs) {
		if (null != refs) {
			allocateMap();
			this.entityMap.clear();
			for (Ref<V> ref : refs) {
				if (null == ref) {
					continue;
				}
				V entity = ref.get();
				this.entityMap.put(this.keyGen.keyFor(entity), entity);
			}
		}
		throw new NotImplementedException("Not yet implemented.");
	}

	public void loadFromEntities(List<V> entities) {
		if (null != entities && entities.size() > 0) {
			allocateMap();
			this.entityMap.clear();
			for (V entity : entities) {
				if (null == entity) {
					continue;
				}
				this.entityMap.put(this.keyGen.keyFor(entity), entity);
			}
		}
	}

	protected void allocateMap() {
		if (null == this.entityMap) {
			this.entityMap = new LinkedHashMap<>(INITIAL_CAPACITY);
		}
	}
}
