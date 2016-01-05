/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.hsjawanda.gaeobjectify.collections.NonNullList;
import com.hsjawanda.gaeobjectify.data.ObjectifyDao;
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
@Accessors(chain = true, fluent = true)
public class Tag implements GaeEntity {

	@Id
	@Setter(value = AccessLevel.NONE)
	protected String name;

	@Setter(value = AccessLevel.NONE)
	protected String displayName;

	@Setter(value = AccessLevel.NONE)
	protected List<String> ids = NonNullList.empty();

	public static final String PREFIX = EMPTY;

	public static final ObjectifyDao<Tag> dao = new ObjectifyDao<>(Tag.class);

	protected Tag() {
	}

	public Tag(String dispName) {
		this(PREFIX, dispName);
	}

	public void addId(String id) {
		this.ids.add(trimToNull(id));
	}

	public void removeId(String id) {
		this.ids.remove(trimToNull(id));
	}

	protected Tag(String prefix, String dispName) {
		this();
		setNames(prefix, dispName);
	}

	protected Tag setNames(String prefix, String name) {
		this.name = normalizeName(prefix, name);
		this.displayName = normalizeSpace(name);
		return this;
	}

	public void changeDisplayName(String newDispName) {
		throw new NotImplementedException("Not yet implemented!");
	}

	public static String normalizeName(String dispName) {
		return normalizeName(PREFIX, dispName);
	}

	protected static String normalizeName(String prefix, String dispName) {
		dispName = checkNotNull(trimToNull(dispName), "dispName" + Constants.notBlank);
		prefix = trimToNull(prefix);
		return SplitJoin.join(normalizeSpace(prefix),
				normalizeSpace(dispName).toLowerCase().replace(' ', '-'));
	}
}
