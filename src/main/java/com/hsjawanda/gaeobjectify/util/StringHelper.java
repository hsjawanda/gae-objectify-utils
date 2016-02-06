/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.logging.Logger;

import javax.annotation.Nonnull;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class StringHelper {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(StringHelper.class.getName());

	private StringHelper() {
	}

	@Nonnull
	@SuppressWarnings("null")
	public static String toString(Object obj) {
		if (null == obj)
			return EMPTY;
		String retVal = obj.toString();
		return null == retVal ? EMPTY : retVal;
	}

	public static String toString(byte[] input) {
		return null == input ? EMPTY : new String(input);
	}

	public static String toString(char[] input) {
		return null == input ? EMPTY : new String(input);
	}

	public static String toString(String input) {
		return null == input ? EMPTY : input;
	}

}
