/**
 *
 */
package com.hsjawanda.gaeobjectify.json;

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/**
 * @author harsh.deep
 *
 */
public class PlainMoneySerializer extends JsonSerializer<Money> {

	@Override
	public void serialize(Money amount, JsonGenerator gen, SerializerProvider sp)
			throws IOException, JsonProcessingException {
		if (null != amount) {
			gen.writeString(amount.getAmount().toPlainString());
		}
	}
}
