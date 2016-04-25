package com.hsjawanda.gaeobjectify.json;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hsjawanda.gaeobjectify.services.Outcome;

public class OutcomeSerializer extends JsonSerializer<Outcome> {

	private static final Logger log = Logger.getLogger(OutcomeSerializer.class.getName());

	@Override
	public void serialize(Outcome value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		log.info("value: " + value + "; toString(): " + value.toString());
		gen.writeString(value.toString());
	}

}
