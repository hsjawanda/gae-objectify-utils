/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import java.lang.reflect.Field;

import com.googlecode.objectify.annotation.Id;


/**
 * @author harsh.deep
 *
 */
public class Check {

	private Check() {
	}

	public static boolean idNotNull(Object obj) {
		if (null != obj) {
			for (Field f : obj.getClass().getDeclaredFields()) {
				if (f.isAnnotationPresent(Id.class)) {
					f.setAccessible(true);
					try {
						if (f.get(obj) != null)
							return true;
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// Do nothing.
					}
					break;
				}
			}
		}
		return false;
	}
}
