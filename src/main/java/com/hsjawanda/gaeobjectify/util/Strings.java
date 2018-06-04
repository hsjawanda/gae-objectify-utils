/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.defaultString;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.mail.internet.InternetAddress;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Strings {

	private static final Logger log = Logger.getLogger(Strings.class.getName());

	public static final String NULL = "null";

	private Strings() {
	}

	@Nonnull
	public static String toString(InternetAddress addr) {
		return String.format("%s <%s>", defaultString(addr.getPersonal(), NULL),
				defaultString(addr.getAddress(), NULL));
	}

	@Nonnull
	public static String toString(Object obj) {
		String retVal = null == obj ? EMPTY : obj.toString();
		return null == retVal ? EMPTY : retVal;
	}

	public static String toString(byte[] input) {
		return null == input ? NULL : new String(input);
	}

	public static String toString(char[] input) {
		return null == input ? EMPTY : new String(input);
	}

	public static String toString(String input) {
		return null == input ? NULL : input;
	}

	public static Long getAsLong(String stringValue) {
		if (null == stringValue)
			return null;
		try {
			return Long.valueOf(stringValue);
		} catch (NumberFormatException e) {
			log.warning("Exception converting '" + stringValue + "' to Long: "
					+ System.lineSeparator() + e.getMessage());
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception converting '" + stringValue + "' to Long...", e);
		}
		return null;
	}

	public static long getAsLong(String stringValue, long defaultValue) {
		if (isNotBlank(stringValue)) {
			try {
				return Long.parseLong(stringValue);
			} catch (NumberFormatException e) {
				log.warning(
						"Exception converting '" + stringValue + "' to long: " + e.getMessage());
			} catch (Exception e) {
				log.log(Level.WARNING,
						"Exception converting '" + stringValue + "' to long. Stacktrace:", e);
			}
		}
		return defaultValue;
	}

	public static int getAsInt(String stringValue, int defaultValue) {
		if (isNotBlank(stringValue)) {
			try {
				return Integer.parseInt(stringValue);
			} catch (NumberFormatException e) {
				log.warning(
						"Exception converting '" + stringValue + "' to int: " + e.getMessage());
			} catch (Exception e) {
				log.log(Level.WARNING,
						"Exception converting '" + stringValue + "' to int. Stacktrace:", e);
			}
		}
		return defaultValue;
	}

	public static Integer getAsInteger(String stringValue) {
		if (isNotBlank(stringValue)) {
			try {
				return Integer.valueOf(stringValue);
			} catch (NumberFormatException e) {
				log.warning(
						"Exception converting '" + stringValue + "' to Integer: " + e.getMessage());
			} catch (Exception e) {
				log.log(Level.WARNING,
						"Exception converting '" + stringValue + "' to Integer. Stacktrace:", e);
			}
		}
		return null;
	}

	public static Double getAsDouble(String stringValue) {
		if (null == stringValue)
			return null;
		try {
			return Double.valueOf(stringValue);
		} catch (NumberFormatException e) {
			log.warning("Exception converting '" + stringValue + "' to Double: "
					+ System.lineSeparator() + e.getMessage());
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception converting '" + stringValue + "' to Double...", e);
		}
		return null;
	}

	public static Boolean getAsBoolean(String stringValue) {
		if (null == stringValue)
			return null;
		return Boolean.valueOf(stringValue);
	}

}
