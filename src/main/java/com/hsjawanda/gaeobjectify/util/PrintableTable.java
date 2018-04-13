/**
 *
 */
package com.hsjawanda.gaeobjectify.util;

import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.leftPad;
import static com.hsjawanda.gaeobjectify.repackaged.commonslang3.StringUtils.rightPad;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * @author Harshdeep S Jawanda <hsjawanda@gmail.com>
 *
 */
public class PrintableTable {

	private static final String NEWLINE = System.lineSeparator();

	private static final String NULL_STR = "null";

	private static final String SPACE = " ";

//	private List<Integer> colWidths;

	private List<String> currRow;

	private List<List<String>> rows = new ArrayList<>();

	private String separator = SPACE;

	private boolean useSerialNum = true;

	private PrintableTable() {
	}

	public static PrintableTable create() {
		PrintableTable table = new PrintableTable();
		return table;
	}

	public PrintableTable add(@Nullable Object obj) {
		if (null == this.currRow) {
			startRow();
		}
		this.currRow.add(null != obj ? obj.toString() : NULL_STR);
		return this;
	}

	public String getSeparator() {
		return this.separator;
	}

	public boolean isUseSerialNum() {
		return this.useSerialNum;
	}

	public synchronized String printable(@Nullable String prefix) {
		if (null != this.currRow) {
			this.rows.add(this.currRow);
		}
		int maxCols = maxColumns(), serialNumPad = serialNumPadding(), rowIdx, colIdx;
		int [] colWidths = new int[maxCols];
		for (rowIdx = 0; rowIdx < this.rows.size(); rowIdx++) {
			List<String> row = this.rows.get(rowIdx);
			for (colIdx = 0; colIdx < row.size(); colIdx++) {
				colWidths[colIdx] = Math.max(colWidths[colIdx], row.get(colIdx).length());
			}
		}
		StringBuilder sb = new StringBuilder(calcSpace(colWidths)).append(prefix).append(NEWLINE);
		for (rowIdx = 0; rowIdx < this.rows.size(); rowIdx++) {
			List<String> row = this.rows.get(rowIdx);
			int numCols = row.size();
			sb.append(NEWLINE);
			if (this.useSerialNum) {
				sb.append(leftPad(Integer.toString(rowIdx + 1), serialNumPad)).append('.').append(this.separator);
			}
			for (colIdx = 0; colIdx < numCols; colIdx++) {
				sb.append(rightPad(row.get(colIdx), colWidths[colIdx]));
				if (colIdx < numCols - 1) {
					sb.append(this.separator);
				}
			}
		}
		return sb.append(NEWLINE).toString();
	}

	public PrintableTable setSeparator(String separator) {
		if (null != separator) {
			this.separator = separator;
		}
		return this;
	}

	public PrintableTable setUseSerialNum(boolean useSerialNum) {
		this.useSerialNum = useSerialNum;
		return this;
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
		return rowWidth * numRows;
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

}
