/**
 *
 */
package com.hsjawanda.gaeobjectify.exceptions;

/**
 * @author harsh.deep
 *
 */
public class InvalidFormatException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2L;

	public InvalidFormatException(String message) {
		super(message);
	}

	public InvalidFormatException(String message, Throwable cause) {
		this(message);
		super.initCause(cause);
	}

}
