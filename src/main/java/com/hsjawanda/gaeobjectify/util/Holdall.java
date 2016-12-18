/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Entity;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public final class Holdall {

	private static Logger LOG = Logger.getLogger(Holdall.class.getName());

	private Holdall() {
	}

	/**
	 * Convert a stack trace to a {@code String}.
	 *
	 * @param e
	 *            the {@code Exception} whose stacktrace requires conversion
	 * @return the stacktrace as a {@code String}
	 */
	public static String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter(500);
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * <p>
	 * Compact an address {@code List<String>} to have a size no larger than {@code targetSize}.
	 *
	 * <p>
	 * An "address list" is a {@code List<String>} where each address line (not counting city,
	 * state, postal code or country) is one element in the {@code List}.
	 *
	 * <p>
	 * Compaction is done by joining lines 1 and 2 of original list and checking if size condition
	 * is met. If not, combine line 3 and 4 of original list and check size; so on and so forth.
	 *
	 * @param alist
	 *            the {@code List<String>} to compact
	 * @param targetSize
	 *            ensure {@code alist.size() <= targetSize}
	 */
	public static void compactList(List<String> alist, int targetSize) {
		if (null == alist)
			return;
		int start = 0;
		while (alist.size() > targetSize) {
			String compacted = Constants.ADDR_JOIN.join(alist.subList(start, start + 2));
			alist.set(start++, compacted);
			alist.remove(start);
			if (start > alist.size() - 2) {
				start = 0;
			}
		}
	}

	/**
	 * Print a {@code List<T>} to {@code System.out} with index numbers.
	 *
	 * @param alist
	 *            the {@code List<T>} to print
	 */
	public static <T> void printList(List<T> alist) {
		if (null != alist) {
			int counter = 0;
			for (T element : alist) {
				System.out.println(String.format("%5d: %s", counter++, element));
			}
		}
	}

	public static <T> void checkIfObjectifyEntity(Class<T> cls)
			throws UnsupportedOperationException {
		if (!cls.isAnnotationPresent(Entity.class))
			throw new UnsupportedOperationException("The class " + cls.getName()
					+ " doesn't have the annotation " + Entity.class.getName()
					+ " and therefore can't be used with this operation.");
	}

	public static void sleep(long millis) {
		if (millis < 1)
			return;
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOG.warning("Sleep of " + millis + " ms interrupted.");
		}
	}

	public static Optional<String> urlDecode(String encodedStr) {
		if (null == encodedStr)
			return Optional.absent();
		try {
			return Optional.of(URLDecoder.decode(encodedStr, Constants.UTF_8));
		} catch (UnsupportedEncodingException e) {
			return Optional.absent();
		}
	}

	public static String removeJSessoinId(String origUri) {
		if (null == origUri)
			return EMPTY;
		return origUri.replaceAll("(?i);JSESSIONID=.*", EMPTY);
	}

}
