package com.hsjawanda.gaeobjectify.json;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hsjawanda.gaeobjectify.services.Outcome;


public class OutcomeSerializer extends JsonSerializer<Outcome> {

	@SuppressWarnings("unused")
	private static final Logger			log		= Logger
			.getLogger(OutcomeSerializer.class.getName());

	private static final SimpleModule	module	= new SimpleModule().addSerializer(Outcome.class,
			new OutcomeSerializer());

	@Override
	public void serialize(Outcome value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeString(value.toString());
	}

	public static SimpleModule getModule() {
		return module;
	}

}
