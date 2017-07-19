/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.googlecode.objectify.annotation.Ignore;


/**
 * @author harsh.deep
 *
 */
public class UniqueNamespacedProperty<T> extends UniqueStringProperty<T> {

	/**
	 * 22/06/17
	 */
	private static final long serialVersionUID = 1L;

	private static final String separator = ":::";

	private static final Joiner joiner = Joiner.on(separator).skipNulls();

	private static final Splitter splitter = Splitter.on(separator);

	@Ignore
	private List<String> parts;

	protected UniqueNamespacedProperty() {
	}

	protected UniqueNamespacedProperty(String namespace, String id) {
		super(joiner(trimToNull(namespace), trimToEmpty(id)));
	}

	// public static Optional<PrincipalTpId> getById(String namespace, String id) {
	// checkArgument(isNotBlank(namespace), "namespace can't be null, empty or whitespace.");
	// checkArgument(isNotBlank(id), "id can't be null, empty or whitespace.");
	// return GaeDataUtil.getById(PrincipalTpId.class, joiner(namespace, id));
	// }

	// protected static void checkArgs(String namespace, String id) {
	// checkArgument(isNotBlank(namespace), "namespace can't be null, empty or whitespace.");
	// checkArgument(isNotBlank(id), "id can't be null, empty or whitespace.");
	// }

	protected static String joiner(String namespace, String id) {
		return joiner.join(namespace, id);
	}

	protected static List<String> splitter(String namespacedId) {
		return splitter.splitToList(namespacedId);
	}

	protected String getIdentifier() {
		if (null == this.parts) {
			this.parts = splitter(super.getId());
		}
		switch (this.parts.size()) {
		case 0:
			return EMPTY;
		case 1:
			return this.parts.get(0);
		case 2:
		default:
			return this.parts.get(1);
		}
	}

	protected String getNamespace() {
		if (null == this.parts) {
			this.parts = splitter(super.getId());
		}
		if (this.parts.size() > 0)
			return this.parts.get(0);
		return EMPTY;
	}

}
