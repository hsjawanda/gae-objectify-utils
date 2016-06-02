/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class Normalize {

	private static final Normalize INSTANCE = new Normalize();

	private Normalize() {}

	public static Normalize get() {
		return INSTANCE;
	}

	public String email(String email) {
		email = trimToNull(email);
		return null == email ? null : email.toLowerCase();
	}

	public String role(String role) throws NullPointerException {
		role = checkNotNull(normalizeSpace(role), "role" + Constants.notNull);
		return role.toLowerCase().replaceAll("[^-\\p{Alnum} ]", "").replace(' ', '-')
				.replaceAll("-{2,}", "-");
	}

}
