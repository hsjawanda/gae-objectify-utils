package com.hsjawanda.gaeobjectify.services;

import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hsjawanda.gaeobjectify.util.Config;

public class Outcome {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Outcome.class.getName());

	public static final Outcome	SUCCESS					= new Outcome("SUCCESS");

	public static final Outcome SUCCESS_NO_RESULTS		= new Outcome("SUCCESS_NO_RESULTS");

	public static final Outcome	FAIL					= new Outcome("FAIL");

	public static final Outcome	FAIL_ENTITY_NOT_FOUND	= new Outcome("FAIL_ENTITY_NOT_FOUND");

	public static final Outcome	FAIL_INTERNAL_ERROR		= new Outcome("FAIL_INTERNAL_ERROR");

	public static final Outcome	FAIL_INVALID_INPUT		= new Outcome("FAIL_INVALID_INPUT");

	public static final Outcome	FAIL_TOKEN_EXPIRED		= new Outcome("FAIL_TOKEN_EXPIRED");

	public static final Outcome	FAIL_TOKEN_INVALID		= new Outcome("FAIL_TOKEN_INVALID");

	public static final Outcome FAIL_UNIQUE_VIOLATION	= new Outcome("FAIL_UNIQUE_VIOLATION");

	protected String			name;

	protected Outcome(String name) {
		this.name = name;
	}

	@JsonIgnore
	public String getMessageFormat() {
		return Config.getOrEmpty(this.name);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
