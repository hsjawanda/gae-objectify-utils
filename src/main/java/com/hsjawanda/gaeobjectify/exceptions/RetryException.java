/**
 *
 */
package com.hsjawanda.gaeobjectify.exceptions;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class RetryException extends RuntimeException {

	/**
	 * Assigned on 07/11/2017
	 */
	private static final long serialVersionUID = 1L;

	public RetryException(String message) {
		super(message);
	}

}
