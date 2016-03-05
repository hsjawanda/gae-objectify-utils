/**
 *
 */
package com.hsjawanda.gaeobjectify.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.googlecode.objectify.Work;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class TagManager {

	private static final Logger log = Logger.getLogger(TagManager.class.getName());

	private static Map<String, TagStore> stores = new HashMap<>();

	public static TagStore getStore(String tagType) {
//		Stopwatch timer = Stopwatch.createStarted();
		final String tagTypeNormalized = TagStore.normalizeTagType(tagType);
//		long normalizationTime = timer.elapsed(TimeUnit.MICROSECONDS);
		if (stores.containsKey(tagTypeNormalized)) {
			// log.info("Time to retrieve (found) TagStore for key '" + tagType + "': "
			// + timer.stop().elapsed(TimeUnit.MICROSECONDS)
			// + " microsec. Normalization time: " + normalizationTime + " microsec");
			log.info("Fetching TagStore from MEMORY for tagType: " + tagTypeNormalized);
			return stores.get(tagTypeNormalized);
		} else {
			TagStore theStore = ofy().transact(new Work<TagStore>() {

				@Override
				public TagStore run() {
					TagStore aggre = TagStore.DAO.getById(tagTypeNormalized).orNull();
					if (null == aggre) {
						log.info("Created a NEW TagStore for tagType: " + tagTypeNormalized);
						aggre = new TagStore(tagTypeNormalized);
						TagStore.DAO.saveEntity(aggre);
					} else {
						log.info("Fetching TagStore from DS for tagType: " + tagTypeNormalized);
					}
					return aggre;
				}
			});
			stores.put(tagTypeNormalized, theStore);
//			log.info("Time to retrieve (retrieved) TagStore for key '" + tagType + "': "
//					+ timer.stop().elapsed(TimeUnit.MICROSECONDS)
//					+ " microsec. Normalization time: " + normalizationTime + " microsec");
			return theStore;
		}
	}

	public static void saveStores() {
		for (String storeType : stores.keySet()) {
			stores.get(storeType).save();
		}
	}

}
