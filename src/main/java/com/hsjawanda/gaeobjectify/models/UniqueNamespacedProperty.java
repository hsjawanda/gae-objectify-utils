/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;


/**
 * @author harsh.deep
 *
 */
public class UniqueNamespacedProperty<T> extends UniqueStringProperty<T> {

	private static final String separator = ":::";

	private static final Joiner joiner = Joiner.on(separator).skipNulls();

	private static final Splitter splitter = Splitter.on(separator);

	protected UniqueNamespacedProperty() {
	}

	protected UniqueNamespacedProperty(String namespace, String id) {
		super(joiner(namespace, id));
	}

	// public static Optional<PrincipalTpId> getById(String namespace, String id) {
	// checkArgument(isNotBlank(namespace), "namespace can't be null, empty or whitespace.");
	// checkArgument(isNotBlank(id), "id can't be null, empty or whitespace.");
	// return GaeDataUtil.getById(PrincipalTpId.class, joiner(namespace, id));
	// }

	protected static void checkArgs(String namespace, String id) {
		checkArgument(isNotBlank(namespace), "namespace can't be null, empty or whitespace.");
		checkArgument(isNotBlank(id), "id can't be null, empty or whitespace.");
	}

	protected static String joiner(String namespace, String id) {
		return joiner.join(namespace, id);
	}

	protected static List<String> splitter(String namespacedId) {
		return splitter.splitToList(namespacedId);
	}

	public String getIdentifier() {
		List<String> parts = splitter(super.getId());
		if (parts.size() > 1)
			return parts.get(1);
		return EMPTY;
	}

	public String getNamespace() {
		List<String> parts = splitter(super.getId());
		if (parts.size() > 0)
			return parts.get(0);
		return EMPTY;
	}

}
