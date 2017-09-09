/**
 * 
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

/**
 * @author hsjawanda
 *
 */
public class PropertyValues {
	
	private static final Logger LOG = Logger.getLogger(PropertyValues.class.getName());
	
	private static final String NL = System.lineSeparator();
	
	private String separator = ": ";
	
	private Map<String, Object> properties = Maps.newTreeMap();
	
	private PropertyValues(String separator) {
		separator = StringUtils.trimToNull(separator);
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
	
	public PropertyValues addProperty(String property, Object value) {
		if (StringUtils.isNotBlank(property)) {
			this.properties.put(property, value);
		}
		return this;
	}
	
	public Map<String, Object> getProperties() {
		return this.properties;
	}
	
	public String publish(boolean startWithNewline) {
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
			result.append(StringUtils.leftPad(property, longestProperty)).append(separator)
					.append(this.properties.get(property)).append(NL);
		}
		return result.toString();
	}
	
	public String publish() {
		return publish(true);
	}

}
