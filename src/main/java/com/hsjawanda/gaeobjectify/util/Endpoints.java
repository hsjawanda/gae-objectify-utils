/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

/**
 * @author harsh.deep
 *
 */
public class Endpoints {

	private Endpoints() {
	}

	public static final String fbVerify = "https://graph.facebook.com/me";

	public static final String GCM_DOWNSTREAM = "https://gcm-http.googleapis.com/gcm/send";

	public static final String FCM_DOWNSTREAM = "https://fcm.googleapis.com/fcm/send";

	public static final String GMAPS_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json";

	public static final String GMAPS_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
}
