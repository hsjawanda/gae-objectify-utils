/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.trimToNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.hsjawanda.gaeobjectify.data.ObjectifyDao;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Normalize;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Entity
@Cache(expirationSeconds = 3600)
@Data
@Accessors(chain = true)
public class Setting implements StringIdEntity, Serializable {

	private static final long	serialVersionUID	= 1L;

	private static Logger						LOG		= Logger.getLogger(Setting.class.getName());

	@Id
	@Setter(AccessLevel.NONE)
	private String								name;

	private List<String>						value	= new ArrayList<>();

	public static final String TYPE_STRING = "String";

	public static final String TYPE_LONG = "long";

	public static final String TYPE_BOOLEAN = "boolean";

	private String								type = TYPE_STRING;

	public static final ObjectifyDao<Setting>	DAO		= new ObjectifyDao<>(Setting.class);

	private Setting() {
	}

	public static String normalizeName(String name) {
		return Normalize.get().tag(name);
	}

	public static Setting create(String name) throws IllegalArgumentException {
		name = normalizeName(name);
		checkArgument(null != name, "name" + Constants.NOT_BLANK);
		Setting setting = new Setting();
		setting.name = name;
		return setting;
	}

	public static int getAsInt(String name, int defaultValue) {
		Optional<Setting> setting = getByName(name);
		return setting.isPresent() ? setting.get().getAsInt(defaultValue) : defaultValue;
	}

	public static long getAsLong(String name, long defaultValue) {
		Optional<Setting> setting = getByName(name);
		return setting.isPresent() ? setting.get().getAsLong(defaultValue) : defaultValue;
	}

	public static String getAsStr(String name, String defaultValue) {
		Optional<Setting> setting = getByName(name);
		return setting.isPresent() ? setting.get().getValue() : defaultValue;
	}

	public static boolean getAsBool(String name, boolean defaultValue) {
		Optional<Setting> setting = getByName(name);
		return setting.isPresent() ? setting.get().getAsBoolean() : defaultValue;
	}

	public static Optional<Setting> getByName(String name) {
		name = normalizeName(name);
		return DAO.getById(name);
	}

	@Override
	@JsonIgnore
	public String getId() {
		return getName();
	}

	public String getValue() {
		if (this.value.isEmpty())
			return EMPTY;
		return Constants.GAE_STRING_JOINER.join(this.value);
	}

	public Setting setValue(String value) {
		value = trimToNull(value);
		if (null != value) {
			this.value = Constants.GAE_STRING_SPLITTER.splitToList(value.trim());
		} else {
			this.value.clear();
		}
		return this;
	}

	protected void setSingleValue(String value) {
		this.value.clear();
		this.value.add(value);
	}

	public Setting setValue(int value) {
		setSingleValue(Integer.toString(value));
		return this;
	}

	public Setting setValue(long value) {
		setSingleValue(Long.toString(value));
		return this;
	}

	public Setting setValue(boolean value) {
		setSingleValue(Boolean.toString(value));
		return this;
	}

	@JsonIgnore
	public int getAsInt(int defaultValue) {
		try {
			return Integer.parseInt(getValue());
		} catch (NumberFormatException e) {
			LOG.log(Level.WARNING, "Error converting String value to int. Stacktrace:", e);
			return defaultValue;
		}
	}

	@JsonIgnore
	public long getAsLong(long defaultValue) {
		try {
			return Long.parseLong(getValue());
		} catch (NumberFormatException e) {
			LOG.warning("Error converting '" + getValue() + "' to long. Returned defaultValue ("
					+ defaultValue + ")");
			return defaultValue;
		}
	}

	@JsonIgnore
	public boolean getAsBoolean() {
		return Boolean.parseBoolean(getValue());
	}

}
