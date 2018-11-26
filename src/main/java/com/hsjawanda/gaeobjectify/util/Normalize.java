/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.normalizeSpace;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.trimToNull;

import java.util.regex.Pattern;

import javax.annotation.Nullable;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class Normalize {

	private static final Normalize	INSTANCE	= new Normalize();

	private static final Pattern TAG_PATTERN = Pattern.compile("[^- 0-9a-zA-Z]");

	private Normalize() {
	}

	public static Normalize get() {
		return INSTANCE;
	}

	@Nullable
	public String email(String email) {
		email = trimToNull(email);
		return null == email ? null : email.toLowerCase();
	}

	public String role(String role) throws NullPointerException {
		role = checkNotNull(normalizeSpace(role), "role" + Constants.NOT_NULL);
		return role.toLowerCase().replaceAll("[^-\\p{Alnum} ]", EMPTY).replace(' ', '-')
				.replaceAll("-{2,}", "-");
	}

	@Nullable
	public String tag(String tag) {
		tag = trimToNull(normalizeSpace(tag));
		if (null == tag)
			return null;
		return TAG_PATTERN.matcher(tag).replaceAll(EMPTY).replace(' ', '-').toLowerCase();
	}

}
