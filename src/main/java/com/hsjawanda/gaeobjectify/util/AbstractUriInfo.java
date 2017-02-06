/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public abstract class AbstractUriInfo {

	protected ImmutableMap<String, String> args = ImmutableMap.of();

	void setParams(Map<String, String> params) {
		this.args = ImmutableMap.copyOf(params);
	}

	protected Optional<String> getParam(String key, boolean caseInsensitive) {
		if (null == key)
			return Optional.absent();
		String retVal = caseInsensitive ? this.args.get(key.toLowerCase()) : this.args.get(key);
		return Optional.fromNullable(retVal);
	}

	public Optional<String> getParam(String key) {
		return getParam(key, true);
	}

	protected boolean containsParam(String key, boolean caseInsensitive) {
		if (null == key)
			return false;
		return this.args.containsKey(key.toLowerCase());
	}

	public boolean containsParam(String key) {
		return containsParam(key, true);
	}

	public ImmutableMap<String, String> getParams() {
		return this.args;
	}

}
