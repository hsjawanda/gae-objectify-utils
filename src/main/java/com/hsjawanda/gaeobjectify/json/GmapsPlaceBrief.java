/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

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
public class GmapsPlaceBrief {

	private String name;

	private Float rating;

	private String place_id;

	private GmapsGeometry geometry;

	private String vicinity;

}
