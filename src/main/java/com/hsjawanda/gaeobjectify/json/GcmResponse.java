/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author Harsh.Deep
 *
 */
@Data
@Accessors(chain = true)
public class GcmResponse {

	private String multicast_id;

	private int success;

	private int failure;

	private int canonical_ids;

	private int statusCode;

	private String statusMessage;

	private List<Map<String, String>> results;
}
