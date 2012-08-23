package org.sealinc.accurator.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * When {@link FlexCellFormatter#setRowSpan(int, int, int)} is used, it causes unexpected shifting for cells
 * on rows below spanned one, because {@link FlexTable#prepareCell(int, int)} is naive and does not take into
 * account fact of spanning (well, often <code>setRowSpan()</code> is invoked after adding cells, so may be we
 * should say that <code>setRowSpan()</code> is guilty).
 * <p>
 * See http://groups.google.com/group/Google-Web-Toolkit/browse_thread/thread/8a76929f78946c00 for discussion
 * about this problem.
 * <p>
 * In any case, this helper class tried to fix row span problem that that {@link FlexTable} can be used as
 * normal grid with arbitrary col/row spanning.
 * 
 * @author scheglov_ke
 */
public class FlexTableHelper {
	/**
	 * Fixes problem with {@link FlexCellFormatter#setRowSpan(int, int, int)}, see comment for
	 * {@link #FlexTableHelper}.
	 */
	public static void fixRowSpan(final FlexTable flexTable) {
		Set<Element> tdToRemove = new HashSet<Element>();
		{
			int rowCount = flexTable.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				int cellCount = flexTable.getCellCount(row);
				//System.out.println("\tcellCount: " + row + " " + cellCount);
				for (int cell = 0; cell < cellCount; cell++) {
					int colSpan = flexTable.getFlexCellFormatter().getColSpan(row, cell);
					int rowSpan = flexTable.getFlexCellFormatter().getRowSpan(row, cell);
					if (rowSpan != 1) {
						int column = getColumnOfCell(flexTable, row, cell);
						for (int row2 = row + 1; row2 < row + rowSpan; row2++) {
							int baseCell2 = getCellOfColumn(flexTable, row2, column);
							for (int cell2 = baseCell2; cell2 < baseCell2 + colSpan; cell2++) {
								if (cell2 != -1) {
									/*System.out.println("remove (row,cell,column): "
										+ row2
										+ " "
										+ cell2
										+ " "
										+ column);*/
									Element td = flexTable.getCellFormatter().getElement(row2, cell2);
									tdToRemove.add(td);
								}
							}
						}
					}
				}
			}
		}
		// remove TD elements
		for (Element td : tdToRemove) {
			Element tr = DOM.getParent(td);
			DOM.removeChild(tr, td);
		}
	}
	private static int getColumnOfCell(FlexTable flexTable, int row, int cell) {
		int column = 0;
		for (int _cell = 0; _cell < cell; _cell++) {
			int colSpan = getColSpan(flexTable, row, _cell);
			column += colSpan;
		}
		return column;
	}
	private static int getCellOfColumn(FlexTable flexTable, int row, int column) {
		int cellCount = flexTable.getCellCount(row);
		int currentColumn = 0;
		for (int cell = 0; cell < cellCount; cell++) {
			int colSpan = getColSpan(flexTable, row, cell);
			if (currentColumn == column) {
				return cell;
			}
			currentColumn += colSpan;
		}
		return -1;
	}
	private static int getColSpan(FlexTable flexTable, int row, int cell) {
		return flexTable.getFlexCellFormatter().getColSpan(row, cell);
	}
}
