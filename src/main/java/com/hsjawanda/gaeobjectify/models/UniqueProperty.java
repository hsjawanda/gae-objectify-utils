/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.ObjectifyDao;
import com.hsjawanda.gaeobjectify.data.UniquePropertyDao;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.SplitJoin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author harsh.deep
 *
 */
@Entity
@Data
@Accessors(chain = true)
public class UniqueProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter(AccessLevel.NONE)
	private String id;

	@Index
	@Setter(AccessLevel.NONE)
	private long dateCreated = Long.MAX_VALUE;

	@Index
	@Setter(AccessLevel.NONE)
	private String namespace;

	@Setter(AccessLevel.NONE)
	private String value;

	private Map<String, Object> properties = Maps.newTreeMap();

	@Setter(AccessLevel.NONE)
	private String referencedWebSafeKey;

	private static final ObjectifyDao<UniqueProperty> BASE = new ObjectifyDao<>(
			UniqueProperty.class);

	public static final UniquePropertyDao DAO = UniquePropertyDao.instance();

	public static final String FOR_PHONE_NUM = "forPhone";

	public static final String AC_CREATOR_NAME = "acCreatorName";

	public static final String SMS_LAST_SENT = "smsLastSent";

	private UniqueProperty() {
	}

	public static UniqueProperty create(String namespace, String value, String referencedWebSafeKey)
			throws NullPointerException, IllegalArgumentException {
		checkArgument(isNotBlank(namespace), "namespace" + Constants.NOT_BLANK);
		checkArgument(isNotBlank(value), "value" + Constants.NOT_BLANK);
		checkNotNull(referencedWebSafeKey, "referencedWebSafeKey" + Constants.NOT_NULL);
		UniqueProperty retVal = new UniqueProperty();
		retVal.namespace = trimToNull(namespace);
		retVal.value = value.trim();
		retVal.id = genIdPreNormalized(retVal.namespace, retVal.value);
		retVal.referencedWebSafeKey = referencedWebSafeKey;
		retVal.dateCreated = System.currentTimeMillis();
		return retVal;
	}

	public static String genId(String namespace, String value) throws IllegalArgumentException {
		checkArgument(isNotBlank(value), "value" + Constants.NOT_BLANK);
		return genIdPreNormalized(trimToNull(namespace), value.trim());
	}

	private static String genIdPreNormalized(String namespace, String value) {
		return SplitJoin.join(namespace, value);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	public String getValue() {
		return StringUtils.defaultString(this.value);
	}

	public String getNamespace() {
		return StringUtils.defaultString(this.namespace);
	}

	public Optional<Key<UniqueProperty>> save() {
		return BASE.saveEntity(this);
	}

	public void deferredSave() {
		BASE.deferredSaveEntity(this);
	}

//
//	@OnLoad
//	protected void tokenize() {
//		List<String> parts = SplitJoin.split(getId());
//		int size;
//		if (null != parts && (size = parts.size()) > 0) {
//			if (1 == size) {
//				this.value = parts.get(0);
//			} else {
//				this.namespace = parts.get(0);
//				this.value = parts.get(1);
//			}
//		}
//	}

	protected UniqueProperty setId(String namespace, String value) {
		this.id = genId(namespace, value);
		return this;
	}

	/**
	 * @param key
	 * @return
	 * @see com.hsjawanda.gaeobjectify.data.ObjectifyDao#deleteByKey(com.googlecode.objectify.Key)
	 */
	public static boolean deleteByKey(Key<UniqueProperty> key) {
		return BASE.deleteByKey(key);
	}

}
