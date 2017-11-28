/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author Harsh.Deep
 *
 */
@Data
@Accessors(chain = true)
public class FcmResponse {

	private String multicast_id;

	private int success;

	private int failure;

	private int canonical_ids;

	private int statusCode;

	private String statusMessage;

	private List<FcmResponseResult> results;

	public static final String ERROR = "error";

	public static final String NOT_REGISTERED = "NotRegistered";

	public static final String UNAVAILABLE = "Unavailable";

	public static final String INTERNAL_SERVER_ERROR = "InternalServerError";
}
