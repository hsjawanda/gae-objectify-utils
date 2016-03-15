/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import com.google.common.base.CaseFormat;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public enum GmapsPlaceType {

	BAR, RESTAURANT, CAFE;

	private final String stringRepresentation;

	private GmapsPlaceType() {
		this.stringRepresentation = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE,
				name());
	}

	@Override
	public String toString() {
		return this.stringRepresentation;
	}
}
