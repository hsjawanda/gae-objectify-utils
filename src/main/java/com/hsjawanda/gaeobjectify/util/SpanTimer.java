/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.leftPad;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Stopwatch;

import lombok.NonNull;
import lombok.Synchronized;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class SpanTimer {

	private String name;

	private Map<String, AtomicLong> timers = new LinkedHashMap<>(3);

	private Map<String, AtomicInteger> counts = new HashMap<>(3);

	public static final NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));

	public static final DecimalFormat df = new DecimalFormat("###,###,###,###");

	TimeUnit timeUnit;

	boolean finished = false;

	SpanTimer(String timerName) {
		this.name = timerName;
		this.timeUnit = TimeUnit.MICROSECONDS;
	}

	public static SpanTimer get(@NonNull String timerName) {
		return new SpanTimer(timerName);
	}

	public Stopwatch startSpan(@NonNull String spanName) throws IllegalArgumentException {
		return Stopwatch.createStarted();
//		checkState(!this.finished, "This timer has been finished and can't be used again.");
//		if (!this.timers.containsKey(spanName)) {
//			this.timers.put(spanName, Stopwatch.createStarted());
//		} else {
//			Stopwatch timer = this.timers.get(spanName);
//			if (!timer.isRunning()) {
//				new KeepCount();
//				timer.start();
//			}
//		}
	}

	public long addToSpan(@NonNull String spanName, @NonNull Stopwatch duration) throws IllegalArgumentException {
		if (duration.isRunning()) {
			duration.stop();
		}
		synchronized (spanName) {
			this.timers.putIfAbsent(spanName, new AtomicLong(0));
			this.counts.putIfAbsent(spanName, new AtomicInteger(0));
		}
		this.counts.get(spanName).incrementAndGet();
		return this.timers.get(spanName).addAndGet(duration.elapsed(this.timeUnit));
//		checkState(!this.finished, "This timer has been finished and can't be used again.");
//		Stopwatch timer = this.timers.get(spanName);
//		checkState(timer != null, "Timer '" + spanName + "' was never started, so it can't be stopped.");
//		if (timer.isRunning()) {
//			timer.stop();
//		}
	}

	@Synchronized
	public String finish() {
		this.finished = true;
		PropertyValues vals = PropertyValues.create();
		for (String timerName : this.timers.keySet()) {
//			Stopwatch timer = this.timers.get(timerName);
//			if (timer.isRunning()) {
//				timer.stop();
//			}
			vals.addProperty(timerName,
					leftPad(nf.format(this.timers.get(timerName)), 9) + SegmentTimer.display(this.timeUnit)
							+ String.format(" %3d times", this.counts.get(timerName).get()));
		}

		return System.lineSeparator() + "Details of timer " + this.name + ":" + vals.publish();
	}

}
