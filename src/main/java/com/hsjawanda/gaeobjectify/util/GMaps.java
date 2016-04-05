/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
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
import com.hsjawanda.gaeobjectify.json.GmapsCall;
import com.hsjawanda.gaeobjectify.json.GmapsDetailResponse;
import com.hsjawanda.gaeobjectify.json.GmapsNearbyResponse;
import com.hsjawanda.gaeobjectify.json.GmapsPlaceDetail;
import com.hsjawanda.gaeobjectify.models.GenericAddress;
import com.hsjawanda.gaeobjectify.models.GenericAddress.Field;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class GMaps {

	private static final Logger	log		= Logger.getLogger(GMaps.class.getName());

	private static final String	API_KEY	= Config.get(Keys.GOOG_SERVER_API_KEY).or(EMPTY);

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

	public static GenericAddress parseDetailAddr(GmapsPlaceDetail deets, boolean debug) {
		GenericAddress addr = new GenericAddress();
		List<String> addrWhole = Constants.ADDR_SPLIT.splitToList(deets.getFormatted_address());
		System.out.println("addrWhole: " + addrWhole);
		List<String> addrLineParts = Collections.emptyList();
		List<GenericAddress.Field> addrLineFields = GenericAddress.Field.getAddrLines();
		int fixedParts = 3;
		if (addrWhole.size() > fixedParts) {
			addrLineParts = new LinkedList<>(addrWhole.subList(0, addrWhole.size() - fixedParts));
		}
		Holdall.compactList(addrLineParts, addrLineFields.size());
		Iterator<String> addrLineItr = addrLineParts.iterator();
		Iterator<Field> addrFieldsItr = addrLineFields.iterator();
		while (addrLineItr.hasNext()) {
			addr.set(addrFieldsItr.next(), addrLineItr.next());
		}
		addr.set(Field.CITY, addrWhole.get(addrWhole.size() - 3));
		addr.set(Field.STATE, addrWhole.get(addrWhole.size() - 2));
		addr.set(Field.COUNTRY, addrWhole.get(addrWhole.size() - 1));
		if (debug) {
			log.info("Address: " + System.lineSeparator() + addr.plainText());
		}
		return addr;
	}

	public static GenericAddress parseDetailAddr(GmapsPlaceDetail deets) {
		return parseDetailAddr(deets, false);
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
