/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.leftPad;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Tracer {

	protected Tracer() {
	}

	public static void partialTrace(int startFrame, int numToPrint) {
		startFrame = Math.max(1, startFrame + 1);
		Exception e = new Exception("Just tracing");
		StackTraceElement[] elements = e.getStackTrace();
		for (int i = startFrame; i < startFrame + numToPrint && i < elements.length; i++) {
			System.out.println(leftPad(Integer.toString(i), 3) + ". " + elements[i]);
		}
		System.out.flush();
	}

}
