/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class Slugs {

	public static String toSlug(String string) {
		return Normalizer.normalize(string.toLowerCase(), Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // Replace diacritical marks
				.replaceAll("[^\\p{Alnum}]+$", "") // Remove trailing non-alphanumeric
				.replaceAll("^[^\\p{Alnum}]+", "") // Remove leading non-alphanumeric
				.replaceAll("[^\\p{Alnum}]+", "-") // Replace non-alphanumeric with -
				.replaceAll("-{2,}", "-"); // Collapse two or more - into single -
	}

}
