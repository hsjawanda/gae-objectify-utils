/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class UriParser {

	private static final Logger log = Logger.getLogger(UriParser.class.getName());

	private boolean consumeMapping = true;

	private boolean hasAction;

	private static Splitter splitter = Splitter.on('/').omitEmptyStrings();

	private UriParser() {
	}

	/**
	 * See {@link #parse(String, boolean)} for detailed documentation.
	 *
	 * @param req
	 *            the request object
	 * @return see {@link #parse(String, boolean)}
	 */
	public UriInfo parse(HttpServletRequest req) {
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
	public UriInfo parse(HttpServletRequest req, boolean debug) {
		if (null == req)
			return parse(EMPTY, debug);
		return parse(req.getRequestURI(), debug);
	}

	/**
	 * <p>
	 * Parse the URI and create a {@link UriInfo} object with the appropriate values.
	 *
	 * <p>
	 * The {@code action} and {@code param}s (not the values of the {@code param}s) in the returned
	 * {@link UriInfo} object will be lower-cased. Any {@code param} without a corresponding
	 * {@code value} in the {@code uri} will have the empty {@code String} as its value.
	 *
	 * @param uri
	 *            the {@code uri} to parse.
	 * @param debug
	 *            if {@code true}, debug information will be logged.
	 * @return a {@code UriInfo} object representing data from the {@code uri}. Never {@code null}.
	 */
	public UriInfo parse(String uri, boolean debug) {
		uri = trimToEmpty(uri);
		List<String> parts = splitter.splitToList(uri);
		if (debug) {
			log.info("Parts of URI: " + parts);
		}
		if (this.consumeMapping && !parts.isEmpty()) {
			parts = parts.subList(1, parts.size());
			if (debug) {
				log.info("After consuming, parts: " + parts);
			}
		}
		String action = EMPTY;
		if (this.hasAction && !parts.isEmpty()) {
			action = parts.get(0).toLowerCase();
			parts = parts.subList(1, parts.size());
			if (debug) {
				log.info("After action, parts: " + parts);
			}
		}
		UriInfo info = new UriInfo(action);
		info.setParams(mapList(parts));
		return info;
	}

	protected Map<String, String> mapList(List<String> parts) {
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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private boolean _consumeMapping = true;

		private boolean _hasAction = true;

		private Builder() {
		}

		public UriParser build() {
			UriParser parser = new UriParser();
			parser.consumeMapping = this._consumeMapping;
			parser.hasAction = this._hasAction;
			return parser;
		}

//		public Builder setConsumeMapping(boolean consume) {
//			this._consumeMapping = consume;
//			return this;
//		}

		public Builder setHasAction(boolean action) {
			this._hasAction = action;
			return this;
		}
	}
}
