/**
 *
 */
package com.hsjawanda.gaeobjectify.security;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Logger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.mindrot.jbcrypt.BCrypt;

import com.hsjawanda.gaeobjectify.util.StringHelper;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class BCryptCredentialsMatcher implements CredentialsMatcher {

	private static final Logger log = Logger.getLogger(BCryptCredentialsMatcher.class.getName());

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.shiro.authc.credential.CredentialsMatcher#doCredentialsMatch(org.apache.shiro.
	 * authc.AuthenticationToken, org.apache.shiro.authc.AuthenticationInfo)
	 */
	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
			throws NullPointerException, IllegalArgumentException {
		checkNotNull(token);
		checkNotNull(info);
		checkArgument(token instanceof UsernamePasswordToken, "token does not have any password");
		String password = StringHelper.toString(((UsernamePasswordToken) token).getPassword());
		String hashedPasswd = StringHelper.toString(info.getCredentials());
		boolean authenticated = BCrypt.checkpw(password, hashedPasswd);
		log.fine("Password matched: " + authenticated);
		return authenticated;
	}

}
