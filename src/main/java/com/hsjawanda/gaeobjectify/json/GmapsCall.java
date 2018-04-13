/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.GmapsPlaceType;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Instances of this class are not threadsafe and therefore are not meant to be shared among
 * threads.
 *
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
@Accessors(chain = true, fluent = true)
public class GmapsCall {

	@Setter(AccessLevel.NONE)
	private String location;

//	@Setter(AccessLevel.NONE)
	private String type;

	private Integer radius;

	private String name;

	private String keyword;

	private String placeId;

	private boolean rankByDistance = false;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Map<String, Object> params;

	public GmapsCall location(Double lat, Double lng) {
		checkArgument(Constants.latRange.contains(lat),
				"Latitude must be in the range " + Constants.latRange.toString());
		checkArgument(Constants.longRange.contains(lng),
				"Longitude must be in the range " + Constants.longRange.toString());
		this.location = String.format("%f,%f", lat.doubleValue(), lng.doubleValue());
		return this;
	}

	public GmapsCall type(GmapsPlaceType type) {
		if (null != type) {
			this.type = type.toString();
		} else {
			this.type = null;
		}
		return this;
	}

	public GmapsCall radius(int radius) {
		if (radius < 0) {
			radius = 500;
		}
		this.radius = radius;
		return this;
	}

	/**
	 * @return a read-only {@code Map} view.
	 */
	public Map<String, Object> asMap() {
		int reqdParams = null == this.name ? 0 : 1;
		reqdParams += null == this.keyword ? 0 : 1;
		reqdParams += null == this.type ? 0 : 1;
		if (this.rankByDistance && reqdParams == 0)
			throw new IllegalStateException("When rankByDistance is set, one or more of name, "
					+ "keyword and type must be specified as well");
		if (null == this.params) {
			this.params = new LinkedHashMap<>(6);
		}
		this.params.clear();
		if (null != this.location) {
			this.params.put("location", this.location);
		}
		if (null != this.radius && !this.rankByDistance) {
			this.params.put("radius", this.radius);
		}
		if (null != this.type) {
			this.params.put("type", this.type);
		}
		if (null != this.placeId) {
			this.params.put("placeid", this.placeId);
		}
		if (null != this.keyword) {
			this.params.put("keyword", this.keyword);
		}
		if (this.rankByDistance) {
			this.params.put("rankby", "distance");
		}
		if (isNotBlank(this.name)) {
			this.params.put("name", this.name);
		}
		return Collections.unmodifiableMap(this.params);
	}

}
