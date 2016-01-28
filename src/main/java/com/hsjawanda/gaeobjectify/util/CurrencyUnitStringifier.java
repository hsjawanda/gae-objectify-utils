/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import org.joda.money.CurrencyUnit;

import com.googlecode.objectify.stringifier.Stringifier;


/**
 * @author harsh.deep
 *
 */
public class CurrencyUnitStringifier implements Stringifier<CurrencyUnit> {

	@Override
	public CurrencyUnit fromString(String str) {
		return CurrencyUnit.of(str);
	}

	@Override
	public String toString(CurrencyUnit obj) {
		return obj.getCode();
	}

}
