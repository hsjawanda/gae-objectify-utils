/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.defaultString;

import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public final class Numbers {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(Numbers.class.getName());

	private Numbers() {}

	public static long randomizeByPercent(final long value, final long percent) throws IllegalArgumentException {
		if (percent < 0 || percent > 100)
			throw new IllegalArgumentException("percent must be in the range [0, 100]");
		long randomizationAmount = value * percent / 100;
		return ((long) Math.random() * 2 * randomizationAmount) + value - randomizationAmount;
	}

	public static int paddingRequiredFor(long maxNumber) {
		return (int) Math.floor(Math.log10(maxNumber)) + 1;
	}

	public static String maxFrequencyFragment(long seconds) {
		long timeMillis = seconds * 1000;
		return Long.toString(System.currentTimeMillis() / timeMillis);
	}

	@Nullable
	public static Long longValueOf(String idStr) {
		try {
			return Long.valueOf(defaultString(idStr));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
