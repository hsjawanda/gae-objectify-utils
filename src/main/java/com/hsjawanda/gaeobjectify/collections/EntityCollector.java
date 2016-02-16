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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;


/**
 * @author harsh.deep
 *
 */
public class EntityCollector<K, V> {

	private static final int INITIAL_CAPACITY = 3;

	protected List<V> entityList;

	protected List<Ref<V>> entityRefs;

	protected List<Ref<V>> refsToLoad;

	protected Map<K, V> entityMap;

	protected KeyGenerator<K, V> keyGen;

	protected boolean entityMapModified = true;

	protected boolean hasEntityAnnotation = true;

	protected boolean refsLoaded = false;

	Class<V> cls;

	public static <K, V> EntityCollector<K, V> instance(Class<V> cls, KeyGenerator<K, V> keyGen) {
		checkNotNull(cls);
		checkNotNull(keyGen);
		EntityCollector<K, V> ec = new EntityCollector<>();
		ec.cls = cls;
		ec.keyGen = keyGen;
		return ec;
	}

	public static <K, V> EntityCollector<K, V> instance(Class<V> cls, KeyGenerator<K, V> keyGen,
			List<Ref<V>> refsToLoad) {
		EntityCollector<K, V> ec = instance(cls, keyGen);
		ec.refsToLoad = refsToLoad;
		return ec;
	}

	public void add(V entity) {
		if (!this.refsLoaded) {
			loadRefs();
		}
		if (null == entity)
			return;
		if (null == this.entityMap) {
			allocateMap();
		}
		this.entityMap.put(this.keyGen.keyFor(entity), entity);
		this.entityMapModified = true;
	}

	public boolean containsValue(V entity) {
		if (!this.refsLoaded) {
			loadRefs();
		}
		if (null != this.entityMap)
			return this.entityMap.containsValue(entity);
		return false;
	}

	public boolean containsKey(K key) {
		if (!this.refsLoaded) {
			loadRefs();
		}
		if (null != this.entityMap)
			return this.entityMap.containsKey(key);
		return false;
	}

	public boolean isEmpty() {
		if (!this.refsLoaded) {
			loadRefs();
		}
		if (null == this.entityMap)
			return true;
		return this.entityMap.isEmpty();
	}

	public V removeByKey(K key) {
		if (!this.refsLoaded) {
			loadRefs();
		}
		if (null == key || null == this.entityMap)
			return null;
		V removedEntity = this.entityMap.remove(key);
		if (null != removedEntity) {
			this.entityMapModified = true;
		}
		return removedEntity;
	}

	public List<Ref<V>> preSaveAction() throws UnsupportedOperationException {
		if (!this.cls.isAnnotationPresent(Entity.class))
			throw new UnsupportedOperationException("The class " + this.cls.getName()
					+ " doesn't have Entity annotation, therefore a List<Ref<"
					+ this.cls.getSimpleName() + ">> can't be generated.");
		if (!this.refsLoaded)
			return this.refsToLoad;
		if (null == this.entityMap || this.entityMap.isEmpty())
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
		if (!this.refsLoaded) {
			loadRefs();
		}
		if (null == this.entityMap || this.entityMap.isEmpty())
			return Collections.emptyList();
		if (null == this.entityList) {
			this.entityList = new ArrayList<>(this.entityMap.size());
		}
		if (this.entityMapModified) {
			this.entityList.clear();
			for (K key : this.entityMap.keySet()) {
				this.entityList.add(this.entityMap.get(key));
			}
			this.entityMapModified = false;
		}
		return this.entityList;
	}

	public Map<K, V> asReadOnlyMap() {
		if (!this.refsLoaded) {
			loadRefs();
		}
		return Collections.unmodifiableMap(this.entityMap);
	}

//	public void loadFromEntityRefs(List<Ref<V>> refs) {
//		if (null != refs) {
//			allocateMap();
//			this.entityMap.clear();
//			for (Ref<V> ref : refs) {
//				if (null == ref) {
//					continue;
//				}
//				V entity = ref.get();
//				this.entityMap.put(this.keyGen.keyFor(entity), entity);
//			}
//		}
////		throw new NotImplementedException("Not yet implemented.");
//	}

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

//	protected void refsToLoad(List<Ref<V>> refsToLoad) {
//		this.refsToLoad = refsToLoad;
//	}

	protected void loadRefs() {
		if (null == this.entityMap) {
			allocateMap();
		}
		Map<Key<V>, V> entities = GaeDataUtil.getByRefs(this.refsToLoad);
		for (Key<V> key : entities.keySet()) {
			V entity = entities.get(key);
			this.entityMap.put(this.keyGen.keyFor(entity), entity);
		}
		this.entityMapModified = true;
		this.refsLoaded = true;
	}

	protected void allocateMap() {
		if (null == this.entityMap) {
			this.entityMap = new LinkedHashMap<>(INITIAL_CAPACITY);
		}
	}
}
