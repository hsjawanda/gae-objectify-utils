/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 */
@Data
@Accessors(chain = true)
public class FcmResponse {

	private String multicast_id;

	@JsonProperty(value = "message_id")
	private long messageId;

	private int success;

	private int failure;

	private int canonical_ids;

	/**
	 * For topic messages
	 */
	private String error;

	private int statusCode;

	private String statusMessage;

	private List<FcmResponseResult> results;

	public static final String ERROR = "error";

	public static final String NOT_REGISTERED = "NotRegistered";

	public static final String UNAVAILABLE = "Unavailable";

	public static final String INTERNAL_SERVER_ERROR = "InternalServerError";
}
