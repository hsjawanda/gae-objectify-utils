package com.hsjawanda.gaeobjectify.tests;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import com.hsjawanda.gaeobjectify.util.UniqueIdGenerator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		Range<Integer> radixRange = Range.closed(2, 60);
		assertTrue(radixRange.contains(60));
		assertTrue(radixRange.contains(2));
		assertFalse(radixRange.contains(1));
		assertFalse(radixRange.contains(61));
		assertTrue(true);
	}

	public void testBigIntegerAsString() {
		SecureRandom random = new SecureRandom();
		int times = 10 * 1000, builtInRadix = 36, customRadix = 62, builtInLen = 0, customLen = 0;
		String builtIn = null, custom = null;
		BigInteger num;
		Stopwatch builtInTimer = Stopwatch.createUnstarted(),
				customTimer = Stopwatch.createUnstarted();
		for (int i = 0; i < times; i++) {
			num = new BigInteger(65, random);
			builtInTimer.start();
			builtIn = num.toString(builtInRadix);
			builtInTimer.stop();
			builtInLen += builtIn.length();
			customTimer.start();
			custom = UniqueIdGenerator.asString(num, customRadix);
			customTimer.stop();
			customLen += custom.length();
			if (times <= 20) {
				System.out.println(
						String.format("By built-in: %30s; by custom: %30s", builtIn, custom));
			}
			if (builtInRadix == customRadix) {
				assertEquals(builtIn, custom);
			}
		}
		TimeUnit tu = TimeUnit.MICROSECONDS;
		System.out.println(String.format("Average times: built-in: %d; custom = %d ",
				builtInTimer.elapsed(tu) / times, customTimer.elapsed(tu) / times));

		System.out.println(String.format("Average length: built-in: %.2f; custom = %.2f",
				(builtInLen * 1.0 / times), (customLen * 1.0 / times)));

		System.out.println(
				"Encoding of 1000: " + UniqueIdGenerator.asString(BigInteger.valueOf(1000), 16));
	}
}
