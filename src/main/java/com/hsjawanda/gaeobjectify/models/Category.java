/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.CategoryDao;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Slugs;
import com.hsjawanda.gaeobjectify.util.SplitJoin;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Data
@Accessors(chain = true)
@Entity
public class Category implements GaeEntity, StringIdEntity {

	@Id
	@Setter(AccessLevel.NONE)
	private String				id;

	@Index
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private String				namespace;

	private String				displayName;

	private String				pluralDisplayName;

	private String				description;

	@Index
	private int					order;

	@Index
	private boolean				showOnHomepage;

	public static CategoryDao DAO = new CategoryDao();

	private Category() {}

	private Category(String namespace, String displayName) {
		this.namespace = namespace;
		this.displayName = displayName;
		this.id = prepareId(namespace, displayName);
	}

	public static Optional<Category> getFor(String namespace, String displayName, boolean createNew)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(displayName), "displayName" + Constants.NOT_BLANK);
		String preparedId = prepareId(namespace, displayName);
		Optional<Category> catOpt = DAO.getById(preparedId);
		if (!catOpt.isPresent() && createNew) {
			Category cat = new Category(normalizeNamespace(namespace), normalizeSpace(displayName));
			DAO.deferredSaveEntity(cat);
			return Optional.of(cat);
		}
		return catOpt;
	}

	public static String prepareId(String namespace, String displayName)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(displayName), "displayName" + Constants.NOT_BLANK);
		String normalizedName = Slugs.toSlug(normalizeSpace(displayName));
		return SplitJoin.join(normalizeNamespace(namespace), normalizedName);
	}

	public static String normalizeNamespace(String namespace) {
		namespace = normalizeSpace(namespace);
		return null == namespace ? EMPTY : namespace;
	}

	public Category setOrder(int order) {
		this.order = Math.max(1, order);
		return this;
	}

}
