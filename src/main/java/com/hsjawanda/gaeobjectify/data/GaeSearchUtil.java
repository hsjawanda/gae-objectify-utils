/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.api.search.StatusCode;
import com.hsjawanda.gaeobjectify.repackaged.commons.lang3.tuple.ImmutablePair;
import com.hsjawanda.gaeobjectify.util.Constants;


/**
 * @author harsh.deep
 *
 */
public class GaeSearchUtil {

	private static final Logger log = Logger.getLogger(GaeSearchUtil.class.getName());

	private static final int maxTries = 5;

	public static final int BATCH_MAX = 200;

	public static boolean indexDocument(String indexName, Document document) {
		Index index = getIndex(indexName);

		boolean keepTrying = true;
		boolean success = false;
		for (int i = 0; i < maxTries && keepTrying; i++) {
			try {
				index.put(document);
				keepTrying = false;
				success = true;
			} catch (PutException e) {
				if (!StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
					keepTrying = false;
					log.log(Level.WARNING, "Couldn't PUT a document. Stacktrace below...", e);
				}
			}
		}
		if (keepTrying) {
			log.warning("Failed to PUT a document despite trying " + maxTries + " times.");
		}
		return success;
	}

	public static boolean indexDocuments(String indexName, Iterable<Document> docs) {
		Index index = getIndex(indexName);
		boolean success = false, keepTrying = true;
		for (int i = 0; i < maxTries && keepTrying; i++) {
			try {
				index.put(docs);
				keepTrying = false;
				success = true;
			} catch (PutException e) {
				if (!StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
					keepTrying = false;
					log.log(Level.WARNING, "Couldn't PUT a document. Stacktrace below...", e);
				}
			}
		}
		if (keepTrying) {
			log.warning("Failed to PUT a document despite trying " + maxTries + " times.");
		}
		return success;
	}

	public static void deleteDocument(String indexName, String docId) {
		Index index = getIndex(indexName);
		if (null != index) {
			index.delete(docId);
		}
	}

	public static void deleteDocuments(String indexName, Iterable<String> docIds) {
		Index index = getIndex(indexName);
		if (null != index) {
			int max = BATCH_MAX;
			List<String> idList = new ArrayList<>(max);
			Iterator<String> iter = docIds.iterator();
			while (iter.hasNext()) {
				idList.clear();
				for (int i = 0; i < max && iter.hasNext(); i++) {
					idList.add(iter.next());
				}
				index.delete(idList);
			}
		}
	}

	public static void deleteIndexContents(String indexName) {
		Index index = getIndex(indexName);
		if (null != index) {
			GetRequest req = GetRequest.newBuilder().setReturningIdsOnly(true).setLimit(BATCH_MAX)
					.build();
			List<String> docIds = new ArrayList<>(BATCH_MAX);
			while (true) {
				GetResponse<Document> resp = index.getRange(req);
				if (resp.getResults().isEmpty()) {
					break;
				} else {
					docIds.clear();
					for (Document doc : resp) {
						docIds.add(doc.getId());
					}
					deleteDocuments(indexName, docIds);
				}
			}
		}
	}

	public static Document getByDocId(String indexName, String docId) {
		Index index = getIndex(indexName);
		if (null != index)
			return index.get(docId);
		return null;
	}

	/**
	 * @param indexName
	 * @param searchStr
	 * @param qob
	 * @param sob
	 * @return the <code>Results</code> of the search. Could be <code>null</code>.
	 */
	public static <T> Results<ScoredDocument> getBySearch(String indexName, String searchStr,
			QueryOptions.Builder qob, SortOptions.Builder sob) {
		Results<ScoredDocument> docs;
		Index index = getIndex(indexName);
		if (null == index) {
			log.warning("Couldn't find index with name " + indexName);
			return null;
		}
		Query.Builder qryBldr = Query.newBuilder();
		if (null != qob) {
			if (null != sob) {
				qob.setSortOptions(sob);
			}
			qryBldr.setOptions(qob);
		}
		docs = index.search(qryBldr.build(searchStr));
		return docs;
	}

	public static ImmutablePair<String, SortExpression> geoSearchQry(GeoPoint centre, double radius,
			String propertyName) {
		checkNotNull(centre);
		checkArgument(radius > 0.0d, "radius has to be > 0.0");
		checkArgument(isNotBlank(propertyName), "propertyName" + Constants.NOT_BLANK);
		String distance = String.format("distance(%s, geopoint(%f, %f))", propertyName,
				centre.getLatitude(), centre.getLongitude());
		SortExpression se = SortExpression.newBuilder().setExpression(distance)
				.setDirection(SortExpression.SortDirection.ASCENDING).build();
		String query = String.format("%s < %f", distance, radius);
		return ImmutablePair.of(query, se);
	}

	public static String distanceQuery(GeoPoint centre, String propertyName) {
		return String.format("distance(%s, geopoint(%f, %f))", propertyName, centre.getLatitude(),
				centre.getLongitude());
	}

	public static Index getIndex(String indexName) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		return index;
	}

	private GaeSearchUtil() {
	}
}
