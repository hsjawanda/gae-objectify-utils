/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import com.google.common.base.Optional;


/**
 * @author harsh.deep
 *
 */
public class PositionalUriInfo extends AbstractUriInfo {


	PositionalUriInfo() {
	}

	@Override
	public Optional<String> getParam(String key) {
		return super.getParam(key, false);
	}

	@Override
	public boolean containsParam(String key) {
		return super.containsParam(key, false);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("Parameters: ").append(this.args);
		return sb.toString();
	}
}
