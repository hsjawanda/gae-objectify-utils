/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Range;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 * @param <T>
 */
public class PagingData<T> {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PagingData.class.getName());

	public static final String PG_NUM = "pgNum";

	public static final String ITEMS_PER_PAGE = "ipp";

	public static final String FM_PD = "pd";

	private List<T> results;

	private static int pgNumMin = 1;

	private int pgNum = Defaults.pgNum;

	private int itemsPerPage = Defaults.itemsPerPage;

	private static Range<Integer> perPageRange = Range.closed(1, 100);

	@Getter
	private int totalResults = -1;

	@Getter
	private int totalPages = 0;

	@Getter
	private int countLimit = 5000;

	@Getter
	@Setter
	private boolean genTotalResults = true;

	@Builder
	private PagingData(int pgNum, int itemsPerPage) {
		this.setPgNum(pgNum);
		this.setItemsPerPage(itemsPerPage);
	}

	/**
	 * @return the results
	 */
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
	 * @return the pgNum
	 */
	public int getPgNum() {
		return this.pgNum;
	}

	/**
	 * @param pgNum
	 *            the pgNum to set
	 */
	public void setPgNum(int pgNum) {
		this.pgNum = Math.max(pgNumMin, pgNum);
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
		if (perPageRange.contains(itemsPerPage)) {
			this.itemsPerPage = itemsPerPage;
		}
	}

	public void setTotalResults(int totalResults) {
		if (totalResults >= 0) {
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
		return (this.pgNum - pgNumMin) * this.itemsPerPage;
	}

	/**
	 * Convenience method giving the number of pages that come before the current page.
	 *
	 * @return the number of previous pages.
	 */
	public int prevPages() {
		return this.getOffset() / this.itemsPerPage;
	}

	/**
	 * Convenience method giving the number of pages that come after the current page.
	 *
	 * @return the number of succeeding pages.
	 */
	public int nextPages() {
		return Math.max(0, this.totalPages - this.pgNum);
	}

	public boolean hasMore() {
		return !(this.totalResults < this.countLimit);
	}
}
