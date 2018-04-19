/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Maps;
import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils;

/**
 * @author hsjawanda
 *
 */
public class PropertyValues {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PropertyValues.class.getName());

	private static final String NL = System.lineSeparator();

	private String separator = ": ";

	private Map<String, Object> properties = Maps.newTreeMap();

	private PropertyValues(String separator) {
//		separator = StringUtils.trimToNull(separator);
		if (null != separator) {
			this.separator = separator + " ";
		}
	}

	public static PropertyValues create() {
		return new PropertyValues(null);
	}

	public static PropertyValues create(String separator) {
		return new PropertyValues(separator);
	}

	public PropertyValues addProperties(Map<String, Object> values) {
		if (null != values) {
			for (String key : values.keySet()) {
				addProperty(key, values.get(key));
			}
		}
		return this;
	}

	public PropertyValues addProperty(String property, Object value) {
		if (StringUtils.isNotBlank(property)) {
			this.properties.put(property, value);
		}
		return this;
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public String publish(boolean startWithNewline, boolean endWithNewline) {
		StringBuilder result = new StringBuilder(this.properties.size() * 100 + 10);
		if (startWithNewline) {
			result.append(NL);
		}
		int longestProperty = 0;
		for (String property : this.properties.keySet()) {
			if (property.length() > longestProperty) {
				longestProperty = property.length();
			}
		}
		for (String property : this.properties.keySet()) {
			result.append(NL).append(StringUtils.leftPad(property, longestProperty)).append(this.separator)
					.append(this.properties.get(property));
		}
		if (endWithNewline) {
			result.append(NL);
		}
		return result.toString();
	}

	public String publish(boolean startWithNewline) {
		return publish(startWithNewline, true);
	}

	public String publish() {
		return publish(false);
	}

}
