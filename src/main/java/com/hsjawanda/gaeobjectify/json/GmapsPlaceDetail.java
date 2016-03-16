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
public class GmapsPlaceDetail {

	private List<GmapsAddrComponent> address_components;

	private String formatted_address;

	private String formatted_phone_number;

	private GmapsGeometry geometry;

	private String international_phone_number;

	private String place_id;

	private String scope;

	private List<String> types;

	private int utc_offset;

	private String vicinity;

}
