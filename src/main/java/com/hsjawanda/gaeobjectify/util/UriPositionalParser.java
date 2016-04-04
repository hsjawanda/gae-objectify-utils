/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Optional;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class UriPositionalParser {

	private static Logger		log			= Logger.getLogger(UriPositionalParser.class.getName());

	List<String>				specifierParts;

	private List<PositionalMatch>	matchAgainst		= new ArrayList<>();

	private int					minLength	= 0;

	private UriPositionalParser() {
	}

	public static UriPositionalParser instance(String specifier) throws IllegalArgumentException {
		checkArgument(isNotBlank(specifier), "specifier" + Constants.notBlank);
		UriPositionalParser parser = new UriPositionalParser();
		parser.specifierParts = Constants.PATH_SPLITTER.splitToList(specifier);
		parser.minLength = parser.specifierParts.size();
		for (int i = 0; i < parser.specifierParts.size(); i++) {
			String part = parser.specifierParts.get(i);
			if (part.startsWith("{") && part.endsWith("}")) {
				parser.matchAgainst.add(new PositionalMatch(i,
						part.substring(1, part.length() - 1)));
			}
		}
		log.info("matchAgainst: " + parser.matchAgainst);
		return parser;
	}

	public Optional<PositionalUriInfo> parse(String uri, boolean debug)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(uri), "uri" + Constants.notBlank);
		log.info("parse(): matchAgainst: " + this.matchAgainst + "; specifierParts: "
				+ this.specifierParts);
		List<String> uriParts = Constants.PATH_SPLITTER.splitToList(uri);
		int counter = 0;
		if (uriParts.size() != this.specifierParts.size()) {
			log.warning("The size of the uri (" + uriParts.size() + ") doesn't match that of the "
					+ "specifier (" + this.specifierParts.size() + ").");
			return Optional.absent();
		}
		Map<String, String> arguments = new HashMap<>(this.matchAgainst.size());
		for (int i = 0; i < uriParts.size(); i++) {
			PositionalMatch match = this.matchAgainst.get(counter);
			if (i == match.position) {
				arguments.put(match.name, uriParts.get(match.position));
				counter++;
			} else if (!uriParts.get(i).equalsIgnoreCase(this.specifierParts.get(i))) {
				log.warning("uri (" + uri + ") doesn't match the specified pattern (/"
						+ Constants.pathJoiner.join(this.specifierParts) + ")");
				return Optional.absent();
			}
		}
		PositionalUriInfo info = new PositionalUriInfo();
		log.info("arguments: " + arguments);
		info.setParams(arguments);
		return Optional.of(info);
	}

	public Optional<PositionalUriInfo> parse(HttpServletRequest req, boolean debug) {
		if (null == req)
			return Optional.absent();
		return parse(req.getRequestURI(), debug);
	}

	public Optional<PositionalUriInfo> parse(HttpServletRequest req) {
		if (null == req)
			return Optional.absent();
		return parse(req.getRequestURI(), false);
	}

	private static class PositionalMatch {

		private int		position;

		private String	name;

		private PositionalMatch(int position, String name) {
			this.position = position;
			this.name = name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "PositionalMatch [position=" + this.position + ", name=" + this.name + "]";
		}
	}

}
