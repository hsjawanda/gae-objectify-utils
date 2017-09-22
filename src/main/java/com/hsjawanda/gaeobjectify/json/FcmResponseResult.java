/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hsjawanda
 *
 */
@Data
@Accessors(chain = true)
public class FcmResponseResult {

	@JsonProperty(value = "message_id")
	private String messageId;

	@JsonProperty(value = "registration_id")
	private String registrationId;

	private String error;

}
