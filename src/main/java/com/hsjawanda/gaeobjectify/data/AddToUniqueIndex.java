/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.hsjawanda.gaeobjectify.models.GaeEntity;
import com.hsjawanda.gaeobjectify.models.UniqueIndex;

/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class AddToUniqueIndex implements Work<Key<UniqueIndex>> {

	private UniqueIndex uniqueIdx;

	private GaeEntity refEntity;

	private AddToUniqueIndex(UniqueIndex uniqueIdx, GaeEntity referencedEntity) {
		this.uniqueIdx = uniqueIdx;
		this.refEntity = referencedEntity;
	}

	public static AddToUniqueIndex instance(String namespace, String value,
			GaeEntity referencedEntity) throws IllegalArgumentException {
		UniqueIndex uniqIdx = UniqueIndex.instance(namespace, value);
		return new AddToUniqueIndex(uniqIdx, referencedEntity);
	}

	@Override
	public Key<UniqueIndex> run() {
		Key<UniqueIndex> key = null;
		UniqueIndex entity = ofy().load().type(UniqueIndex.class).id(this.uniqueIdx.getId()).now();
		if (null == entity) {
			String webKey = GaeDataUtil.getWebKeyFromPojo(this.refEntity);
			if (null != webKey) {
				this.uniqueIdx.setRefWebKey(webKey);
			}
			key = ofy().save().entity(this.uniqueIdx).now();
		}
		return key;
	}

}
