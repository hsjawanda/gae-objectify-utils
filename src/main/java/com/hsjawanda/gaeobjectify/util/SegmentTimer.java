/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Data
@Accessors(chain = true, fluent = true)
public class SegmentTimer {

//	@Getter(AccessLevel.NONE)
//	@Setter(AccessLevel.NONE)
//	private Logger extLog;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<Stopwatch> timers = Lists.newArrayList();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean finalStop = false;

	private TimeUnit timeUnit;

	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.NONE)
	private String timeUnitStr;

	public SegmentTimer() {
		timeUnit(TimeUnit.MILLISECONDS);
	}

	public String start() throws IllegalStateException {
		checkState(this.timers.isEmpty(), "The timer is already running, it can't be re-started");
//		checkState(!this.finalStop, "Timer has been stopped, it can't be started again.");
		this.timers.add(Stopwatch.createStarted());
		return "Started timer";
	}

	public String next() {
		return next(null);
	}

	public String next(@Nullable String segmentLabel) throws IllegalStateException {
		int size = this.timers.size();
		checkState(size >= 1, "Timer isn't running yet, you can't start next segment");
		checkState(!this.finalStop, "Timer has been stopped, you can't start next segment");
		Stopwatch lastTimer = this.timers.get(size - 1);
		// Check if lastTimer.isRunning() ?
		lastTimer.stop();
		segmentLabel = isNotBlank(segmentLabel) ? " (" + segmentLabel + ")" : EMPTY;
//		log().info();
		this.timers.add(Stopwatch.createStarted());
		return String.format("%3d >>>> %s%s", size, time(lastTimer), segmentLabel);
	}

	public SegmentTimer pauseSegment() throws IllegalStateException {
		int size = this.timers.size();
		checkState(size >= 1, "Timer isn't running yet, you can't pause a segment");
		checkState(!this.finalStop, "Timer has been stopped, you can't pause segment");
		this.timers.get(size).stop();
		return this;
	}

	public SegmentTimer restartSegment() throws IllegalStateException {
		int size = this.timers.size();
		checkState(size >= 1, "Timer isn't running yet, you can't restart a segment");
		checkState(!this.finalStop, "Timer has been stopped, you can't restart a segment");
		this.timers.get(size).start();
		return this;
	}

	public SegmentTimer resetSegment() throws IllegalStateException {
		int size = this.timers.size();
		checkState(size >= 1, "Timer isn't running yet, you can't reset a segment");
		checkState(!this.finalStop, "Timer has been stopped, you can't reset a segment");
		this.timers.get(size).reset();
		return this;
	}

	public String stop() {
		return stop(null);
	}

	public String stop(String stopMessage) {
		int size = this.timers.size();
		checkState(size >= 1, "Timer isn't running yet, you can't stop it.");
		Stopwatch lastTimer = this.timers.get(size - 1);
		// Check if lastTimer.isRunning() ?
		lastTimer.stop();
		stopMessage = isNotBlank(stopMessage) ? " (" + stopMessage + ")" : EMPTY;
//		log().info();
		this.finalStop = true;
		int totalTime = 0;
		for (Stopwatch timer : this.timers) {
			totalTime += timer.elapsed(this.timeUnit);
		}
		return String.format("%3d >>>> %s%s <last segment>\nTotal time: %d%s", size,
				time(lastTimer), stopMessage, totalTime, timeUnitStr());
	}

	public SegmentTimer timeUnit(TimeUnit tu) {
		this.timeUnit = tu;
		timeUnitStr(tu);
		return this;
	}

	private SegmentTimer timeUnitStr(TimeUnit tu) {
		this.timeUnitStr = null != tu ? display(tu) : EMPTY;
		return this;
	}

//	private Logger log() {
//		return null != this.extLog ? this.extLog : LOG;
//	}

	private String time(Stopwatch timer) {
		if (null != this.timeUnit)
			return Long.toString(timer.elapsed(this.timeUnit)) + timeUnitStr();
		else
			return timer.toString();
	}

	private static String display(TimeUnit unit) {
		switch (unit) {
		case NANOSECONDS:
			return " ns";
		case MICROSECONDS:
			return " \u03bcs"; // μs
		case MILLISECONDS:
			return " ms";
		case SECONDS:
			return " s";
		case MINUTES:
			return " min";
		case HOURS:
			return " h";
		case DAYS:
			return " d";
		default:
			return EMPTY;
		}
	}

}
