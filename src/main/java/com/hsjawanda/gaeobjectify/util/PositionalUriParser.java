/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

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
public class PositionalUriParser {

	private static Logger			log				= Logger
			.getLogger(PositionalUriParser.class.getName());

	List<String>					specifierParts;

	private int						fixedParts = 0;

	private List<PositionalMatch>	matchAgainst	= new ArrayList<>();

	private PositionalUriParser() {
	}

	public static PositionalUriParser instance(String specifier) throws IllegalArgumentException {
		checkArgument(isNotBlank(specifier), "specifier" + Constants.NOT_BLANK);
		PositionalUriParser parser = new PositionalUriParser();
		parser.specifierParts = Constants.PATH_SPLITTER.splitToList(specifier);
		for (int i = 0; i < parser.specifierParts.size(); i++) {
			String part = parser.specifierParts.get(i);
			if (part.startsWith("{") && part.endsWith("}")) {
				parser.matchAgainst.add(new PositionalMatch(i,
						part.substring(1, part.length() - 1)));
				if (0 == parser.fixedParts) {
					parser.fixedParts = i;
				}
			}
		}
		if (0 == parser.fixedParts) {
			parser.fixedParts = parser.specifierParts.size();
		}
//		log.info("matchAgainst: " + parser.matchAgainst);
		return parser;
	}

	public Optional<PositionalUriInfo> parse(String uri, boolean debug)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(uri), "uri" + Constants.NOT_BLANK);
		uri = Holdall.removeJSessionId(uri);
		List<String> uriParts = Constants.PATH_SPLITTER.splitToList(uri);
		int counter = 0;
		if (uriParts.size() < this.specifierParts.size()) {
			if (debug) {
				log.info("The size of the uri (" + uriParts.size() + ") doesn't match that of the "
						+ "specifier (" + this.specifierParts.size() + ").");
			}
			return Optional.absent();
		}
		for (int i = 0; i < this.fixedParts; i++) {
			if (!uriParts.get(i).equalsIgnoreCase(this.specifierParts.get(i)))
				return Optional.absent();
		}
		if (debug) {
			log.info("uri: " + uri + "; uriParts: " + uriParts + "; specifierParts: "
					+ this.specifierParts);
		}
		Map<String, String> arguments = new HashMap<>(this.matchAgainst.size());
		for (int i = 0; i < uriParts.size() && this.matchAgainst.size() > 0; i++) {
			PositionalMatch match = this.matchAgainst.get(counter);
			if (i == match.position) {
				arguments.put(match.name, uriParts.get(match.position));
				counter++;
			} else if (!uriParts.get(i).equalsIgnoreCase(this.specifierParts.get(i))) {
				log.fine("uri (" + uri + ") doesn't match the specified pattern (/"
						+ Constants.pathJoiner.join(this.specifierParts) + ")");
				return Optional.absent();
			}
			if (counter == this.matchAgainst.size()) {
				break;
			}
		}
		PositionalUriInfo info = new PositionalUriInfo();
//		log.info("arguments: " + arguments);
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
