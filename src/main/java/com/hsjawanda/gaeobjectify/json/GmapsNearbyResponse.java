/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmapsNearbyResponse {

	private List<GmapsPlaceBrief> results;
}
