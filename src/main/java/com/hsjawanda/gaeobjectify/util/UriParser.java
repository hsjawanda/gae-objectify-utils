/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;


/**
 * @author harsh.deep
 *
 */
public class UriParser {

	private static final Logger log = Logger.getLogger(UriParser.class.getName());

	private boolean consumeMapping;

	private boolean hasAction;

	private static Splitter splitter = Splitter.on('/').omitEmptyStrings();

	private UriParser() {
	}

	public UriInfo parse(HttpServletRequest req) {
		return this.parse(req, false);
	}

	public UriInfo parse(HttpServletRequest req, boolean debug) {
		String uri = req.getRequestURI();
		List<String> parts = splitter.splitToList(uri);
		if (debug) {
			log.info("Parts of URI: " + parts);
		}
		if (this.consumeMapping) {
			parts = parts.subList(1, parts.size());
			if (debug) {
				log.info("After consuming, parts: " + parts);
			}
		}
		String action = EMPTY;
		if (this.hasAction) {
			action = parts.get(0);
			parts = parts.subList(1, parts.size());
			if (debug) {
				log.info("After action, parts: " + parts);
			}
		}
		UriInfo info = new UriInfo(action);
		info.setParams(this.mapList(parts));
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
				retMap.put(parts.get(i), EMPTY);
			} else {
				retMap.put(parts.get(i), parts.get(i + 1));
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

		public Builder setConsumeMapping(boolean consume) {
			this._consumeMapping = consume;
			return this;
		}

		public Builder setHasAction(boolean action) {
			this._hasAction = action;
			return this;
		}
	}
}
