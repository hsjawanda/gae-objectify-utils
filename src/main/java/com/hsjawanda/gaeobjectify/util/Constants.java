/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;

import com.google.common.base.Joiner;


/**
 * @author Harsh.Deep
 *
 */
public class Constants {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Constants.class.getName());

	public static final String notBlank = " can't be null, empty or whitespace.";

	public static final int logRounds = 12;

	public static final int gaeStringLength = 500;

	public static final String servletMapping = "servletMapping";

	public static final List<InternetAddress> adminEmailsToNotify = new ArrayList<>(2);

	public static final String emailNewline = "\r\n";

	public static final BigDecimal HUNDRED = new BigDecimal(100);

	public static final Joiner pathJoiner = Joiner.on('/').skipNulls();

	private Constants() {
	}

}
