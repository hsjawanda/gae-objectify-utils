/**
 *
 */
package com.hsjawanda.gaeobjectify.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class BaseServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(BaseServlet.class.getName());

	protected static final String charEncoding = StandardCharsets.UTF_8.name();

	public BaseServlet() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		initialize(resp);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		initialize(resp);
	}

	private void initialize(HttpServletResponse resp) {
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
	}

	public static void loginWithNewSession(AuthenticationToken token, Subject subject) {
		Session origSess = subject.getSession();

		Map<Object, Object> attributes = new LinkedHashMap<>();
		Collection<Object> keys = origSess.getAttributeKeys();
		for (Object key : keys) {
			Object value = origSess.getAttribute(key);
			if (null != value) {
				log.info(key + " : " + value);
				attributes.put(key, value);
			}
		}
		origSess.stop();
		subject.login(token);
		Session newSess = subject.getSession();
		for (Object key : attributes.keySet()) {
			newSess.setAttribute(key, attributes.get(key));
		}
		newSess.setTimeout(432000 * 1000); // 5 days
	}

}
