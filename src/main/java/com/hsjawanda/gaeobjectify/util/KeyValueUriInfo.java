/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.defaultString;


/**
 * @author harsh.deep
 *
 */
public class KeyValueUriInfo extends AbstractUriInfo {

	public final String action;

	KeyValueUriInfo(String action) {
		this.action = defaultString(action);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("Action: ").append(this.action).append(System.lineSeparator());
		sb.append("Parameters: ").append(this.args);
		return sb.toString();
	}
}
