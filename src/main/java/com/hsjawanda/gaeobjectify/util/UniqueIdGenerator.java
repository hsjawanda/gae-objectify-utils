/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * @author Harsh.Deep
 *
 */
public class UniqueIdGenerator {

	public static final int TAB_RADIX = 36;

	public static final int UID_RADIX = 36;

	public UniqueIdGenerator() {
	}

	private static SecureRandom random = new SecureRandom();

	public static String next() {
		return new BigInteger(130, random).toString(UID_RADIX);
	}

	public static BigInteger tabId() {
		return new BigInteger(31, random);
	}

	public static int tabIdAsInt() {
		return tabId().intValue();
	}

	public static String tabIdAsString() {
		return tabId().toString(TAB_RADIX);
	}
}
