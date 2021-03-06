/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;
import com.hsjawanda.gaeobjectify.data.IndexProperty;
import com.hsjawanda.gaeobjectify.exceptions.InvalidFormatException;
import com.hsjawanda.gaeobjectify.exceptions.InvalidPasswordException;
import com.hsjawanda.gaeobjectify.exceptions.NotUniqueException;
import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.tuple.ImmutablePair;
import com.hsjawanda.gaeobjectify.util.Config;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.Normalize;
import com.hsjawanda.gaeobjectify.util.PasswordKeys;
import com.hsjawanda.gaeobjectify.util.Passwords;
import com.hsjawanda.gaeobjectify.util.Validators;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public abstract class AccountEntity<K, T extends UniqueStringProperty<K>> {

	private static Logger			log				= Logger.getLogger(AccountEntity.class
															.getName());

	@JsonIgnore
	protected String				hashedPassword;

	@JsonIgnore
	@Index
	protected boolean				suspended		= false;

	@JsonIgnore
	@Index
	protected boolean				emailVerified	= false;

	@JsonIgnore
	protected Ref<T>				email;

	public static final Passwords	PWDS			= Passwords
															.builder()
															.minLowerChars(
																	Config.getAsLong(PasswordKeys.PASSWORDS_MIN_LOWER))
															.minUpperChars(
																	Config.getAsLong(PasswordKeys.PASSWORDS_MIN_UPPER))
															.minDigits(
																	Config.getAsLong(PasswordKeys.PASSWORDS_MIN_DIGITS))
															.minSpecialChars(
																	Config.getAsLong(PasswordKeys.PASSWORDS_MIN_SPECIAL))
															.minLength(
																	Config.getAsLong(PasswordKeys.PASSWORDS_MIN_LENGTH))
															.build();

	protected void hashAndSetPassword(String plaintext) {
		this.hashedPassword = BCrypt.hashpw(plaintext, BCrypt.gensalt(Constants.logRounds));
	}

	public static void checkPasswd(String passwd) throws InvalidPasswordException {
		ImmutablePair<Boolean, String> pwdValidity = PWDS.isValidPassword(passwd);
		if (!pwdValidity.left)
			throw new InvalidPasswordException(pwdValidity.right);
	}

	public String genRandomPassword() {
			String pwd = PWDS.genRandomPassword();
			this.hashAndSetPassword(pwd);
			GaeDataUtil.deferredSaveEntity(this);
			return pwd;
	}

	/**
	 * Use {@link AccountEntity#checkPasswd(String)} before calling this method.
	 *
	 * @param plaintextPassword
	 */
	public void forceSetPassword(String plaintextPassword) {
		this.hashAndSetPassword(plaintextPassword);
		GaeDataUtil.deferredSaveEntity(this);
	}

	@SuppressWarnings("unchecked")
	public K setPassword(String plainPassword) throws InvalidPasswordException {
		if (null != this.hashedPassword)
			throw new IllegalStateException(
					"Password already set. Use changePasswd() if you want to change password.");
		checkPasswd(plainPassword);
		this.hashAndSetPassword(plainPassword);
		return (K) this;
	}

	public void changePasswd(String oldPasswd, String newPasswd) throws InvalidPasswordException {
		if (BCrypt.checkpw(oldPasswd, this.hashedPassword)) {
			checkPasswd(newPasswd);
			this.hashAndSetPassword(newPasswd);
		} else
			throw new InvalidPasswordException(
					"The old password doesn't match the one already set.");
	}

	public boolean isPasswdValid(String passwdToTry) {
		if (isNotBlank(passwdToTry) && BCrypt.checkpw(passwdToTry, this.hashedPassword))
			return true;
		return false;
	}

	protected String checkEmail(String inputEmail) throws InvalidFormatException {
		inputEmail = Normalize.get().email(inputEmail);
		if (!Validators.email.isValid(inputEmail))
			throw new InvalidFormatException("The email '" + inputEmail + "' is invalid.");
		return inputEmail;
	}

	@SuppressWarnings("unchecked")
	protected K setEmail(Class<T> clazz, T uniqEntity) throws NotUniqueException {
		Key<T> key = null;
		key = ofy().transact(new IndexProperty<T, K>(clazz, uniqEntity, (K) this));
		if (null == key)
			throw new NotUniqueException("The email '" + uniqEntity.getId()
					+ "' is already in use for another " + clazz.getSimpleName() + ".");
		log.info("Successfully created " + clazz.getSimpleName() + " with key: " + key);
		GaeDataUtil.deleteEntity(this.email);
		this.email = Ref.create(key);
		log.info("Set Ref on current object.");
		return (K) this;
	}

	/**
	 * @return {@code null} if email is not set; {@code EMPTY} if email
	 *         {@code Ref<T>} is invalid; or the email.
	 */
	public String getEmail() {
		T email = null == this.email ? null : this.email.get();
		return null != email ? email.getId() : null;
	}

	/**
	 * @return {@code true} if success, {@code false} otherwise. Success is if
	 *         the email {@code Ref<T>} is invalid, could be removed and saved
	 *         back successfully. Success is also if the email was valid and/or
	 *         not set at all.
	 */
	public boolean unsetEmail() {
		if (null == this.email)
			return true;
		T email = this.email.get();
		if (null == email) {
			this.email = null;
			try {
				GaeDataUtil.deferredSaveEntity(this);
				return true;
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to save from AccountEntity.getEmail()", e);
				return false;
			}
		} else
			return true;
	}

	public abstract K setEmail(String inputEmail) throws InvalidFormatException, NotUniqueException;

	public AccountEntity<K, T> setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
		return this;
	}

	public String getHashedPassword() {
		return this.hashedPassword;
	}

	public boolean isEmailVerified() {
		return this.emailVerified;
	}

}
