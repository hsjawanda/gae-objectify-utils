/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class UriParser {

	private static final Logger log = Logger.getLogger(UriParser.class.getName());

	private String mapping;

	private boolean hasAction;

	private UriParser() {
	}

	public static UriParser instance(String mapping, boolean hasAction)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(mapping), "mapping" + Constants.NOT_BLANK);
		UriParser parser = new UriParser();
		parser.mapping = mapping.trim();
		parser.hasAction = hasAction;
		return parser;
	}

	/**
	 * See {@link #parse(String, boolean)} for detailed documentation.
	 *
	 * @param req
	 *            the request object
	 * @return see {@link #parse(String, boolean)}
	 */
	public KeyValueUriInfo parse(HttpServletRequest req) {
		return this.parse(req, false);
	}

	/**
	 * See {@link #parse(String, boolean)} for detailed documentation.
	 *
	 * @param req
	 *            the request object.
	 * @param debug
	 *            if {@code true}, debug information will be logged.
	 * @return see {@link #parse(String, boolean)}.
	 */
	public KeyValueUriInfo parse(HttpServletRequest req, boolean debug) {
		if (null == req)
			return parse(EMPTY, debug);
		return parse(req.getRequestURI().replace(this.mapping, EMPTY), debug);
	}

	/**
	 * <p>
	 * Parse the URI and create a {@link KeyValueUriInfo} object with the appropriate values.
	 *
	 * <p>
	 * The {@code action} and {@code param}s (not the values of the {@code param}s) in the returned
	 * {@link KeyValueUriInfo} object will be lower-cased. Any {@code param} without a corresponding
	 * {@code value} in the {@code uri} will have the empty {@code String} as its value.
	 *
	 * @param uri
	 *            the {@code uri} to parse (typically obtained by {@code request.getPathInfo()}).
	 * @param debug
	 *            if {@code true}, debug information will be logged.
	 * @return a {@code KeyValueUriInfo} object representing data from the {@code uri}. Never {@code null}.
	 */
	public KeyValueUriInfo parse(String uri, boolean debug) {
		String origUri = trimToEmpty(uri);
		uri = Holdall.removeJSessoinId(origUri);
		if (debug) {
			log.info("origUri: " + origUri + "; uri: " + uri);
		}
		List<String> parts = Constants.PATH_SPLITTER.splitToList(uri);
		if (debug) {
			log.info("Parts of pathInfo: " + parts);
		}
		String action = EMPTY;
		if (this.hasAction && !parts.isEmpty()) {
			action = parts.get(0)/*.toLowerCase()*/;
			parts = parts.subList(1, parts.size());
			if (debug) {
				log.info("After consuming action, remaining parts: " + parts);
			}
		}
		KeyValueUriInfo info = new KeyValueUriInfo(action);
		info.setParams(listToMap(parts));
		return info;
	}

	protected Map<String, String> listToMap(List<String> parts) {
		int size = parts.size();
		int capacity = size / 2;
		if (size % 2 == 1) {
			capacity++;
		}
		Map<String, String> retMap = new HashMap<>(capacity);
		for (int i = 0; i < size; i += 2) {
			if (i == size - 1) {
				retMap.put(parts.get(i).toLowerCase(), EMPTY);
			} else {
				retMap.put(parts.get(i).toLowerCase(), parts.get(i + 1));
			}
		}
		return retMap;
	}

//	public static Builder builder() {
//		return new Builder();
//	}
//
//	public static class Builder {
//
//		private boolean _hasAction = true;
//
//		private Builder() {
//		}
//
//		public UriParser build() {
//			UriParser parser = new UriParser();
//			parser.hasAction = this._hasAction;
//			return parser;
//		}
//
//		public Builder setHasAction(boolean action) {
//			this._hasAction = action;
//			return this;
//		}
//	}
}
