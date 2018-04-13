/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

	private static final String NEWLINE = System.lineSeparator();

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

	public String printable(@Nullable String prefix) {
		StringBuilder sb = new StringBuilder(this.counters.size() * 15);
		if (isNotBlank(prefix)) {
			sb.append(prefix).append(':');
		}
		int counter = 0;
		for (String key : this.counters.keySet()) {
			counter++;
			sb.append(NEWLINE);
			if (this.useSerialNumbers) {
				sb.append(String.format("%3d. ", counter));
			}
			sb.append(key).append(SEPARATOR).append(this.counters.get(key).value());
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
