/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmapsNearbyResponse extends GmapsResponse {

	private List<GmapsPlaceBrief> results;

}
