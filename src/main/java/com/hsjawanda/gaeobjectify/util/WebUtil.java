/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Optional;


/**
 * @author harsh.deep
 *
 */
public class WebUtil {
	private WebUtil() {
	}

	public static boolean isProduction() {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}

	public static String addPassThruParams(UriInfo info, String baseUrl,
			String... paramNames) {
		StringBuilder retUrl = new StringBuilder(baseUrl);
		Optional<String> paramVal;
		for (String paramName : paramNames) {
			paramVal = info.getParam(paramName);
			if (paramVal.isPresent()) {
				retUrl.append("/").append(paramName).append("/").append(paramVal.get());
			}
		}
		return retUrl.toString();
	}
}
