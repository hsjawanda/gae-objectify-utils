/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.googlecode.objectify.ObjectifyService.ofy;
import lombok.NonNull;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.hsjawanda.gaeobjectify.models.UniqueProperty;


/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class IndexUniqueProperty implements Work<Key<UniqueProperty>> {

	private UniqueProperty uniqProp;

	public IndexUniqueProperty(@NonNull UniqueProperty uniqueProperty) {
		this.uniqProp = uniqueProperty;
	}

	@Override
	public Key<UniqueProperty> run() {
		Key<UniqueProperty> retKey = null;
		UniqueProperty ret = ofy().load().type(UniqueProperty.class).id(this.uniqProp.getId()).now();
		if (null == ret) {
			retKey = ofy().save().entity(this.uniqProp).now();
		}
		return retKey;
	}

}
