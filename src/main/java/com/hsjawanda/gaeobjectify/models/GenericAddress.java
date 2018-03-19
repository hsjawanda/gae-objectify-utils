/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.trimToNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.hsjawanda.gaeobjectify.util.Constants;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
@Accessors(chain = true, fluent = true)
public class GenericAddress {

//	private static final int INITIAL_CAPACITY = 4;

//	private List<String> addrLines = NonNullList.empty(INITIAL_CAPACITY);

	@Setter(AccessLevel.NONE)
	private Map<Field, String> details = new EnumMap<>(Field.class);

//	public List<String> addrLines() {
//		return Collections.unmodifiableList(this.addrLines);
//	}
//
//	public GenericAddress addrLines(List<String> list) {
//		if (null != list) {
//			this.addrLines.clear();
//			for (String line : list) {
//				line = trimToNull(line);
//				if (null != line) {
//					if (line.endsWith(",")) {
//						line = line.substring(0, line.length() - 1);
//					}
//					this.addrLines.add(line);
//				}
//			}
//		}
//		return this;
//	}
//
//	public GenericAddress addAddrLine(String line) {
//		line = trimToNull(line);
//		if (null != line) {
//			this.addrLines.add(abbreviate(line, Constants.gaeStringLength));
//		}
//		return this;
//	}

	/**
	 * Get the address details.
	 *
	 * @return an <b>unmodifiable</b> {@code Map} of address details.
	 */
	public Map<Field, String> details() {
		return Collections.unmodifiableMap(this.details);
	}

	public GenericAddress set(Field field, String value) {
		checkNotNull(field, "field" + Constants.NOT_NULL);
		value = trimToNull(value);
		this.details.put(field, value);
		return this;
	}

	public String get(Field field) {
		checkNotNull(field, "field" + Constants.NOT_NULL);
		return this.details.get(field);
	}

	public String plainText() {
		StringBuilder addr = new StringBuilder(100);
		for (Field lineField : Field.values()) {
			String val = get(lineField);
			if (null != val) {
				addr.append(val);
				if (lineField != Field.COUNTRY) {
					addr.append(',').append(System.lineSeparator());
				}
			}
		}
		return addr.toString();
	}

	public enum Field {
		LINE1, LINE2, LINE3, LINE4, CITY, STATE, POSTAL_CODE, POSTAL_CODE_EXTN, COUNTRY;

		private static final List<Field> addrLineFields = new ArrayList<>(Field.values().length);

		public static List<Field> getAddrLines() {
			if (addrLineFields.size() == 0) {
				for (Field field : Field.values()) {
					if (field.name().startsWith("LINE")) {
						addrLineFields.add(field);
					}
				}
			}
			return addrLineFields;
		}
	}

}
