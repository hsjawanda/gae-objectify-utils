/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import lombok.Builder;

import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.RandomStringUtils;
import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils;
import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.tuple.ImmutablePair;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.trimToEmpty;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableListIterator;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Passwords {

	private int minLowerChars, minUpperChars, minSpecialChars, minDigits, minLength;

	private static int _minLength = 8;

	private static String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
			+ "0123456789@!#$%*^";

	private static CharMatcher validMatcher = CharMatcher.anyOf(validChars);

	private Passwords() {
	}

	@Builder
	private Passwords(Long minLowerChars, Long minUpperChars, Long minSpecialChars, Long minDigits,
			Long minLength) {
		this.minLowerChars = null == minLowerChars ? 1 : (int) Math.max(0L, minLowerChars);
		this.minUpperChars = null == minUpperChars ? 1 : (int) Math.max(0L, minUpperChars);
		this.minSpecialChars = null == minSpecialChars ? 1 : (int) Math.max(0L, minSpecialChars);
		this.minDigits = null == minDigits ? 1 : (int) Math.max(0L, minDigits);
		this.minLength = null == minLength ? _minLength : (int) Math.max(_minLength, minLength);
	}

	public ImmutablePair<Boolean, String> isValidPassword(String pwd) {
		pwd = trimToEmpty(pwd);
		StringBuilder failureReason = new StringBuilder(
				"Invalid password. Doesn't meet the following requirement(s): ");
		if (pwd.length() < this.minLength) {
			addReason("minimum length", this.minLength, failureReason);
			return ImmutablePair.of(Boolean.FALSE, failureReason.toString());
		}
		int loChars = 0, upChars = 0, spChars = 0, numbers = 0, invalidChars = 0;
		ImmutableList<Character> chars = Lists.charactersOf(pwd);
		UnmodifiableListIterator<Character> iter = chars.listIterator();
		while (iter.hasNext()) {
			char c = iter.next();
			if (!validMatcher.matches(c)) {
				invalidChars++;
			}
			if (c >= 'a' && c <= 'z') {
				loChars++;
			} else if (c >= 'A' && c <= 'Z') {
				upChars++;
			} else if (c >= '0' && c <= '9') {
				numbers++;
			} else {
				spChars++;
			}
		}
		if (loChars < this.minLowerChars) {
			addReason("minimum lower-case characters", this.minLowerChars, failureReason);
		}
		if (upChars < this.minUpperChars) {
			addReason("minimum upper-case characters", this.minUpperChars, failureReason);
		}
		if (spChars < this.minSpecialChars) {
			addReason("minimum special characters", this.minSpecialChars, failureReason);
		}
		if (numbers < this.minDigits) {
			addReason("minimum numbers", this.minDigits, failureReason);
		}
		if (invalidChars > 0) {
			addReason("contains invalid characters (allowed: " + validChars + ")", 0,
					failureReason);
		}
		if (loChars >= this.minLowerChars && upChars >= this.minUpperChars
				&& spChars >= this.minSpecialChars && numbers >= this.minDigits)
			return ImmutablePair.of(Boolean.TRUE, StringUtils.EMPTY);
		else
			return ImmutablePair.of(Boolean.FALSE, failureReason.toString());
	}

	private void addReason(String partReason, int num, StringBuilder failureReason) {
		failureReason.append(partReason).append(" (").append(num).append("); ");
	}

	public String genRandomPassword(int length) {
		return RandomStringUtils.random(length, validChars);
	}

	public String genRandomPassword() {
		return genRandomPassword(this.minLength);
	}

	public static String genRandom() {
		int length = _minLength + 5;
		return RandomStringUtils.random(length, validChars);
	}

}
