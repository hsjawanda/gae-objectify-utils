/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.normalizeSpace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.hsjawanda.gaeobjectify.collections.KeyGenerator;
import com.hsjawanda.gaeobjectify.data.TagStoreDao;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Convert;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Entity
@Data
@Accessors(chain = true, fluent = true)
public class TagStore {

	private static final Logger log = Logger.getLogger(TagStore.class.getName());

	@Id
	@Setter(AccessLevel.NONE)
	private String storeName;

	@Ignore
	private Map<String, Tag> tags = new LinkedHashMap<>(10);

	private List<Tag> tagsList = new ArrayList<>();

	protected static final KeyGenerator<String, Tag> keyGen = new TagStoreKeyGen();

	@Ignore
	private boolean modified = true;

	public static final TagStoreDao DAO = new TagStoreDao();

	private TagStore() {
	}

	TagStore(String storeName) {
		this();
		this.storeName = normalizeTagType(storeName);
	}

	public Tag addTag(String displayName) {
		String tagNormalize = Tag.normalizeName(displayName);
		Tag tag = null;
		if (!this.tags.containsKey(tagNormalize)) {
			tag = new Tag(displayName);
			this.tags.put(tagNormalize, tag);
			this.modified = true;
			DAO.deferredSaveEntity(this);
		} else {
			tag = this.tags.get(tagNormalize);
		}
		return tag;
	}

	public Tag tagFor(String displayName) {
		return this.tags.get(Tag.normalizeName(displayName));
	}

	public Tag applyTag(String tagDisplayName, StringIdEntity entity) {
		Tag tag = addTag(tagDisplayName);
		tag.addId(entity.getId());
		this.modified = true;
		DAO.deferredSaveEntity(this);
		return tag;
	}

	public Set<String> entitiesWithTag(String tagDisplayName) {
		String tagNormalized = Tag.normalizeName(tagDisplayName);
		if (this.tags.containsKey(tagNormalized))
			return Collections.unmodifiableSet(this.tags.get(tagNormalized).ids());
		else
			return Collections.emptySet();
	}

	public Collection<Tag> getAllTags() {
		return Collections.unmodifiableCollection(this.tags.values());
	}

	public static String normalizeTagType(String tagType) {
		checkArgument(isNotBlank(tagType), "tagType" + Constants.NOT_BLANK);
		return normalizeSpace(tagType).toLowerCase().replace(' ', '-');
	}

	public void save() {
		if (this.modified) {
			log.info("About to save TagStore '" + this.storeName + "'");
			DAO.deferredSaveEntity(this);
		}
		this.modified = false;
	}

	@OnSave
	protected void saveActions() {
		this.tagsList = Convert.mapToList(this.tags, this.tagsList);
	}

	@OnLoad
	protected void loadActions() {
		log.info("In loadActions.");
		this.tags = Convert.listToMap(this.tagsList, keyGen, this.tags);
	}

}
