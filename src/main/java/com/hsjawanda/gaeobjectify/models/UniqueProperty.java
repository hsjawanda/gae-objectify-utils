/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnLoad;

import com.hsjawanda.gaeobjectify.data.GaeDataUtil;
import com.hsjawanda.gaeobjectify.util.SplitJoin;


/**
 * @author harsh.deep
 *
 */
@Entity
public class UniqueProperty<T extends GaeEntity> extends RootEntity<UniqueProperty<T>> {

	@Id
	private String id;

	@Ignore
	private String namespace;

	@Ignore
	private String value;

	private Ref<T> reference;

	private UniqueProperty() {
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

	@OnLoad
	protected void tokenize() {
		List<String> parts = SplitJoin.split(getId());
		int size;
		if (null != parts && (size = parts.size()) > 0) {
			if (1 == size) {
				this.value = parts.get(0);
			} else {
				this.namespace = parts.get(0);
				this.value = parts.get(1);
			}
		}
	}

	protected void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the reference
	 */
	public Optional<T> getReferenced() {
		return GaeDataUtil.getByRef(this.reference);
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(T reference) {
		this.reference = GaeDataUtil.getNullableRefFromPojo(reference);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		public <T extends GaeEntity> UniqueProperty<T> build(String namespace, String value) {
			UniqueProperty<T> prop = null;
			checkArgument(isNotBlank(value), "value can't be null, empty or whitespace");
			prop = new UniqueProperty<>();
			prop.setId(SplitJoin.join(StringUtils.trimToNull(namespace), value));
			return prop;
		}
	}
}
