/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author hsjawanda
 *
 */
@ToString
@EqualsAndHashCode
public class KeepCount implements Comparable<KeepCount> {

	private long counter;

	public KeepCount() {
		this.counter = 0;
	}

	public KeepCount(long init) {
		this.counter = init;
	}

	public synchronized KeepCount increment(long incValue) {
		this.counter += incValue;
		return this;
	}

	public long value() {
		return this.counter;
	}

	@Override
	public int compareTo(KeepCount o) {
		if (null == o)
			return -1;
		return (int) (this.counter - o.counter);
	}

}
