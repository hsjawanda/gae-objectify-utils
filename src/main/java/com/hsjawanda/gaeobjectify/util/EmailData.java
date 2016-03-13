/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.List;

import javax.mail.internet.InternetAddress;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class EmailData {

	@NonNull
	private InternetAddress from;

	@Singular("to")
	@NonNull
	private List<InternetAddress> to;

	private String subj;

	private String body;

	public static EmailData empty() {
		return builder().build();
	}

	public String getSubj(String defaultSubj) {
		return defaultString(this.subj, defaultSubj);
	}

	public String getSubj() {
		return getSubj("(No subject)");
	}

	public String getBody(String defaultBody) {
		return defaultString(this.body, defaultBody);
	}

	public String getBody() {
		return getBody("(No body)");
	}
}
