package com.hsjawanda.gaeobjectify.json;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author Harsh.Deep
 *
 */
@Data
@Accessors(chain = true, fluent = true)
public class GcmMessage {

	private String to;

	@Setter(value = AccessLevel.NONE)
	private List<String> regIds;

	@Setter(value = AccessLevel.NONE)
	private Map<String, String> data;

	@Setter(value = AccessLevel.NONE)
	private Map<String, String> notif;

	@Setter(value = AccessLevel.NONE)
	private String collapse_key;

	@Setter(value = AccessLevel.NONE)
	private String priority;

	private Boolean content_available;

	private Boolean dry_run;

	public static final String DATA_MESSAGE = "message";

	private static final String[] forbiddenKeys = { "from", "content_available", "dry_run",
			"priority", "collapse_key" };

	public GcmMessage() {
	}

	public GcmMessage addRegistrationId(String regId) throws IllegalStateException {
		if (null != this.to)
			throw new IllegalStateException(
					"Registration Id can't be added as 'to' field is already set.");
		if (null == this.regIds) {
			this.regIds = new ArrayList<>();
		}
		if (isNotBlank(regId)) {
			this.regIds.add(regId);
		}
		return this;
	}

	public GcmMessage to(String to) throws IllegalStateException {
		if (null != this.regIds)
			throw new IllegalStateException(
					"'to' field can't be set as 'registration_ids' are already set");
		this.to = trimToEmpty(to);
		return this;
	}

	public GcmMessage collapse_key(String collapse_key) {
		this.collapse_key = trimToNull(collapse_key);
		return this;
	}

	public GcmMessage priority(Priority priority) {
		if (null == priority) {
			priority = Priority.NORMAL;
		}
		this.priority = priority.toString();
		return this;
	}

	public GcmMessage addData(String key, String value) {
		if (null != key) {
			checkArgument(!key.startsWith("google"), "key can't start with 'google'");
			checkArgument(!key.startsWith("gcm"), "key can't start with 'gcm'");
			for (String forbWord : forbiddenKeys) {
				checkArgument(!key.equalsIgnoreCase(forbWord),
						"'" + forbWord + "' can't be used as a key");
			}
			if (null == this.data) {
				this.data = new LinkedHashMap<>();
			}
			addToMap(this.data, key, value);
		}
		return this;
	}

	public GcmMessage addNotification(Notification notification, String value) {
		if (null != notification) {
			if (null == this.notif) {
				this.notif = new LinkedHashMap<>();
			}
			addToMap(this.notif, notification.toString(), value);
		}
		return this;
	}

	protected void addToMap(Map<String, String> map, String key, String value) {
		if (isNotBlank(key)) {
			map.put(key.trim(), trimToNull(value));
		}
	}

	public Map<String, Object> asMap() {
		Map<String, Object> retMap = new LinkedHashMap<>();
		if (null != this.to) {
			retMap.put("to", this.to);
		}
		if (null != this.priority) {
			retMap.put("priority", this.priority);
		}
		if (null != this.collapse_key) {
			retMap.put("collapse_key", this.collapse_key);
		}
		if (null != this.content_available) {
			retMap.put("content_available", this.content_available);
		}
		if (null != this.dry_run) {
			retMap.put("dry_run", this.dry_run);
		}
		if (null != this.regIds) {
			retMap.put("registration_ids", this.regIds);
		}
		if (null != this.data) {
			retMap.put("data", this.data);
		}
		if (null != this.notif) {
			retMap.put("notification", this.notif);
		}
		return retMap;
	}

	public enum Priority {
		NORMAL, HIGH;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public enum Notification {
		TITLE, BODY;

		@Override
		public String toString() {
			return super.name().toLowerCase();
		}
	}
}
