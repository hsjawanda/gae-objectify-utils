/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.defaultString;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.leftPad;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.trimToEmpty;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import lombok.Builder;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
@Builder
public class Messages {

	private static Logger LOG;

	private static final String NL = System.lineSeparator();

	private static DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd MMM yyy HH:mm:ss.SSS")
			.withZone(Constants.IST_TZ_ID);

	@Builder.Default
	private int initialCapacity = 200;

	private final List<String> messages = new ArrayList<>();

	private boolean serialNumbers;

	private boolean timestamps;

	@SuppressWarnings("unused")
	private static Logger log() {
		if (null == LOG) {
			LOG = Logger.getLogger(Messages.class.getName());
		}
		return LOG;
	}

	public Messages add(String message) {
		this.messages.add(addTimestampIfNeeded(message));
		return this;
	}

	public Messages add(String formatSpecifier, Object... args) {
		this.messages.add(addTimestampIfNeeded(String.format(formatSpecifier, args)));
		return this;
	}

	public String printable(String prefix) {
		StringBuilder message = new StringBuilder(this.initialCapacity).append(NL).append(trimToEmpty(prefix));
		int counter = 0, padding = -1;
		for (String mesg : this.messages) {
			message.append(NL);
			if (this.serialNumbers) {
				if (padding == -1) {
					padding = ((int) Math.log10(this.messages.size())) + 1;
				}
				message.append(leftPad(Integer.toString(++counter), padding)).append(". ");
			}
			message.append(mesg);
		}
		return message.toString();
	}

	private String addTimestampIfNeeded(String mainMesg) {
		return this.timestamps ? new StringBuilder(100).append(TIMESTAMP.format(Instant.now())).append(": ")
				.append(mainMesg).toString() : defaultString(mainMesg, "null");
	}

}
