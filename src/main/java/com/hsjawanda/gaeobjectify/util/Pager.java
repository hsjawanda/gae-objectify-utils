/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
public class Pager<T> {

	public static final int			PAGE_NUM_MIN	= 1;

	public static Range<Integer>	PER_PAGE_RANGE	= Range.closed(5, 100);

	private int						pageNum			= PAGE_NUM_MIN;

	private int						itemsPerPage	= 20;

	private Cursor					cursor;

	private int						totalResults	= -1;

	private int						countLimit		= 5000;

	@Setter(AccessLevel.NONE)
	private int						totalPages		= 0;

	private boolean					genTotalResults	= true;

	private List<T>					results;

	public List<T> getResults() {
		if (null == this.results)
			return Collections.emptyList();
		return Collections.unmodifiableList(this.results);
	}

	/**
	 * The <code>results</code> can be set at most once.
	 *
	 * @param results
	 *            the results to set
	 */
	public void setResults(List<T> results) {
		if (null == this.results) {
			this.results = results;
		}
	}

	/**
	 * @param pageNum
	 *            the pageNum to set
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = Math.max(PAGE_NUM_MIN, pageNum);
	}

	/**
	 * @return the itemsPerPage
	 */
	public int getItemsPerPage() {
		return this.itemsPerPage;
	}

	/**
	 * @param itemsPerPage
	 *            the itemsPerPage to set
	 */
	public void setItemsPerPage(int itemsPerPage) {
		if (PER_PAGE_RANGE.contains(itemsPerPage)) {
			this.itemsPerPage = itemsPerPage;
		}
	}

	public void setTotalResults(int totalResults) {
		if (totalResults >= 0 && this.totalResults < 0) {
			this.totalResults = totalResults;
			this.calcTotalPages();
		}
	}

	public void setCountLimit(int limit) {
		if (limit >= 100) {
			this.countLimit = limit;
		}
	}

	private void calcTotalPages() {
		this.totalPages = this.totalResults / this.itemsPerPage;
		if (this.totalResults % this.itemsPerPage > 0) {
			this.totalPages++;
		}
	}

	/**
	 * Get the zero-based offset into the result set.
	 *
	 * @return the zero-based offset.
	 */
	public int getOffset() {
		return (this.pageNum - PAGE_NUM_MIN) * this.itemsPerPage;
	}

	/**
	 * Convenience method giving the number of pages that come before the current page.
	 *
	 * @return the number of previous pages.
	 */
	public int numPagesBefore() {
		return this.getOffset() / this.itemsPerPage;
	}

	/**
	 * Convenience method giving the number of pages that come after the current page.
	 *
	 * @return the number of succeeding pages.
	 */
	public int numPagesAfter() {
		return Math.max(0, this.totalPages - this.pageNum);
	}

	public boolean hasMore() {
		return !(this.totalResults < this.countLimit);
	}

}
