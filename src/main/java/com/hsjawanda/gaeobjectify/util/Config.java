/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.hsjawanda.gaeobjectify.collections.NonNullList;


/**
 * @author Harsh.Deep
 *
 */
public class Config {

	protected static final Logger log = Logger.getLogger(Config.class.getName());

	protected static final String keysFilename = "keys";

	protected static final String propFilename = "config";

	protected static Set<String> bundleFilenames = Sets.newHashSet("keys", "config");

	protected static String keysFailureReason = EMPTY;

	protected static String propFailureReason = EMPTY;

	protected static ResourceBundle rbKeys = null;

	protected static ResourceBundle rbProp = null;

	protected static List<ResourceBundle> bundles = NonNullList.empty(2);

	protected static boolean loaded = false;

	static {
		for (String bundleFilename : bundleFilenames) {
			try {
				bundles.add(ResourceBundle.getBundle(bundleFilename));
				loaded = true;
			} catch (MissingResourceException e) {
				log.info("Couldn't find resource bundle: " + bundleFilename);
			}
		}
//		try {
//			rbKeys = ResourceBundle.getBundle(keysFilename);
//			bundles.add(rbKeys);
//			loaded = true;
//		} catch (MissingResourceException e) {
//			keysFailureReason = "Couldn't find resource bundle: " + keysFilename;
//			log.warning(keysFailureReason);
//		}
//		try {
//			rbProp = ResourceBundle.getBundle(propFilename);
//			bundles.add(rbProp);
//			loaded = true;
//		} catch (MissingResourceException e) {
//			propFailureReason = "Couldn't find resource bundle: " + propFilename;
//			log.warning(propFailureReason);
//		} catch (Exception e) {
//			propFailureReason = "Unexpected error (" + e.getMessage() + ")";
//			log.warning(propFailureReason);
//		}
	}

	public static Optional<String> get(String key) {
		if (isBlank(key))
			return Optional.absent();
		String retVal = null;
		if (loaded) {
			for (ResourceBundle rb : bundles) {
				try {
					retVal = rb.getString(key);
					break;
				} catch (MissingResourceException e) {
					// Do nothing
				} catch (Exception e) {
					log.log(Level.WARNING, "Error getting value for key '" + key + "'", e);
				}
			}
//			if (null != rbKeys) {
//				try {
//					retVal = rbKeys.getString(key);
//				} catch (MissingResourceException e) {
//					// Do nothing
//				} catch (Exception e) {
//					log.log(Level.WARNING, "Error getting value for key '" + key + "' in '"
//							+ keysFilename + "'...", e);
//				}
//			} else {
//				log.warning("Failed to get from rbKeys: " + keysFailureReason);
//			}
//			if (null == retVal) {
//				if (null != rbProp) {
//					try {
//						retVal = rbProp.getString(key);
//					} catch (MissingResourceException e) {
//						// Do nothing
//					} catch (Exception e) {
//						log.log(Level.WARNING, "Error getting value for key '" + key + "' in '"
//								+ propFilename + "'...", e);
//					}
//				} else {
//					log.warning("Failed to get from rbProp: " + propFailureReason);
//				}
//			}
		}
		if (null == retVal) {
			log.info("Couldn't find any value for key '" + key + "'");
		}
		return Optional.fromNullable(retVal);
	}

	public static Optional<String> get(Keys key) {
		if (null == key)
			return Optional.absent();
		return get(key.name());
	}

	/**
	 * Get the value corresponding to {@code key}.
	 *
	 * @param key
	 *            the key to get value for
	 * @return the value, or the empty string if no value was found
	 */
	public static String getOrEmpty(String key) {
		return get(key).or(EMPTY);
	}

	/**
	 * Get the value corresponding to {@code key}.
	 *
	 * @param key
	 *            the {@code key} to get value for
	 * @return the value, or {@code null} if no value was found
	 */
	public static String getOrNull(String key) {
		return get(key).orNull();
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
