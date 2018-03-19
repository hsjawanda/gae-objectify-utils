/**
 *
 */
package com.hsjawanda.gaeobjectify.web;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.trimToEmpty;

import java.util.HashMap;
import java.util.Map;

import com.hsjawanda.gaeobjectify.util.Constants;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Errors {

	private Map<String, Object> errors = new HashMap<>();

	public Errors() {
	}

	public Errors add(String key, Object value) {
		checkArgument(isNotBlank(key), "key" + Constants.NOT_BLANK);
		this.errors.put(key.trim().toLowerCase(), value);
		return this;
	}

	public boolean has(String key) {
		return this.errors.containsKey(key);
	}

	public boolean hasStartingWith(String keyFragment) {
		if (isNotBlank(keyFragment)) {
			keyFragment = keyFragment.trim().toLowerCase();
			for (String key : this.errors.keySet()) {
				if (key.startsWith(keyFragment))
					return true;
			}
		}
		return false;
	}

	public Object value(String key) {
		return this.errors.get(trimToEmpty(key).toLowerCase());
	}

}
