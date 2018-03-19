/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.EMPTY;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Range;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class Constants {

	@SuppressWarnings("unused")
	private static final Logger					log					= Logger.getLogger(Constants.class
																			.getName());

	public static final String					NOT_BLANK			= " can't be null, empty or whitespace.";

	public static final String					NOT_NULL			= " can't be null.";

	public static final int						logRounds			= 12;

	public static final int						GAE_STRING_LENGTH	= 500;

	public static final Splitter				GAE_STRING_SPLITTER	= Splitter.fixedLength(
																			GAE_STRING_LENGTH)
																			.omitEmptyStrings();

	public static final Joiner					GAE_STRING_JOINER	= Joiner.on(EMPTY);

	public static final String					servletMapping		= "servletMapping";

	public static final List<InternetAddress>	adminEmailsToNotify	= new ArrayList<>(2);

	public static final String					emailNewline		= "\r\n";

	public static final BigDecimal				HUNDRED				= new BigDecimal(100);

	public static final Joiner					pathJoiner			= Joiner.on('/').skipNulls();

	public static Splitter						PATH_SPLITTER		= Splitter.on('/')
																			.omitEmptyStrings()
																			.trimResults();

	public static final Joiner					commaJoiner			= Joiner.on(", ").skipNulls();

	public static final Splitter				COMMA_SPLITTER		= Splitter.on(",")
																			.omitEmptyStrings()
																			.trimResults();

	public static final Range<Double>			latRange			= Range.closed(-90.0, 90.0);

	public static final Range<Double>			longRange			= Range.closed(-180.0, 180.0);

	public static final String					UTF_8				= StandardCharsets.UTF_8.name();

	public static final String					HOMEPAGE			= "/";

	public static final String					QUEUE_NAME_HEADER	= "X-AppEngine-QueueName";

	public static final String					FM_USER_UID			= "userUid";

	public static final String					FM_ACTION			= "action";

	public static final String					FM_ENCODED_URI		= "encodedUri";

	public static final String					FM_SERVER_URL		= "serverUrl";

	public static final Joiner					ADDR_JOIN			= Joiner.on(", ").skipNulls();

	public static final Splitter				ADDR_SPLIT			= Splitter.on(", ")
																			.omitEmptyStrings()
																			.trimResults();

	public static final Joiner					NAME_JOINER			= Joiner.on(' ').skipNulls();

	public static final String					URI_WEB_KEY			= "webkey";

	public static final TimeZone				IST					= TimeZone.getTimeZone("IST");

	public static final Calendar				INDIAN_CAL			= new GregorianCalendar(IST);

	public static final String					CRON_HEADER			= "X-Appengine-Cron";

	protected Constants() {
	}

}
