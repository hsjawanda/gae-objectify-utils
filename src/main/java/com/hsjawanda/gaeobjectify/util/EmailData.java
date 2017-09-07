/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.ObjectifyDao;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode
@ToString
@Entity
public class EmailData implements Serializable {

	@Id
	private String								id;

	@Index
	private Date								dateCreated;

	private static final long					serialVersionUID	= 2L;

	@NonNull
	private InternetAddress						from;

	@NonNull
	private List<InternetAddress>				to;

	private String								subj;

	private String								body;

	private boolean								isHtml;

	public static final ObjectifyDao<EmailData>	DAO					= new ObjectifyDao<>(
																			EmailData.class);

	private EmailData() {
		this.id = UniqueIdGenerator.medium();
		this.isHtml = true;
		this.dateCreated = Calendar.getInstance().getTime();
	}

	@Builder
	private EmailData(InternetAddress from, @Singular("to") List<InternetAddress> to, String subj,
			String body, Boolean isHtml) {
		this();
		this.from = from;
		this.to = to;
		this.subj = subj;
		this.body = body;
		if (null != isHtml) {
			this.isHtml = isHtml.booleanValue();
		}
	}

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

//	public static class EmailDataBuilder {
//		private String id = UniqueIdGenerator.medium();
//	}
}
