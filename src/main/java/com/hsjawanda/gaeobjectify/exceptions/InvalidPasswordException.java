/**
 *
 */
package com.hsjawanda.gaeobjectify.exceptions;

/**
 * @author harsh.deep
 *
 */
public class InvalidPasswordException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPasswordException(String reason) {
		super(reason);
	}
}
