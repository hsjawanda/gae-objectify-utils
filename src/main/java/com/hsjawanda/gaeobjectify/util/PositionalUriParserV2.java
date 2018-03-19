/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Optional;


//import com.hsjawanda.gaeobjectify.util.PositionalUriParser.PositionalMatch;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class PositionalUriParserV2 {

	private static Logger	LOG			= Logger.getLogger(PositionalUriParserV2.class.getName());

	List<String>			specifierParts;

	private List<String>	consts		= new ArrayList<>(3);

	private List<String>	vars		= new ArrayList<>();

//	private List<PositionalMatch>	matchAgainst	= new ArrayList<>();

	private PositionalUriParserV2() {
	}

	private static PositionalUriParserV2 instance(String specifier) throws IllegalArgumentException {
		checkArgument(isNotBlank(specifier), "specifier" + Constants.NOT_BLANK);
		PositionalUriParserV2 parser = new PositionalUriParserV2();
		parser.specifierParts = Constants.PATH_SPLITTER.splitToList(specifier);
		for (int i = 0; i < parser.specifierParts.size(); i++) {
			String part = parser.specifierParts.get(i);
			if (part.startsWith("{") && part.endsWith("}")) {
				parser.vars.add(part.substring(1, part.length() - 1));
			} else {
				parser.consts.add(part);
			}
		}
		return parser;
	}

	public static PositionalUriParserV2 instance(String... specifier)
			throws IllegalArgumentException {
		return instance(Constants.pathJoiner.join(specifier));
	}

	public String getMapping() {
		return Constants.pathJoiner.join(this.specifierParts);
	}

	public Optional<PositionalUriInfo> parse(String uri, boolean debug) {
		if (debug) {
			LOG.info("Original URI: " + uri);
		}
		uri = Holdall.removeJSessionId(uri);
		List<String> splitList = Constants.PATH_SPLITTER.splitToList(uri);
		int argSize = splitList.size() - this.consts.size();
		if (argSize < 0) {
			if (debug) {
				LOG.info("No match because: uri (" + splitList + ") parts < specifier ("
						+ this.specifierParts + ") const parts.");
			}
			return Optional.absent();
		}
		int partNum, varNum;
		for (partNum = 0; partNum < this.consts.size(); partNum++) {
			if (!splitList.get(partNum).equalsIgnoreCase(this.consts.get(partNum)))
				return Optional.absent();
		}
		PositionalUriInfo info = new PositionalUriInfo();
		if (argSize > 0) {
			Map<String, String> args = new HashMap<String, String>(Math.min(argSize,
					this.vars.size()));
			for (varNum = 0; varNum < this.vars.size() && partNum < splitList.size(); partNum++, varNum++) {
				if (debug) {
					LOG.info("partNum = " + partNum + "; varNum = " + varNum);
				}
				args.put(this.vars.get(varNum), splitList.get(partNum));
			}
			info.setParams(args);
		}
		return Optional.of(info);
	}

	public Optional<PositionalUriInfo> parse(String uri) {
		return parse(uri, false);
	}

	public Optional<PositionalUriInfo> parse(HttpServletRequest req) {
		checkNotNull(req, "req" + Constants.NOT_NULL);
		return parse(req.getRequestURI());
	}
}
