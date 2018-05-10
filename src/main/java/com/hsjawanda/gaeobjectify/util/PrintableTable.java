/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.center;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.leftPad;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.repeat;
import static com.hsjawanda.gaeobjectify.repackaged.commons.lang3.StringUtils.rightPad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.Nullable;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class PrintableTable {

	private static final String NEWLINE = System.lineSeparator();

	private static final String NULL_STR = "null";

	private static final String SPACE = " ";

	private List<CellData> currRow;

	private boolean hasHeaderRow;

	private List<List<CellData>> rows = new ArrayList<>();

	private String separator = SPACE;

	private boolean useSerialNum = true;

	private PrintableTable() {
	}

	@Generated("SparkTools")
	private PrintableTable(Builder builder) {
		this.hasHeaderRow = builder.hasHeaderRow;
		this.separator = builder.separator;
		this.useSerialNum = builder.useSerialNum;
	}

	/**
	 * Creates builder to build {@link PrintableTable}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	public PrintableTable add(@Nullable Object obj) {
		return add(obj, null);
	}

	public PrintableTable add(@Nullable Object obj, @Nullable Justification just) {
		if (null == this.currRow) {
			startRow();
		}
		this.currRow.add(new CellData(obj, just));
		return this;
	}

	public synchronized String print(@Nullable String prefix) {
		if (null != this.currRow) {
			this.rows.add(this.currRow);
		}
		int maxCols = maxColumns(), serialNumPad = serialNumPadding(), rowIdx, colIdx, counter;
		int [] colWidths = new int[maxCols];
		for (rowIdx = 0; rowIdx < this.rows.size(); rowIdx++) {
			List<CellData> row = this.rows.get(rowIdx);
			for (colIdx = 0; colIdx < row.size(); colIdx++) {
				colWidths[colIdx] = Math.max(colWidths[colIdx], row.get(colIdx).length());
			}
		}
		int serialColWidth = this.useSerialNum ? serialNumPad + 1 : 0;
		int maxRowWidth = Arrays.stream(colWidths).sum() + ((maxCols - 1) * this.separator.length())
				+ serialColWidth + (this.useSerialNum ? this.separator.length() : 0);
		StringBuilder sb = new StringBuilder(calcSpace(colWidths)).append(prefix).append(NEWLINE);
		for (rowIdx = 0, counter = 0; rowIdx < this.rows.size(); rowIdx++) {
			List<CellData> row = this.rows.get(rowIdx);
			int numCols = row.size();
			sb.append(NEWLINE);
			if (this.useSerialNum) {
				if (this.hasHeaderRow && rowIdx == 0) {
					sb.append(repeat(SPACE, serialColWidth));
				} else {
					sb.append(leftPad(Integer.toString(++counter), serialNumPad)).append('.');
				}
				sb.append(this.separator);
			}
			for (colIdx = 0; colIdx < numCols; colIdx++) {
				sb.append(row.get(colIdx).formatted(colWidths[colIdx]));
				if (colIdx < numCols - 1) {
					sb.append(this.separator);
				}
			}
			if (rowIdx == 0 && this.hasHeaderRow) {
				sb.append(NEWLINE).append(repeat('-', maxRowWidth));
			}
		}
		return sb.append(NEWLINE).toString();
	}

	public synchronized PrintableTable startRow() {
		if (null != this.currRow) {
			this.rows.add(this.currRow);
		}
		this.currRow = new ArrayList<>();
		return this;
	}

	private int calcSpace(int [] colWidths) {
		int rowWidth = 0, numRows = this.rows.size();
		for (int i = 0; i < colWidths.length; i++) {
			rowWidth += colWidths[i];
		}
		rowWidth += (Math.max(0, colWidths.length - 1) * this.separator.length());
		return rowWidth * (this.hasHeaderRow ? numRows + 1 : numRows);
	}

	private int maxColumns() {
		int maxCols = 0;
		for (int rowIdx = 0; rowIdx < this.rows.size(); rowIdx++) {
			maxCols = Math.max(maxCols, this.rows.get(rowIdx).size());
		}
		return maxCols;
	}

	private int serialNumPadding() {
		return ((int) Math.floor(Math.log10(this.rows.size()))) + 1;
	}

	/**
	 * Builder to build {@link PrintableTable}.
	 */
	@Generated("SparkTools")
	public static final class Builder {

		private boolean hasHeaderRow;

		private String separator;

		private boolean useSerialNum;

		private Builder() {
		}

		public PrintableTable build() {
			return new PrintableTable(this);
		}

		public Builder hasHeaderRow(boolean hasHeaderRow) {
			this.hasHeaderRow = hasHeaderRow;
			return this;
		}

		public Builder separator(String separator) {
			this.separator = separator;
			return this;
		}

		public Builder useSerialNum(boolean useSerialNum) {
			this.useSerialNum = useSerialNum;
			return this;
		}
	}

	public enum Justification {
		CENTRE, LEFT, RIGHT;
	}

	private static class CellData {

		private String content;

		private Justification justification;

		private CellData(Object content) {
			this(content, null);
		}

		private CellData(Object content, Justification just) {
			this.content = null != content ? content.toString() : NULL_STR;
			this.justification = null != just ? just : Justification.LEFT;
		}

		public String formatted(int cellWidth) {
			switch (this.justification) {
			case RIGHT:
				return leftPad(this.content, cellWidth);
			case CENTRE:
				return center(this.content, cellWidth);
			case LEFT:
			default:
				return rightPad(this.content, cellWidth);
			}
		}

		public int length() {
			return this.content.length();
		}

	}

}
