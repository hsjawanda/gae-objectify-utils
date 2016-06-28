/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import com.hsjawanda.gaeobjectify.models.TagStore;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class TagStoreDao extends ObjectifyDao<TagStore> {

	public TagStoreDao() {
		super(TagStore.class);
	}

//	public TagAggregator instance(final String tagCategory) {
//		TagAggregator agg = ofy().transact(new Work<TagAggregator>() {
//
//			@Override
//			public TagAggregator run() {
//				String tagCat = trimToNull(tagCategory);
//				checkNotNull(tagCat, "tagCategory" + Constants.NOT_BLANK);
//				TagAggregator aggre = getById(tagCat).orNull();
//				if (null == aggre) {
//					aggre = new TagAggregator(tagCat);
//					saveEntity(aggre);
//				}
//				return aggre;
//			}
//		});
//		return agg;
//	}
}
