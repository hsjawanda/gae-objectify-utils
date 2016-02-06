/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableListIterator;

import lombok.Builder;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Passwords {

	private int minLowerChars, minUpperChars, minSpecialChars, minDigits, minLength;

	private static int _minLoChars = 0, _minUpChars = 0, _minSpChars = 0, _minNumbers = 0,
			_minLength = 8;

	private static String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "0123456789@!#$%*";

	private static CharMatcher validMatcher = CharMatcher.anyOf(validChars);

	private static Passwords instance;

	private Passwords() {
	}

	@Builder
	private Passwords(int minLowerChars, int minUpperChars, int minSpecialChars, int minDigits,
			int minLength) {
		this.minLowerChars = Math.max(0, minLowerChars);
		this.minUpperChars = Math.max(0, minUpperChars);
		this.minSpecialChars = Math.max(0, minSpecialChars);
		this.minDigits = Math.max(0, minDigits);
		this.minLength = Math.max(8, minLength);
	}

	public static Passwords getInstance() {
		if (null == instance) {
			instance = new Passwords();
			instance.minLowerChars = _minLoChars;
			instance.minUpperChars = _minUpChars;
			instance.minSpecialChars = _minSpChars;
			instance.minDigits = _minNumbers;
			instance.minLength = _minLength;
		}
		return instance;
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

	public static String genRandom() {
		int length = _minLength + 5;
		return RandomStringUtils.random(length, validChars);
	}

	public static void setMinLowerChars(int minNum) {
		checkArgument(minNum >= _minLoChars,
				"Minimum number of lower-case characters has to be >= " + _minLoChars);
		_minLoChars = minNum;
	}

	public static void setMinUpperChars(int minNum) {
		checkArgument(minNum >= _minUpChars,
				"Minimum number of upper-case characters has to be >= " + _minUpChars);
		_minUpChars = minNum;
	}

	public static void setMinSpecialChars(int minNum) {
		checkArgument(minNum >= _minSpChars,
				"Minimum number of special characters has to be >= " + _minSpChars);
		_minSpChars = minNum;
	}

	public static void setMinNumbers(int minNum) {
		checkArgument(minNum >= 0,
				"Minimum number of digit characters has to be >= " + _minNumbers);
		_minNumbers = minNum;
	}

	public static void setMinLength(int minNum) {
		checkArgument(minNum >= _minLength, "Minimum length has to be >= " + _minLength);
		_minLength = minNum;
	}
}
