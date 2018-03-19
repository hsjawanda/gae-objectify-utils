/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkState;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.EMPTY;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.defaultString;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.leftPad;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.repeat;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.rightPad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
@Data
@Accessors(chain = true, fluent = true)
public class SegmentTimer {

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

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Data data = new Data();

	private static final String NL = System.lineSeparator();

	public SegmentTimer() {
		timeUnit(TimeUnit.MILLISECONDS);
	}

	public SegmentTimer start() throws IllegalStateException {
		start(null);
		return this;
	}

	public SegmentTimer start(@Nullable String mesg) {
		checkState(this.timers.isEmpty(), "The timer is already running, it can't be re-started");
		checkState(!this.finalStop, "Timer has been stopped, it can't be started again.");
		this.timers.add(Stopwatch.createStarted());
		this.data.add("Started timer" + (isNotBlank(mesg) ? ": " + mesg : EMPTY), EMPTY);
		return this;
	}

	public SegmentTimer next() {
		next(null);
		return this;
	}

	public SegmentTimer next(@Nullable String segmentLabel) throws IllegalStateException {
		int size = this.timers.size();
		checkState(size >= 1, "Timer isn't running yet, you can't start next segment");
		checkState(!this.finalStop, "Timer has been stopped, you can't start next segment");
		Stopwatch lastTimer = this.timers.get(size - 1);
		// Check if lastTimer.isRunning() ?
		lastTimer.stop();
		this.timers.add(Stopwatch.createStarted());
		this.data.add(segmentLabel, time(lastTimer));
		return this;
	}

	public long prevSegmentTime() {
		return this.timers.size() > 1 ? this.timers.get(this.timers.size() - 2).elapsed(
				this.timeUnit) : 0;
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
		this.finalStop = true;
		this.data.add(stopMessage, time(lastTimer));
		return report();
	}

	public SegmentTimer timeUnit(TimeUnit tu) {
		this.timeUnit = tu;
		timeUnitStr(tu);
		return this;
	}

	private String report() {
		List<DataLine> lines = this.data.lines();
		int longestText = 0, longestTime = 0;
		for (DataLine line : lines) {
			longestText = Math.max(longestText, line.text != null ? line.text.length() : 0);
			longestTime = Math.max(longestTime, line.elapsed != null ? line.elapsed.length() : 0);
		}
		int count = 1;
		StringBuilder report = new StringBuilder(200);
		for (DataLine line : lines) {
			report.append(NL).append(leftPad(Integer.toString(count), 3)).append(". ")
					.append(rightPad(defaultString(line.text), longestText + 2))
					.append(leftPad(line.elapsed, longestTime));
			count++;
		}
		long totalTime = 0;
		for (Stopwatch timer : this.timers) {
			totalTime += timer.elapsed(this.timeUnit);
		}
		return report.append(NL).append(repeat('-', 5 + longestText + 3 + longestTime)).append(NL)
				.append(leftPad("Total time: ", 5 + longestText + 1)).append(leftPad(time(totalTime), longestTime + 1))
				.toString();
	}

	private SegmentTimer timeUnitStr(TimeUnit tu) {
		this.timeUnitStr = null != tu ? display(tu) : EMPTY;
		return this;
	}

	private String time(Stopwatch timer) {
		if (null != this.timeUnit)
			return Long.toString(timer.elapsed(this.timeUnit)) + timeUnitStr();
		else
			return timer.toString();
	}

	private String time(long timeValue) {
		return Long.toString(timeValue) + this.timeUnitStr;
	}

	public static String display(TimeUnit unit) {
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

	private class DataLine {

		public String text;

		public String elapsed;

		public DataLine(String text, String elapsed) {
			this.text = text;
			this.elapsed = elapsed;
		}

	}

	private class Data {

		@Getter
		List<DataLine> lines = new ArrayList<>();

		public void add(String text, String elapsed) {
			this.lines.add(new DataLine(text, elapsed));
		}

	}

}
