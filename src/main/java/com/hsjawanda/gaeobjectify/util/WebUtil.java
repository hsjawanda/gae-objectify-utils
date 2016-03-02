/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Optional;


/**
 * @author harsh.deep
 *
 */
public class WebUtil {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(WebUtil.class.getName());

	protected static final Whitelist list = Whitelist.simpleText().addTags("br");

	protected static final OutputSettings settings = new OutputSettings().prettyPrint(false);

	protected WebUtil() {
	}

	public static boolean isProduction() {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}

	public static String addPassThruParams(UriInfo info, String baseUrl, String... paramNames) {
		StringBuilder retUrl = new StringBuilder(60);
		if (baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		retUrl.append(baseUrl);
		if (null != info) {
			Optional<String> paramVal;
			for (String paramName : paramNames) {
				paramVal = info.getParam(paramName);
				if (paramVal.isPresent()) {
					retUrl.append("/");
					Constants.pathJoiner.appendTo(retUrl, paramName, paramVal.get());
				}
			}
		}
		return retUrl.toString();
	}

	public static String plaintext2Html(String plaintext) {
		String retVal = trimToEmpty(plaintext);
		retVal = Jsoup.clean(plaintext, EMPTY, list, settings);
		retVal = retVal.replaceAll("(\\\r?\\\n|\\\r)", "<br />");
		retVal = retVal.replaceAll("<br />(\\s*)<br />", "</p><p>");
		StringBuilder complete = new StringBuilder(retVal.length() + 7);
		return complete.append("<p>").append(retVal).append("</p>").toString();
	}

	public static String getServerURL(HttpServletRequest req) {
		return req.getRequestURL().toString().replace(req.getRequestURI(), EMPTY);
	}
}
