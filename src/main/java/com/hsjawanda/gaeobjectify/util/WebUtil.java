/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.trimToEmpty;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;


/**
 * @author harsh.deep
 *
 */
public class WebUtil {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(WebUtil.class.getName());

	protected static Whitelist list;

	protected static OutputSettings settings;

	protected WebUtil() {
	}

	public static boolean isProduction() {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}

	public static String addPassThruParams(AbstractUriInfo info, String baseUrl, String... paramNames) {
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
		if (null == list) {
			list = Whitelist.simpleText().addTags("br");
		}
		if (null == settings) {
			settings = new OutputSettings().prettyPrint(false);
		}
		String retVal = trimToEmpty(plaintext);
		retVal = Jsoup.clean(plaintext, EMPTY, list, settings);
		retVal = retVal.replaceAll("(\\\r?\\\n|\\\r)", "<br />");
		retVal = retVal.replaceAll("<br />(\\s*)<br />", "</p><p>");
		StringBuilder complete = new StringBuilder(retVal.length() + 7);
		return complete.append("<p>").append(retVal).append("</p>").toString();
	}

	public static String getServerUrl(HttpServletRequest req) {
		return req.getRequestURL().toString().replace(req.getRequestURI(), EMPTY);
	}

	public static void setCacheControl(HttpServletResponse res, boolean isPublic, int seconds) {
		StringBuilder particulars = new StringBuilder(35);
		particulars.append(isPublic ? "public" : "private").append(", max-age=").append(seconds);
		res.setHeader(HttpHeaders.CACHE_CONTROL, particulars.toString());
	}
}
