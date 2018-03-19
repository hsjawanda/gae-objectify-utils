/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isNotBlank;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.normalizeSpace;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.trimToNull;

import java.util.logging.Logger;

import com.hsjawanda.gaeobjectify.repackaged.commonslang3.tuple.Triple;

import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.hsjawanda.gaeobjectify.data.GaeDataUtil;
import com.hsjawanda.gaeobjectify.util.Constants;
import com.hsjawanda.gaeobjectify.util.SplitJoin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
@Accessors(chain = true)
public class UniqueIndex {

	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(UniqueIndex.class.getName());

	@Id
	@Setter(AccessLevel.NONE)
	private String				id;

	@Index
	@Setter(AccessLevel.NONE)
	private String				namespace;

	@Setter(AccessLevel.NONE)
	private String				value;

	private String				refWebKey;

	private UniqueIndex() {
	}

	public static UniqueIndex instance(String namespace, String value)
			throws IllegalArgumentException {
		Triple<String, String, String> triad = getTriple(namespace, value);
		UniqueIndex ui = new UniqueIndex();
		ui.id			= triad.getLeft();
		ui.namespace	= triad.getMiddle();
		ui.value		= triad.getRight();
		return ui;
	}

	public UniqueIndex setRefWebKey(String refWebKey) throws IllegalArgumentException {
		checkArgument(isNotBlank(refWebKey), "refWebKey" + Constants.NOT_BLANK);
		this.refWebKey = refWebKey;
		return this;
	}

	public <T> Optional<T> getReferenced() throws ClassCastException {
		if (null == this.refWebKey)
			return Optional.absent();
		return GaeDataUtil.getByWebKey(this.refWebKey);
	}

	public static String genIdFor(String namespace, String value) {
		Triple<String, String, String> triad = getTriple(namespace, value);
		return triad.getLeft();
	}

	private static Triple<String, String, String> getTriple(String namespace, String value) {
		checkArgument(isNotBlank(value), "value" + Constants.NOT_BLANK);
		namespace = trimToNull(normalizeSpace(namespace));
		value = normalizeSpace(value);
		String id = SplitJoin.join(namespace, value); // TODO: slug-ify value??
		return Triple.of(id, namespace, value);
	}

}
