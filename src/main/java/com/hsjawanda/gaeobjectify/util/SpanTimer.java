/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import lombok.NonNull;
import lombok.Synchronized;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class SpanTimer {

	private String name;

	Map<String, Stopwatch> timers = new LinkedHashMap<>(3);

	boolean finished = false;

	SpanTimer(String timerName) {
		this.name = timerName;
	}

	public static SpanTimer get(@NonNull String timerName) {
		return new SpanTimer(timerName);
	}

	@Synchronized
	public void startSpan(@NonNull String spanName) throws IllegalArgumentException {
		checkState(!this.finished, "This timer has been finished and can't be used again.");
		if (!this.timers.containsKey(spanName)) {
			this.timers.put(spanName, Stopwatch.createStarted());
		} else {
			this.timers.get(spanName).start();
		}
	}

	@Synchronized
	public void stopSpan(@NonNull String spanName) throws IllegalArgumentException {
		checkState(!this.finished, "This timer has been finished and can't be used again.");
		Stopwatch timer = this.timers.get(spanName);
		checkState(timer != null, "Timer '" + spanName + "' was never started, so it can't be stopped.");
		timer.stop();
	}

	@Synchronized
	public String finish() {
		this.finished = true;
		PropertyValues vals = PropertyValues.create();
		for (String timerName : this.timers.keySet()) {
			Stopwatch timer = this.timers.get(timerName).stop();
			vals.addProperty(timerName, timer.elapsed(TimeUnit.MILLISECONDS));
		}

		return System.lineSeparator() + "Details of timer " + this.name + ":" + vals.publish();
	}

}
