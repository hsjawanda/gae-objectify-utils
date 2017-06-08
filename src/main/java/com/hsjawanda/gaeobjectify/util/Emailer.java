/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.google.common.base.Optional;


/**
 * @author harsh.deep
 *
 */
public class Emailer {

	private static final Logger log = Logger.getLogger(Emailer.class.getName());

	public static final SimpleDateFormat dateFormatter = Defaults.dateFmt;

	public static final Optional<String> DEFAULT_DOMAIN = Config.get("email.domain");

	protected Emailer() {
	}

	public static void sendEmail(EmailData data, boolean isHtml, Session session) {
		Message mesg = new MimeMessage(session);
		InternetAddress from = checkNotNull(data.getFrom(), "The 'From' address can't be null");

		try {
			mesg.setFrom(from);
			checkNotNull(data.getTo(), "The 'To' addresses can't be null.");
			checkArgument(!data.getTo().isEmpty(), "The 'To' address list can't be empty.");
			for (InternetAddress to : data.getTo()) {
				if (null != to) {
					mesg.addRecipient(RecipientType.TO, to);
				}
			}
			mesg.setSubject(trimToEmpty(data.getSubj()));
			if (isHtml) {
				mesg.setContent(data.getBody(), MediaType.TEXT_HTML);
			} else {
				mesg.setText(trimToEmpty(data.getBody()));
			}
			Transport.send(mesg);
		} catch (SendFailedException e) {
			log.log(Level.WARNING, "Email sending failed. Stacktrace:", e);
		} catch (MessagingException e) {
			log.log(Level.WARNING, "Exception while sending email.", e);
		} catch (Exception e) {
			log.log(Level.WARNING, "General exception catching while sending email. Stacktrace:", e);
		}
	}

	public static void sendEmail(EmailData data, boolean isHtml) {
		sendEmail(data, isHtml, Session.getDefaultInstance(new Properties(), null));
	}

	public static void sendGridEmail(EmailData data, boolean isHtml, String username,
			String password) {
		InternetAddress from = checkNotNull(data.getFrom(), "The 'From' address can't be null");
		List<InternetAddress> toList = checkNotNull(data.getTo(),
				"The 'To' addresses can't be null.");
		checkArgument(!toList.isEmpty(), "The 'To' address list can't be empty.");

		Sendgrid mail = new Sendgrid(username, password);
		mail.setFrom(from.getAddress()).setFromName(from.getPersonal()).setSubject(data.getSubj());
		for (InternetAddress iaddr : toList) {
			mail.addTo(iaddr.getAddress(), defaultString(iaddr.getPersonal()));
		}
		if (isHtml) {
			mail.setText(Jsoup.clean(data.getBody(), Whitelist.none()));
			mail.setHtml(data.getBody());
		} else {
			mail.setText(data.getBody());
		}
		try {
			mail.send();
		} catch (Exception e) {
			log.log(Level.WARNING, "Error with SendGrid emailing. Stacktrace:", e);
		}
	}

	public static void sendEmail(EmailData data) {
		sendEmail(data, false);
	}

	public static InternetAddress getEmailAddress(String beforeAt, String domain, String name) {
		if (!DEFAULT_DOMAIN.isPresent()) {
			domain = checkNotNull(trimToNull(domain),
					"Domain name ('" + domain + "') must be specified correctly.");
		} else {
			domain = Defaults.or(trimToNull(domain), Holdall.get(DEFAULT_DOMAIN));
		}
		String fullEmail = beforeAt + "@" + domain;
		return getEmailAddress(fullEmail, name);
	}

	public static InternetAddress getEmailAddress(String fullEmail, String name) {
		InternetAddress retVal = null;
		try {
			retVal = new InternetAddress(fullEmail, name);
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Error creating email (" + name + " <" + fullEmail + ">)", e);
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Unforeseen error creating email (" + name + " <" + fullEmail + ">)", e);
		}
		return retVal;
	}

	public static String normalizeEmail(String email) {
		return trimToEmpty(email).toLowerCase();
	}
}
