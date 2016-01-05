/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.annotation.Id;


/**
 * @author harsh.deep
 *
 */
public class Check {

	private static final Logger log = Logger.getLogger(Check.class.getName());

	private Check() {
	}

	public static boolean idNotNull(Object obj) {
		if (null != obj) {
			boolean search = true;
			Class<?> cls = obj.getClass(), origCls = obj.getClass();
			while (search && null != cls) {
				for (Field f : cls.getDeclaredFields()) {
					if (f.isAnnotationPresent(Id.class)) {
						f.setAccessible(true);
						try {
							if (f.get(obj) != null)
								return true;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							log.log(Level.WARNING, "Exception trying to use/access fields of "
									+ origCls.getName() + " using reflection...", e);
						}
						search = false;
						break;
					}
				}
				cls = cls.getSuperclass();
			}
		}
		return false;
	}
}
