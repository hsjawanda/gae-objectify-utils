/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class SplitJoin {

	public static String token = ":::";

	protected static Joiner joiner = Joiner.on(token).skipNulls();

	protected static final Splitter splitter = Splitter.on(token);

	private SplitJoin() {
	}

	public static List<String> split(String input) {
		return splitter.splitToList(input);
	}

	public static String join(String... inputs) {
		return joiner.join(inputs);
	}
}
