/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.isBlank;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Cursor;
import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;


/**
 * @author Harshdeep S Jawanda (hsjawanda@gmail.com)
 *
 */
@Data
public class Pager<T> {

	private static Logger							LOG					= Logger.getLogger(Pager.class
																				.getName());

	public static final int							PAGE_NUM_MIN		= 1;

	public static Range<Integer>					RESULT_LIMIT_RANGE	= Range.closed(1, 5000);

	public static int								DEFAULT_LIMIT		= 200;

	private int										limit;

//	private String					nextCursorStr;

	@JsonIgnore
	private Cursor									cursor;

	private String									cursorStr;

	@JsonIgnore
	private com.google.appengine.api.search.Cursor	searchCursor;

	@JsonIgnore
	private boolean									keysOnly			= false;

	private int										totalResults		= -1;

	private int										countLimit			= 5000;

	@Setter(AccessLevel.NONE)
	private int										totalPages			= 0;

	@Setter(AccessLevel.PRIVATE)
	private boolean									genTotalResults;

	private List<T>									results;

	@Builder
	private Pager(Integer limit, Boolean genTotalResults, String cursorStr, String searchCursorStr) {
		setLimit(Defaults.or(limit, Integer.valueOf(DEFAULT_LIMIT)).intValue());
		setGenTotalResults(Defaults.or(genTotalResults, Boolean.FALSE));
		setCursorStr(cursorStr);
		setSearchCursorStr(searchCursorStr);
	}

	@Nonnull
	public static <T> Pager<T> getDefault(int limit) {
		limit = Holdall.constrainToRange(RESULT_LIMIT_RANGE, Integer.valueOf(limit)).intValue();
		return Pager.<T> builder().limit(limit).build();
	}

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
		if (null != results && null == this.results) {
			this.results = results;
		}
	}

	private void setLimit(int limit) {
		if (limit < RESULT_LIMIT_RANGE.lowerEndpoint().intValue()) {
			this.limit = RESULT_LIMIT_RANGE.lowerEndpoint().intValue();
		}
		else if (limit > RESULT_LIMIT_RANGE.upperEndpoint().intValue()) {
			this.limit = RESULT_LIMIT_RANGE.upperEndpoint().intValue();
		} else {
			this.limit = limit;
		}
	}

	public Pager<T> setCursorStr(String cursorStr) {
		this.cursorStr = cursorStr;
		this.cursor = null;
		if (isBlank(cursorStr))
			return this;
		else {
			try {
				this.cursor = Cursor.fromWebSafeString(cursorStr);
			} catch (IllegalArgumentException e) {
				LOG.warning("The supplied cursor string couldn't be decoded as a valid Cursor: "
						+ cursorStr);
			}
		}
		return this;
	}

	public String getCursorStr() {
		if (null == this.cursor)
			return this.cursorStr;
		return this.cursor.toWebSafeString();
	}

	public String getSearchCursorStr() {
		return null != this.searchCursor ? this.searchCursor.toWebSafeString() : null;
	}

	public com.google.appengine.api.search.Cursor getSearchCursor() {
		if (null == this.searchCursor) {
			this.searchCursor = com.google.appengine.api.search.Cursor.newBuilder().build();
		}
		return this.searchCursor;
	}

	public Pager<T> setSearchCursorStr(String cursorStr) {
		if (isBlank(cursorStr)) {
			this.searchCursor = null;
		} else {
			try {
				this.searchCursor = com.google.appengine.api.search.Cursor.newBuilder().build(
						cursorStr);
			} catch (IllegalArgumentException e) {
				LOG.warning("Cursor string couldn't be decoded as a valid search Cursor: "
						+ cursorStr);
				this.searchCursor = null;
			}
		}
		return this;
	}

	public void setTotalResults(int totalResults) {
		if (totalResults >= 0 && this.totalResults < 0) {
			this.totalResults = totalResults;
//			this.calcTotalPages();
		}
	}

	public void setCountLimit(int limit) {
		if (limit >= 100) {
			this.countLimit = limit;
		}
	}

	public boolean hasMore() {
		if (this.totalResults < 0 && null != this.results)
			return !(this.results.size() < this.limit);
		return !(this.totalResults < this.countLimit);
	}

	public static class PagerBuilder<T> {

		private int limit = 500;

	}

	/**
	 * "Reset" this {@code Pager}. What exactly that means is encapsulated within this class.
	 */
	public void reset() {
		this.cursor = null;
		this.searchCursor = null;
		this.results = null;
	}

}
