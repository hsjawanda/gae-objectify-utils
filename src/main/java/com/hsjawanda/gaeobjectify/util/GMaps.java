/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Splitter;
import com.hsjawanda.gaeobjectify.json.GmapsAddrComponent;
import com.hsjawanda.gaeobjectify.json.GmapsCall;
import com.hsjawanda.gaeobjectify.json.GmapsDetailResponse;
import com.hsjawanda.gaeobjectify.json.GmapsNearbyResponse;
import com.hsjawanda.gaeobjectify.json.GmapsPlaceDetail;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class GMaps {

	private static final Logger log = Logger.getLogger(GMaps.class.getName());

	private static final String API_KEY = Config.get(Keys.GOOG_SERVER_API_KEY).or(EMPTY);

	private GMaps() {
	}

	public static GmapsDetailResponse placeDetails(Map<String, Object> params, boolean debug)
			throws JsonParseException, JsonProcessingException, IOException {
		String response = webSvcCall(new GenericUrl(Endpoints.GMAPS_PLACE_DETAILS), params, debug);
		try {
			GmapsDetailResponse deets = NetCall.mapper.readValue(response,
					GmapsDetailResponse.class);
			return deets;
		} catch (JsonProcessingException e) {
			log.log(Level.WARNING, "Error processing response of GMaps. Stacktrace:", e);
			throw e;
		}
	}

	public static GmapsDetailResponse placeDetails(GmapsCall params, boolean debug)
			throws JsonParseException, JsonProcessingException, IOException {
		return placeDetails(params.asMap(), debug);
	}

	public static GmapsDetailResponse placeDetails(GmapsCall params)
			throws JsonParseException, JsonProcessingException, IOException {
		return placeDetails(params, false);
	}

	public static void parseDetailAddress(GmapsPlaceDetail deets) {
		int subLocalityLevels = 5, additional = 2;
		int addrPartsCapacity = subLocalityLevels + additional;
		String[] addrParts = new String[addrPartsCapacity];
//		GmapsCall params = new GmapsCall().placeId(googPlaceId);
//		try {
//			GmapsDetailResponse deets = GMaps.placeDetails(params, true);
		GmapsPlaceDetail placeDeets = deets;
//			Merchant merch = Merchant.builder().name(placeDeets.getName())
//					.website(placeDeets.getWebsite()).build();
//			AddressMerchantBuilder addrB = AddressMerchant.builder();
		for (GmapsAddrComponent addrComponent : placeDeets.getAddress_components()) {
			if (addrComponent.getTypes().contains("administrative_area_level_1")) {
				System.out.println("state: " + addrComponent.getLong_name());
			} else if (addrComponent.getTypes().contains("country")) {
				System.out.println("country: " + addrComponent.getLong_name());
			} else if (addrComponent.getTypes().contains("locality")) {
				System.out.println("city: " + addrComponent.getLong_name());
			} else if (addrComponent.getTypes().contains("street_number")) {
				addrParts[0] = addrComponent.getLong_name();
			} else if (addrComponent.getTypes().contains("route")) {
				addrParts[1] = addrComponent.getLong_name();
			} else {
				for (String type : addrComponent.getTypes()) {
					if (type.startsWith("sublocality_level_")) {
						for (int i = subLocalityLevels; i > 0; i--) {
							if (type.equals("sublocality_level_" + i)) {
								addrParts[subLocalityLevels - i + additional] = addrComponent
										.getLong_name();
								break;
							}
						}
						break;
					}
				}
			}
		}
		for (int i = 0; i < addrPartsCapacity; i++) {
			if (addrParts[i] != null) {
				System.out.println("addrLine: " + addrParts[i]);
			}
		}
		Splitter addrSplitter = Splitter.on(", ").omitEmptyStrings().trimResults();
		List<String> addrList = addrSplitter.splitToList(deets.getFormatted_address());
		System.out.println("From splitter: " + addrList.subList(0, addrList.size() - 3));
//			AddressMerchant addr = addrB.build();
//			this.log.info("Address: " + addr);
//			merch.setAddr(addrB.build());
//		} catch (JsonParseException e) {
//		} catch (JsonProcessingException e) {
//		} catch (IOException e) {
//		}
	}

	public static GmapsNearbyResponse nearbySearch(Map<String, Object> params, boolean debug)
			throws JsonParseException, JsonProcessingException, IOException, InterruptedException,
			ExecutionException {
		String response = webSvcCall(new GenericUrl(Endpoints.GMAPS_NEARBY_SEARCH), params, debug);
		try {
			GmapsNearbyResponse nearbyData = NetCall.mapper.readValue(response,
					GmapsNearbyResponse.class);
			return nearbyData;
		} catch (JsonProcessingException e) {
			log.log(Level.WARNING, "Error processing response of GMaps. Stacktrace:", e);
			throw e;
		}
	}

	public static GmapsNearbyResponse nearbySearch(GmapsCall params, boolean debug)
			throws JsonParseException, JsonProcessingException, IOException, InterruptedException,
			ExecutionException {
		return nearbySearch(params.asMap(), debug);
	}

	public static GmapsNearbyResponse nearbySearch(GmapsCall params) throws JsonParseException,
			JsonProcessingException, IOException, InterruptedException, ExecutionException {
		return nearbySearch(params, false);
	}

	private static String webSvcCall(GenericUrl url, Map<String, Object> params, boolean debug)
			throws IOException {
		url.putAll(params);
		url.put("key", API_KEY);
		HttpResponse res = NetCall.callResponse(url, null, HttpMethods.GET, null);
		String response = NetCall.toString(res, debug);
		return response;
	}

}
