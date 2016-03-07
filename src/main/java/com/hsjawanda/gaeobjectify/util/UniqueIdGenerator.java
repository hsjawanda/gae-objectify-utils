/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.google.common.collect.Range;


/**
 * @author Harsh.Deep
 *
 */
public class UniqueIdGenerator {

	public static final int TAB_RADIX = 36;

	public static final int UID_RADIX = 36;

	public static final int MIN_RADIX = 2;

	public static final int MAX_RADIX = 60;

	private static final Range<Integer> radixRange = Range.closed(MIN_RADIX, MAX_RADIX);

	public static final char[] charPool = new char[MAX_RADIX];

	static {
		int i = 0;
		char allowed;
		for (allowed = '0'; allowed <= '9'; allowed++) {
			charPool[i++] = allowed;
		}
		for (allowed = 'a'; allowed <= 'z'; allowed++) {
			charPool[i++] = allowed;
		}
		for (allowed = 'A'; allowed <= 'Z'; allowed++) {
			if (allowed == 'O' || allowed == 'I') {
				continue;
			}
			charPool[i++] = allowed;
		}
	}

	public UniqueIdGenerator() {
	}

	private static SecureRandom random = new SecureRandom();

	public static String next() {
		return new BigInteger(130, random).toString(UID_RADIX);
	}

	public static BigInteger bigNumber() {
		return new BigInteger(130, random);
	}

	public static String big() {
		return asString(bigNumber(), MAX_RADIX);
	}

//	public static BigInteger tabId() {
//		return new BigInteger(31, random);
//	}

	public static BigInteger mediumNumber() {
		return new BigInteger(64, random);
	}

	public static String medium() {
		return asString(mediumNumber(), MAX_RADIX);
	}

//	public static int tabIdAsInt() {
//		return tabId().intValue();
//	}
//
//	public static String tabIdAsString() {
//		return tabId().toString(TAB_RADIX);
//	}

	public static String asString(BigInteger bigInt, int radix) {
		if (!radixRange.contains(radix)) {
			radix = 10;
		}
		BigInteger radixBigInt = BigInteger.valueOf(radix);
		BigInteger[] divAndRem;
		StringBuilder sb = new StringBuilder(30);
		bigInt = bigInt.abs();
		while (bigInt.compareTo(BigInteger.ZERO) > 0) {
			divAndRem = bigInt.divideAndRemainder(radixBigInt);
			sb.append(charPool[divAndRem[1].intValue()]);
			bigInt = divAndRem[0];
		}
		return sb.reverse().toString();
	}
}
