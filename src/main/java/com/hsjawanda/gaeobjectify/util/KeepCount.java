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
public class KeepCount {

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

}
