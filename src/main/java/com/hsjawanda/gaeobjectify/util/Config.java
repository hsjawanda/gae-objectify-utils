/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.google.common.base.Optional;


/**
 * @author Harsh.Deep
 *
 */
public class Config {

	protected static final Logger log = Logger.getLogger(Config.class.getName());

	protected static final String keysFilename = "keys";

	protected static final String propFilename = "config";

	protected static String keysFailureReason = EMPTY;

	protected static String propFailureReason = EMPTY;

	protected static ResourceBundle rbKeys = null;

	protected static ResourceBundle rbProp = null;

	protected static boolean loaded = false;

	static {
		try {
			rbKeys = ResourceBundle.getBundle(keysFilename);
			loaded = true;
		} catch (MissingResourceException e) {
			keysFailureReason = "Couldn't find resource bundle: " + keysFilename;
			log.warning(keysFailureReason);
		}
		try {
			rbProp = ResourceBundle.getBundle(propFilename);
			loaded = true;
		} catch (MissingResourceException e) {
			propFailureReason = "Couldn't find resource bundle: " + propFilename;
			log.warning(propFailureReason);
		} catch (Exception e) {
			propFailureReason = "Unexpected error (" + e.getMessage() + ")";
			log.warning(propFailureReason);
		}
	}

	public static Optional<String> get(String key) {
		if (isBlank(key))
			return Optional.absent();
		String retVal = null;
		if (loaded) {
			if (null != rbKeys) {
				try {
					retVal = rbKeys.getString(key);
				} catch (MissingResourceException e) {
					log.info("Key '" + key + "' not found in bundle '" + keysFilename + "'");
//					log.log(Level.WARNING, "Error getting from rbKeys...", e);
				}
			} else {
				log.warning("Failed to get from rbKeys: " + keysFailureReason);
			}
			if (null == retVal) {
				if (null != rbProp) {
					try {
						retVal = rbProp.getString(key);
					} catch (Exception e) {
						log.info("Key '" + key + "' not found in bundle '" + propFilename + "'");
//						log.log(Level.WARNING, "Error getting from rbProp...", e);
					}
				} else {
					log.warning("Failed to get from rbProp: " + propFailureReason);
				}
			}
		}
		return Optional.fromNullable(retVal);
	}

	public static Optional<String> get(Keys key) {
		if (null == key)
			return Optional.absent();
		return get(key.name());
	}

	public static Long getAsLong(Keys key) {
		return StringHelper.getAsLong(get(key).orNull());
	}

	public static Long getAsLong(String key) {
		return StringHelper.getAsLong(get(key).orNull());
	}

	public static Boolean getAsBoolean(Keys key) {
		return StringHelper.getAsBoolean(get(key).orNull());
	}

	public static Boolean getAsBoolean(String key) {
		return StringHelper.getAsBoolean(get(key).orNull());
	}

	public static enum Keys {
		GCM_SERVER_API_KEY, GCM_IOS_SENDER_ID, STRIPE_SECRET_TEST_KEY, STRIPE_PUBLIC_TEST_KEY,
		STRIPE_DEV_CLIENT_ID, DEFAULT_TOKEN_VALIDITY, PASSWORDS_MIN_LOWER, PASSWORDS_MIN_UPPER,
		PASSWORDS_MIN_SPECIAL, PASSWORDS_MIN_DIGITS, PASSWORDS_MIN_LENGTH,
	}
}
