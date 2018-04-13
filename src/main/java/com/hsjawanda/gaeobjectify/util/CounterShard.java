package com.hsjawanda.gaeobjectify.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.hsjawanda.gaeobjectify.models.StringIdEntity;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 */
@Entity
@Cache(expirationSeconds = 60)
@Data
@EqualsAndHashCode(of = {"id"})
@Accessors(chain = true)
public class CounterShard implements Serializable, StringIdEntity {

	/**
	 * 05 Jun 2017
	 */
	private static final long	serialVersionUID	= 1L;

	@Id
	@Setter(AccessLevel.PRIVATE)
	private String	id;

	private long	count	= 0;

	private CounterShard() {
	}

	static CounterShard create(@Nonnull String counterId, @Nullable Long initialValue)
			throws IllegalArgumentException {
		checkArgument(isNotBlank(counterId), "counterId" + Constants.NOT_BLANK);
		CounterShard sCounter = new CounterShard().setId(counterId).setCount(initialValue);
		return sCounter;
	}

	private CounterShard setCount(Long initialValue) {
		this.count = null == initialValue ? 0 : initialValue.longValue();
		return this;
	}

	public void increment(long delta) {
		this.count += delta;
	}

}
