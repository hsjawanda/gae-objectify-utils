/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.leftPad;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.rightPad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class FrequencyCounter {

	private static final String NL = System.lineSeparator();

	private static final String SEPARATOR = " : ";

	private Map<String, KeepCount> counters;

	private boolean useSerialNumbers = true;

	private FrequencyCounter() {
	}

	public static FrequencyCounter create(@Nonnull KeyOrdering order) throws NullPointerException {
		if (null == order)
			throw new NullPointerException("order cannot be null");
		FrequencyCounter counter = new FrequencyCounter();
		switch (order) {
		case NONE:
			counter.counters = new HashMap<>();
			break;
		case IN_ORDER:
			counter.counters = new LinkedHashMap<>();
			break;
		case SORTED:
			counter.counters = new TreeMap<>();
			break;
		}
		return counter;
	}

	public long increment(@Nonnull String key, long incr) throws NullPointerException {
		if (null == key)
			throw new NullPointerException("null keys are not allowed");
		if (this.counters.containsKey(key)) {
			this.counters.get(key).increment(incr);
		} else {
			this.counters.put(key, new KeepCount(incr));
		}
		return this.counters.get(key).value();
	}

	public boolean isUseSerialNumbers() {
		return this.useSerialNumbers;
	}

	public synchronized String print(@Nullable String prefix) {
		StringBuilder sb = new StringBuilder(this.counters.size() * 15).append(NL).append(prefix);
		int counter = 0;
		for (String key : this.counters.keySet()) {
			counter++;
			sb.append(NL);
			if (this.useSerialNumbers) {
				sb.append(String.format("%3d. ", counter));
			}
			sb.append(key).append(SEPARATOR).append(this.counters.get(key).value());
		}
		return sb.toString();
	}

	public String printSorted(@Nullable String prefix) {
		return printSorted(prefix, true);
	}

	public synchronized String printSorted(@Nullable String prefix, boolean sortDescending) {
		List<Map.Entry<String, KeepCount>> sortedCounters = new ArrayList<>(this.counters.entrySet());
		final int multiplier = sortDescending ? -1 : 1;
		Collections.sort(sortedCounters, (a, b) -> multiplier * a.getValue().compareTo(b.getValue()));
		int maxKeyWidth = 0, maxValueWidth = 0, counterPadding = Numbers.paddingRequiredFor(sortedCounters.size());
		long maxValue = 0;
		String serialNumFormatSpecifier = "%" + counterPadding + "d. ";
		for (Map.Entry<String, KeepCount> entry : sortedCounters) {
			maxKeyWidth = Math.max(maxKeyWidth, entry.getKey().length());
			maxValue = Math.max(maxValue, entry.getValue().value());
		}
		maxValueWidth = Numbers.paddingRequiredFor(maxValue);
		StringBuilder sb = new StringBuilder(this.counters.size() * 15).append(NL).append(prefix);
		int counter = 0;
		for (Map.Entry<String, KeepCount> entry : sortedCounters) {
			counter++;
			sb.append(NL);
			if (this.useSerialNumbers) {
				sb.append(String.format(serialNumFormatSpecifier, counter));
			}
			sb.append(rightPad(entry.getKey(), maxKeyWidth)).append(SEPARATOR)
					.append(leftPad(Long.toString(entry.getValue().value()), maxValueWidth));
		}
		return sb.toString();
	}

	public FrequencyCounter setUseSerialNumbers(boolean useSerialNumbers) {
		this.useSerialNumbers = useSerialNumbers;
		return this;
	}

	public Map<String, KeepCount> values() {
		return ImmutableMap.copyOf(this.counters);
	}

}
