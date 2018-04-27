/**
 *
 */
package com.hsjawanda.gaeobjectify.exceptions;

/**
 * @author harsh.deep
 *
 */
public class NotUniqueException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2L;

	public NotUniqueException(String reason) {
		super(reason);
	}
}
