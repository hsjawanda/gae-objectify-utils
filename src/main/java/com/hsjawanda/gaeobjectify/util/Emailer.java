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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
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
//		Properties props = new Properties();
//		log.info("Email properties: " + props);
//		Session session = Session.getDefaultInstance(props, null);
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

	public static void sendGmailEmail(EmailData data, boolean isHtml) {
		final String username="hsjawanda@gmail.com", password="nothing";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		sendEmail(data, isHtml, session);
	}

	public static void sendGridEmail(EmailData data, boolean isHtml) {
		String username = "admin@plowns.com", password = "Admin@1234";
		Sendgrid mail = new Sendgrid(username, password);
		mail.setFrom(data.getFrom().getAddress()).setFromName(data.getFrom().getPersonal())
				.setSubject(data.getSubj());
		for (InternetAddress iaddr : data.getTo()) {
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
			domain = Defaults.orDefault(trimToNull(domain), DEFAULT_DOMAIN.get());
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
}
