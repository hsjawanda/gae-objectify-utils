/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;


/**
 * @author harsh.deep
 *
 */
public class UriInfo {

	private ImmutableMap<String, String> args;

	public final String action;

	UriInfo(String action) {
		this.action = defaultString(action);
	}

	void setParams(Map<String, String> params) {
		this.args = ImmutableMap.copyOf(params);
	}

	public Optional<String> getParam(String key) {
		if (null == key)
			return Optional.absent();
		return Optional.fromNullable(this.args.get(key.toLowerCase()));
	}

	public boolean containsParam(String key) {
		if (null == key)
			return false;
		return this.args.containsKey(key.toLowerCase());
	}

	public ImmutableMap<String, String> getParams() {
		return this.args;
	}
	//
	// void setAction(String action) {
	// this.action = action;
	// }
	//
	// public String getAction() {
	// return defaultString(this.action);
	// }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("Action: ").append(this.action).append(System.lineSeparator());
		sb.append("Parameters: ").append(this.args);
		return sb.toString();
	}
}
