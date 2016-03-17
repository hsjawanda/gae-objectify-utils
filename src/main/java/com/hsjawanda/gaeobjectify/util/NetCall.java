/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.MultipartContent.Part;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.hsjawanda.gaeobjectify.json.GcmMessage;
import com.hsjawanda.gaeobjectify.json.GcmResponse;


/**
 * @author harsh.deep
 *
 */
public class NetCall {

	private static final Logger log = Logger.getLogger(NetCall.class.getName());

	private String urlStr;

	private Method method = Method.GET;

	private ImmutableMap<String, String> params;

	private String paramStr;

	public static final String FIELD_MERCHANT_KEY = "theMerchant";

	public static final String FIELD_PRINCIPAL_KEY = "thePrincipal"; //$NON-NLS-1$

	public static final String FIELD_PROFILE_IMG = "profileImage"; //$NON-NLS-1$

	private static final String paramSeparator = "&";

	private static final String enc = StandardCharsets.UTF_8.name();

	private static HttpRequestFactory factory = null;

	private static HttpRequestFactory gcmFactory = null;

	private static GenericUrl gcmDownstreamUrl = new GenericUrl(Endpoints.GCM_DOWNSTREAM);

	private static JacksonFactory jacksonFactory = new JacksonFactory();

	public static final ObjectMapper mapper = new ObjectMapper();

	public static final ObjectMapper mapperPretty = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_ABSENT);
		mapperPretty.setSerializationInclusion(Include.NON_ABSENT);
		mapperPretty.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private NetCall(String url) {
		this.urlStr = url;
	}

	public void setParameters(Map<String, String> parameters) {
		this.params = ImmutableMap.copyOf(parameters);
	}

	protected void genParameterString() {
		if (null != this.paramStr)
			return;
		StringBuilder paramSb = null;
		if (null != this.params) {
			paramSb = new StringBuilder();
			for (String key : this.params.keySet()) {
				try {
					paramSb.append(URLEncoder.encode(key, enc)).append('=')
							.append(URLEncoder.encode(this.params.get(key), enc))
							.append(paramSeparator);
				} catch (UnsupportedEncodingException e) {
					log.info("Exception: the encoding '" + enc + "' is not supported.");
				}
			}
			if (paramSb.length() > 0) {
				this.paramStr = paramSb.substring(0, paramSb.length() - paramSeparator.length());
			} else {
				this.paramStr = EMPTY;
			}
		} else {
			this.paramStr = EMPTY;
		}
	}

	public String getResult() {
		URL url;
		genParameterString();
		StringBuilder fullUrl = new StringBuilder(this.urlStr);
		if (isNotBlank(this.paramStr)) {
			fullUrl.append('?').append(this.paramStr);
		}
		log.info("Full URL: " + fullUrl);
		try {
			url = new URL(fullUrl.toString());
		} catch (MalformedURLException e1) {
			log.warning("MalformedURLException for url: " + fullUrl);
			return EMPTY;
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(this.method.name());
			conn.setDoOutput(true);
			conn.setDoInput(true);
			if (isNotBlank(this.paramStr)) {
				log.info("Parameter string: " + this.paramStr);
				if (this.method == Method.POST) {
					DataOutputStream os = new DataOutputStream(conn.getOutputStream());
					os.writeBytes(this.paramStr);
					os.close();
				}
			}
			int respCode = conn.getResponseCode();
			if (200 == respCode)
				return IOUtils.toString(conn.getInputStream());
			else {
				log.warning("Error getting result. Response: " + respCode + " "
						+ conn.getResponseMessage() + System.lineSeparator() + this);
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "Exception while making call.", e);
		}
		return EMPTY;
	}

	public static HttpRequestFactory getRequestFactory() {
		if (null == factory) {
			factory = (new UrlFetchTransport()).createRequestFactory();
		}
		return factory;
	}

	public static HttpRequestFactory getGcmRequestFactory() {
		if (null == gcmFactory) {
			gcmFactory = (new UrlFetchTransport())
					.createRequestFactory(new HttpRequestInitializer() {
						@Override
						public void initialize(HttpRequest request) throws IOException {
							HttpHeaders headers = request.getHeaders();
							headers.setAuthorization(
									"key=" + Config.get(Keys.GOOG_SERVER_API_KEY).or(EMPTY));
							headers.setContentType(javax.ws.rs.core.MediaType.APPLICATION_JSON);
						}
					});
		}
		return gcmFactory;
	}

	public static HttpResponse callResponse(GenericUrl url, HttpRequestFactory factory,
			String httpMethod, HttpContent content) throws IOException {
		factory = null == factory ? getRequestFactory() : factory;
//		if (httpMethod.equals(HttpMethods.POST) && null == content) {
//		}
		try {
			HttpRequest req = factory.buildRequest(httpMethod, url, content);
			HttpResponse response = req.execute();
			return response;
		} catch (IOException e) {
			int size = content == null ? 100 : 200;
			StringBuilder mesg = new StringBuilder(size);
			mesg.append("Error making call to").append(System.lineSeparator()).append("      URL: ")
					.append(System.lineSeparator()).append("  Method: ").append(httpMethod);
			if (content != null) {
				mesg.append(System.lineSeparator()).append("Content: ").append(content.toString());
			}
			log.log(Level.WARNING, mesg.toString(), e);
			throw e;
		}
	}

	public static String toString(HttpResponse res, boolean debug) throws IOException {
		if (null == res)
			return EMPTY;
		try {
			String response = IOUtils.toString(res.getContent());
			if (debug) {
				Object responseObj = mapper.readValue(response, Object.class);
				log.info("Response: " + mapperPretty.writeValueAsString(responseObj));
			}
			return response;
		} catch (IOException e) {
			log.log(Level.WARNING, "Exception converting HttpResponse to String. Stacktrace:", e);
			throw e;
		}
	}

	public static String toString(HttpResponse res) throws IOException {
		return toString(res, false);
	}

//	public static GmapsNearbyResponse gmapsCall(String urlStr, Map<String, Object> params,
//			boolean debug) throws JsonProcessingException, IOException {
//		HttpRequestFactory factory = getRequestFactory();
//		GenericUrl url = new GenericUrl(urlStr);
//		url.putAll(params);
//		url.put("key", Config.get(Keys.GOOG_SERVER_API_KEY).or(EMPTY));
//		try {
//			HttpRequest req = factory.buildGetRequest(url);
//			HttpResponse res = req.execute();
//			String response = IOUtils.toString(res.getContent());
//			GmapsNearbyResponse nearbyData = mapper.readValue(response, GmapsNearbyResponse.class);
//			if (debug) {
//				log.info("Reply to " + url + System.lineSeparator() + "is: " + nearbyData);
//			}
//			return nearbyData;
//		} catch (JsonProcessingException e) {
//			log.log(Level.WARNING, "Error parsing response of GMaps api call. Stacktrace:", e);
//			throw e;
//		} catch (IOException e) {
//			log.log(Level.WARNING, "Error making GMaps api call. Stacktrace:", e);
//			throw e;
//		}
//	}
//
//	public static GmapsNearbyResponse gmapsCall(String urlStr, GmapsCall params, boolean debug)
//			throws JsonProcessingException, IOException {
//		return gmapsCall(urlStr, params.asMap(), debug);
//	}
//
//	public static GmapsNearbyResponse gmapsCall(String urlStr, GmapsCall params)
//			throws JsonProcessingException, IOException {
//		return gmapsCall(urlStr, params, false);
//	}

	public static JsonFactory getJsonFactory() {
		return jacksonFactory;
	}

	// Registration token:
	// nFtrGlHegjs:APA91bH0mJiGN58zKfSe5HhtEvnt8-QO1djlX0iHDTiO1fxS375WAuuLEygpxq8OiD1Dg6O3MyE39Hryw2tsTBolvB24FM8IYStDYNVyGBZDVEtVq1ChqHNpvkNdVRuQOCZSEFcoNG2M
	// Device id: e0f961349fcfcbd465935dca12249f8e503063bc407d41e3ecb7be5ca6f37b2f
	public static GcmResponse sendGcmMessage(Map<String, Object> message, boolean debug)
			throws JsonParseException, JsonMappingException, IOException {
		HttpRequestFactory factory = getGcmRequestFactory();
		JsonHttpContent content = new JsonHttpContent(getJsonFactory(), message);
		String response = EMPTY;
		GcmResponse resp = null;
		Object json;
		try {
			HttpRequest req = factory.buildPostRequest(gcmDownstreamUrl, content);
			ExponentialBackOff backOff = new ExponentialBackOff.Builder().build();
			req.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(backOff));
			if (debug) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				content.writeTo(ps);
				json = mapper.readValue(baos.toString(), Object.class);
				ps.close();
				baos.close();
				log.info("JSON sent to GCM: " + mapperPretty.writeValueAsString(json));
			}
			HttpResponse res = req.execute();
			response = IOUtils.toString(res.getContent());
			resp = mapper.readValue(response, GcmResponse.class);
			resp.setStatusCode(res.getStatusCode()).setStatusMessage(res.getStatusMessage());
			if (debug) {
				json = mapper.readValue(response, Object.class);
				log.info("Response from GCM: " + mapperPretty.writeValueAsString(json));
				log.info("GcmResponse.toString(): " + resp);
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "Exception log...", e);
			throw e;
		}
		return resp;
	}

	public static GcmResponse sendGcmMessage(Map<String, Object> message)
			throws JsonParseException, JsonMappingException, IOException {
		return sendGcmMessage(message, false);
	}

	public static GcmResponse sendGcmMessage(GcmMessage mesg, boolean debug)
			throws JsonParseException, JsonMappingException, IOException {
		return sendGcmMessage(mesg.asMap(), debug);
	}

	public static GcmResponse sendGcmMessage(GcmMessage mesg)
			throws JsonParseException, JsonMappingException, IOException {
		return sendGcmMessage(mesg.asMap(), false);
	}

	public static void multipartFormSubmit(String url, byte[] userKey, byte[] merchKey,
			List<byte[]> dataList) {
		checkNotNull(dataList);
		GenericUrl imgSubmitUrl = new GenericUrl(url);
		HttpRequestFactory factory = getRequestFactory();

		MultipartContent multContent = new MultipartContent();
		multContent.setMediaType(new HttpMediaType("multipart/form-data").setParameter("boundary",
				"__END_OF_PART__"));

		if (null != userKey) {
			Part userWebKey = new Part(
					new ByteArrayContent(MediaType.PLAIN_TEXT_UTF_8.toString(), userKey));
			userWebKey.setHeaders(
					new HttpHeaders().set(com.google.common.net.HttpHeaders.CONTENT_DISPOSITION,
							String.format("form-data; name=\"%s\"", FIELD_PRINCIPAL_KEY)));
			multContent.addPart(userWebKey);
		}
		if (null != merchKey) {
			Part merchWebKey = new Part(
					new ByteArrayContent(MediaType.PLAIN_TEXT_UTF_8.toString(), merchKey));
			merchWebKey.setHeaders(
					new HttpHeaders().set(com.google.common.net.HttpHeaders.CONTENT_DISPOSITION,
							String.format("form-data; name=\"%s\"", FIELD_MERCHANT_KEY)));
			multContent.addPart(merchWebKey);
		}

		Part image = null;
		int counter = 0;
		for (byte[] data : dataList) {
			image = new Part(new ByteArrayContent(MediaType.JPEG.toString(), data));
			image.setHeaders(
					new HttpHeaders().set(com.google.common.net.HttpHeaders.CONTENT_DISPOSITION,
							String.format("form-data; name=\"%s\"; filename=\"image%s.jpg\"",
									(FIELD_PROFILE_IMG + counter), counter++)));
			multContent.addPart(image);
		}
		Stopwatch timer = Stopwatch.createUnstarted();
		try {
			timer.start();
			HttpRequest req = factory.buildPostRequest(imgSubmitUrl, multContent);
			HttpResponse res = req.execute();
			log.info("Time taken to submit image data: " + timer.stop());
			String resStr = IOUtils.toString(res.getContent());
			log.info("Response to image upload: " + resStr);
		} catch (HttpResponseException e) {
			log.warning("HttpResponseException: " + e.getMessage());
		} catch (IOException e) {
			log.info("Time before image data submission failed: " + timer.stop());
			log.log(Level.WARNING, "Exception while uploading to Blobstore", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 30;
		StringBuilder builder2 = new StringBuilder();
		builder2.append("NetCall [");
		if (this.urlStr != null) {
			builder2.append("urlStr=").append(this.urlStr).append(", ");
		}
		if (this.method != null) {
			builder2.append("method=").append(this.method).append(", ");
		}
		if (this.params != null) {
			builder2.append("params=").append(toString(this.params.entrySet(), maxLen))
					.append(", ");
		}
		if (this.paramStr != null) {
			builder2.append("paramStr=").append(this.paramStr);
		}
		builder2.append("]");
		return builder2.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) {
				builder2.append(", ");
			}
			builder2.append(iterator.next());
		}
		builder2.append("]");
		return builder2.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NetCall))
			return false;
		NetCall other = (NetCall) obj;
		if (this.method != other.method)
			return false;
		if (this.paramStr == null) {
			if (other.paramStr != null)
				return false;
		} else if (!this.paramStr.equals(other.paramStr))
			return false;
		if (this.params == null) {
			if (other.params != null)
				return false;
		} else if (!this.params.equals(other.params))
			return false;
		if (this.urlStr == null) {
			if (other.urlStr != null)
				return false;
		} else if (!this.urlStr.equals(other.urlStr))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
		result = prime * result + ((this.paramStr == null) ? 0 : this.paramStr.hashCode());
		result = prime * result + ((this.params == null) ? 0 : this.params.hashCode());
		result = prime * result + ((this.urlStr == null) ? 0 : this.urlStr.hashCode());
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public enum Method {
		GET, POST,
	}

	public static class Builder {

		private String _url;

		private Method _method;

		private Builder() {
		}

		public NetCall build() throws MalformedURLException, NullPointerException {
			if (!Validators.url.isValid(this._url))
				throw new MalformedURLException("'" + this._url + "' is not a valid URL.");
			checkNotNull(this._method);
			NetCall helper = new NetCall(this._url);
			return helper;
		}

		public Builder setUrl(String url) {
			this._url = url;
			return this;
		}

		public Builder setMethod(Method method) {
			this._method = method;
			return this;
		}
	}
}
