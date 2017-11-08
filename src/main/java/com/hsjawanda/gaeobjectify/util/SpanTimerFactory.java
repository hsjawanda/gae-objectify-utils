/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Map;
import java.util.TreeMap;

import lombok.NonNull;
import lombok.Synchronized;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class SpanTimerFactory {

	private static Map<String, SpanTimer> timers = new TreeMap<>();

	private SpanTimerFactory() {
	}

	@Synchronized
	public static SpanTimer getInstance(@NonNull String timerName) {
		if (!timers.containsKey(timerName)) {
			timers.put(timerName, new SpanTimer(timerName));
		}
		return timers.get(timerName);
	}

}
