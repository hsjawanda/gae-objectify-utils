/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;


/**
 * @author Harsh.Deep
 *
 */
public class Validators {

	private Validators() {
	}

	public static final EmailValidator email = EmailValidator.getInstance(false);

	public static final UrlValidator url = new UrlValidator();
}
