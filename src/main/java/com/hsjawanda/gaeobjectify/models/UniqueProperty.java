/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.UniquePropertyDao;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.SplitJoin;


/**
 * @author harsh.deep
 *
 */
@Entity
@Data
@Accessors(chain = true)
public class UniqueProperty {

	@Id
	@Setter(AccessLevel.NONE)
	private String							id;

	@Index
	@Setter(AccessLevel.NONE)
	private String							namespace;

	@Setter(AccessLevel.NONE)
	private String							value;

	@Setter(AccessLevel.NONE)
	private String							referencedWebSafeKey;

	public static final UniquePropertyDao	DAO	= UniquePropertyDao.instance();

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
}
