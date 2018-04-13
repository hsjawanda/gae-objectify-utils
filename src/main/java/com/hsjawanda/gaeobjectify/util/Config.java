/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.CaseFormat;
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
		}
//		if (null == retVal) {
//			log.info("Couldn't find any value for key '" + key + "'");
//		}
		return Optional.fromNullable(retVal);
	}

	public static <T extends Enum<?>> Optional<String> get(T key) {
		if (null == key)
			return Optional.absent();
		CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, key.name());
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

	public static <T extends Enum<?>> String getOrEmpty(T key) {
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

	public static <T extends Enum<?>> String getOrNull(T key) {
		return get(key).orNull();
	}

	public static <T extends Enum<?>> Long getAsLong(T key) {
		return StringHelper.getAsLong(get(key).orNull());
	}

	public static Long getAsLong(String key) {
		return StringHelper.getAsLong(get(key).orNull());
	}

	public static long getAsLong(String key, long defaultValue) {
		return StringHelper.getAsLong(get(key).orNull(), defaultValue);
	}

	public static int getAsInt(String key, int defaultValue) {
		return StringHelper.getAsInt(get(key).orNull(), defaultValue);
	}

	public static Integer getAsInteger(String key) {
		return StringHelper.getAsInteger(get(key).orNull());
	}

	public static <T extends Enum<?>> Boolean getAsBoolean(T key) {
		return StringHelper.getAsBoolean(get(key).orNull());
	}

	public static Boolean getAsBoolean(String key) {
		return StringHelper.getAsBoolean(get(key).orNull());
	}
}
