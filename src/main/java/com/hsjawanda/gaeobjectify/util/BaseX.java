/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class BaseX {

	private String charset;

	private int length;

	public static final BaseX URL_SAFE_BASE64 = get("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
			+ "_-");

	private BaseX(String charset) {
		this.charset = charset;
		this.length = this.charset.length();
	}

	public static BaseX get(String charset) throws NullPointerException, IllegalArgumentException {
		checkNotNull(charset, "charset" + Constants.NOT_NULL);
		checkArgument(charset.length() >= 10, "charset must have a minimum length of 10.");
		checkArgument(charset.length() <= 128, "charset can have a maximum length of 128.");
		return new BaseX(charset);
	}

	public String encode10(long number) throws IllegalArgumentException {
		checkArgument(number >= 0, "Only non-negative numbers can be encoded");
		StringBuilder encoded = new StringBuilder(20);
		if (number == 0)
			return encoded.append(this.charset.charAt(0)).toString();
		encode10(number, encoded);
		return encoded.reverse().toString();
	}

	private void encode10(long number, StringBuilder encoded) {
		while (number > 0) {
			long remainder = number % this.length;
			number = number / this.length;
			encoded.append(this.charset.charAt((int) remainder));
		}
	}

	public long decode10(String number) {
		checkNotNull(number, "number" + Constants.NOT_NULL);
		long retVal = 0;
		for (int i = 0; i < number.length(); i++) {
			int index = this.charset.indexOf(number.charAt(i));
			if (index == -1)
				throw new IllegalArgumentException("This number wasn't generated using this encoder.");
			retVal = retVal * this.length + index;
		}
		return retVal;
	}

}
