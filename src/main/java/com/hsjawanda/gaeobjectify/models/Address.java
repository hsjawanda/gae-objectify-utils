/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.UniqueIdGenerator;

import lombok.Singular;


/**
 * @author Harsh.Deep
 *
 */
@Entity
@JsonIgnoreProperties({ "id", "addressFor", "uniqueId" })
public class Address<T> extends DatedEntity {

	protected static final Joiner joiner = Joiner.on(",<br />").skipNulls();

	protected static final Joiner spaceJoiner = Joiner.on(' ').skipNulls();

	@Id
	protected String id;

	protected static final int maxAddrLines = 5;

	protected List<String> addrLines = new ArrayList<>(3);

	protected String city;

	protected String state;

	protected String country;

	protected String postalCode;

	protected String postalCodeExtn;

	protected Ref<T> addressFor;

	protected Address() {
		this.id = UniqueIdGenerator.medium();
	}

	// @Builder
	protected Address(@Singular List<String> addrLines, String city, String state, String country,
			String postalCode, String postalCodeExtn) {
		this();
		this.addrLines.addAll(addrLines);
		this.city = city;
		this.state = state;
		this.country = country;
		this.postalCode = postalCode;
		this.postalCodeExtn = postalCodeExtn;
	}

	/**
	 * @return the addr
	 */
	public List<String> getAddrLines() {
		if (null != this.addrLines)
			return Collections.unmodifiableList(this.addrLines);
		return Collections.emptyList();
	}

	@JsonIgnore
	public String getAddrLine() {
		StringBuilder sb = new StringBuilder();
		if (isNotBlank(this.postalCode)) {
			sb.append(' ').append(this.postalCode);
		}
		if (isNotBlank(this.postalCodeExtn)) {
			sb.append('-').append(this.postalCodeExtn);
		}
		return joiner.join(joiner.join(this.addrLines), this.city, this.state) + sb.toString();
	}

	public Address<T> addAddrLine(String addrLine) {
		if (this.addrLines.size() >= maxAddrLines)
			return this;
		this.addrLines.add(abbreviate(addrLine, Constants.gaeStringLength));
		return this;
	}

	public Address<T> setAddrLine(int index, String addrLine) {
		if (index >= maxAddrLines)
			return this;
		this.addrLines.set(index, addrLine);
		return this;
	}

	public Address<T> clearAddrLines() {
		this.addrLines.clear();
		return this;
	}

	/**
	 * @return the city
	 */
	public final String getCity() {
		return this.city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public final void setCity(String city) {
		this.city = trimToNull(city);
	}

	/**
	 * @return the country
	 */

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = trimToNull(state);
	}

	public final String getCountry() {
		return this.country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public final void setCountry(String country) {
		this.country = trimToNull(country);
	}

	/**
	 * @return the postalCode
	 */
	public final String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	public final void setPostalCode(String postalCode) {
		this.postalCode = trimToNull(postalCode);
	}

	/**
	 * @return the postalCodeExtn
	 */
	public final String getPostalCodeExtn() {
		return this.postalCodeExtn;
	}

	/**
	 * @param postalCodeExtn
	 *            the postalCodeExtn to set
	 */
	public final void setPostalCodeExtn(String postalCodeExtn) {
		this.postalCodeExtn = trimToNull(postalCodeExtn);
	}

	/**
	 * @return the addressFor
	 */
	public Optional<T> getAddressFor() {
		return GaeDataUtil.getByRef(this.addressFor);
	}

	@JsonIgnore
	public Ref<T> getAddressForRef() {
		return this.addressFor;
	}

	/**
	 * @param addressFor
	 *            the addressFor to set
	 */
	public void setAddressFor(T addressFor) {
		this.addressFor = GaeDataUtil.getNullableRefFromPojo(addressFor);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.addrLines == null) ? 0 : this.addrLines.hashCode());
		result = prime * result + ((this.city == null) ? 0 : this.city.hashCode());
		result = prime * result + ((this.country == null) ? 0 : this.country.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.postalCode == null) ? 0 : this.postalCode.hashCode());
		result = prime * result
				+ ((this.postalCodeExtn == null) ? 0 : this.postalCodeExtn.hashCode());
		result = prime * result + ((this.state == null) ? 0 : this.state.hashCode());
		return result;
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
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Address))
			return false;
		@SuppressWarnings("rawtypes")
		Address other = (Address) obj;
		if (this.addrLines == null) {
			if (other.addrLines != null)
				return false;
		} else if (!this.addrLines.equals(other.addrLines))
			return false;
		if (this.city == null) {
			if (other.city != null)
				return false;
		} else if (!this.city.equals(other.city))
			return false;
		if (this.country == null) {
			if (other.country != null)
				return false;
		} else if (!this.country.equals(other.country))
			return false;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.postalCode == null) {
			if (other.postalCode != null)
				return false;
		} else if (!this.postalCode.equals(other.postalCode))
			return false;
		if (this.postalCodeExtn == null) {
			if (other.postalCodeExtn != null)
				return false;
		} else if (!this.postalCodeExtn.equals(other.postalCodeExtn))
			return false;
		if (this.state == null) {
			if (other.state != null)
				return false;
		} else if (!this.state.equals(other.state))
			return false;
		return true;
	}
}
