/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.trimToNull;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class Normalize {

	private static final Normalize	INSTANCE	= new Normalize();

	private Normalize() {
	}

	public static Normalize get() {
		return INSTANCE;
	}

	public String email(String email) {
		email = trimToNull(email);
		return null == email ? null : email.toLowerCase();
	}

	public String role(String role) throws NullPointerException {
		role = checkNotNull(normalizeSpace(role), "role" + Constants.NOT_NULL);
		return role.toLowerCase().replaceAll("[^-\\p{Alnum} ]", "").replace(' ', '-')
				.replaceAll("-{2,}", "-");
	}

	public String tag(String tag) {
		if (null == tag)
			return null;
		tag = normalizeSpace(tag).replaceAll("[^- 0-9a-zA-Z]", EMPTY).replaceAll(" +", "-")
				.toLowerCase();
		return isBlank(tag) ? null : tag;
	}

}
