/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.normalizeSpace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.CategoryDao;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Slugs;
import com.hsjawanda.gaeobjectify.util.SplitJoin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Data
@Accessors(chain = true)
@Entity
@Cache(expirationSeconds = 1800)
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

	private String				icon;

	private String				image;

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

	public Optional<Key<Category>> save() {
		return DAO.saveEntity(this);
	}

	public void deferredSave() {
		DAO.deferredSaveEntity(this);
	}

	public boolean delete() {
		return DAO.deleteEntity(this);
	}

	public void deferredDelete() {
		DAO.deferredDeleteEntity(this);
	}

	public Key<Category> key() {
		return DAO.getKeyFromPojo(this);
	}

}
