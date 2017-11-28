/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.leftPad;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class Tracer {

	protected Tracer() {
	}

	public static String partialTrace(Exception e, int startFrame, int numToPrint) {
		startFrame = Math.max(1, startFrame + 1);
		if (null == e) {
			e = new Exception("Just tracing");
		}
		StackTraceElement[] elements = e.getStackTrace();
		StringBuilder trace = new StringBuilder(700).append(System.lineSeparator());
		for (int i = startFrame; i < startFrame + numToPrint && i < elements.length; i++) {
			trace.append(leftPad(Integer.toString(i), 3)).append(". ").append(elements[i])
					.append(System.lineSeparator());
		}
		return trace.toString();
	}

	public static String callerLocation(int stackFrameNum) {
		stackFrameNum = Math.max(1, stackFrameNum + 1);
		StackTraceElement[] stackTrace = new Exception().getStackTrace();
		String location = EMPTY;
		if (null != stackTrace && stackTrace.length >= stackFrameNum) {
			location = stackTrace[stackFrameNum].toString();
		}
		return location;
	}

	public static String callerLocation() {
		return callerLocation(1);
	}

	public static StackTraceElement getStackFrame(int stackFrameNum) {
		stackFrameNum = Math.max(1, stackFrameNum + 1);
		StackTraceElement[] stackTrace = new Exception().getStackTrace();
		if (null != stackTrace && stackTrace.length >= stackFrameNum)
			return stackTrace[stackFrameNum];
		return null;
	}

	public static String callingMethodName() {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		return null != stackTrace && stackTrace.length > 1 ? stackTrace[1].getMethodName() + "(...)" : "Unknown";
	}

}
