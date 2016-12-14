/**
 *
 */
package com.hsjawanda.gaeobjectify.data;

import java.util.List;

import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.hsjawanda.gaeobjectify.models.Category;
import com.hsjawanda.gaeobjectify.util.Pager;

/**
 * @author Harshdeep Jawanda <hsjawanda@gmail.com>
 *
 */
public class CategoryDao extends ObjectifyDao<Category> {

	public CategoryDao() {
		super(Category.class);
	}

	public List<Category> getAllForNamespace(String namespace) {
		Filter filter = new FilterPredicate("namespace", FilterOperator.EQUAL,
				Category.normalizeNamespace(namespace));
		this.log.info("filter: " + filter);
		return super.filteredQuery(filter);
	}

	public List<Category> getOrderedForNamespace(String namespace) {
		Filter namespaceFilter = new FilterPredicate("namespace", FilterOperator.EQUAL,
				Category.normalizeNamespace(namespace));
		Filter showOnHomepageFilter = new FilterPredicate("showOnHomepage", FilterOperator.EQUAL,
				Boolean.TRUE);
		Pager<Category> pgr = Pager.<Category>builder().limit(50).build();
		return getPaginatedEntities(pgr,
				CompositeFilterOperator.and(namespaceFilter, showOnHomepageFilter), "order");
	}

}
