/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.hsjawanda.gaeobjectify.util.Constants;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
public class BlobstoreHelper {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(BlobstoreHelper.class.getName());

	private BlobstoreHelper() {
	}

	public static Map<String, List<BlobKey>> extractBlobKeys(HttpServletRequest req) {
		checkNotNull(req);
		BlobstoreService blobSvc = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, List<BlobKey>> blobMap = blobSvc.getUploads(req);
		return blobMap;
	}

	public static List<BlobKey> extractBlobKeys(HttpServletRequest req, String fieldName) {
		checkArgument(isNotBlank(fieldName), "fieldName" + Constants.notBlank);
		Map<String, List<BlobKey>> blobs = extractBlobKeys(req);
		List<BlobKey> retVal = blobs.get(fieldName);
		return null == retVal ? Collections.<BlobKey> emptyList() : retVal;
	}

	public static List<BlobKey> extractBlobKeys(Map<String, List<BlobKey>> blobs,
			String fieldNameStem) {
		checkNotNull(blobs);
		checkArgument(isNotBlank(fieldNameStem), "fieldNameStem" + Constants.notBlank);
		List<BlobKey> retVal = null;
		for (int i = 0; true; i++) {
			List<BlobKey> blobKeys = blobs.get(fieldNameStem + i);
			if (null != blobKeys) {
				if (null == retVal) {
					retVal = new LinkedList<>();
				}
				retVal.addAll(blobKeys);
			} else {
				break;
			}
		}
		return null == retVal ? Collections.<BlobKey> emptyList() : retVal;
	}
}
