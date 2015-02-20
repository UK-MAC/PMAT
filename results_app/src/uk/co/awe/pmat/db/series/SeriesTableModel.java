package uk.co.awe.pmat.db.series;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineStyle;
import uk.co.awe.pmat.graph.PlottableLine;

/**
 * A {@code TableModel} used to display series lines and to allow updating of
 * line types.
 *
 * @author AWE Plc copyright 2013
 */
public class SeriesTableModel extends AbstractTableModel {

    private static final String COLOUR_COLUMN_NAME = "Colour";
    private static final String LINETYPE_COLUMN_NAME = "Line Type";
    private static final String LINEWIDTH_COLUMN_NAME = "Line Width";
    private static final int BASIC_COLUMNS_COUNT = 3;

    private final List<PlottableLine> rows = new ArrayList<>();
    private final List<String[]> seriesNames = new ArrayList<>();
    private final List<Series> columns = new ArrayList<>();

    /**
     * Returns the {@code PlottableLine} for the given row index.
     * 
     * @param rowIdx the row index.
     * @return the plottable line.
     */
    public PlottableLine getLine(int rowIdx) {
        return rows.get(rowIdx);
    }

    /**
     * Returns the series associated with the given column index.
     *
     * @param colIdx the column index.
     * @return the series.
     */
    public Series getSeries(int colIdx) {
        return columns.get(colIdx);
    }

    /**
     * Add a row to the table holding the given line.
     *
     * @param line the line add to the table.
     */
    public void addRow(PlottableLine line) {
        rows.add(line);
        seriesNames.add(line.getName().split("/"));
        fireTableRowsInserted(rows.size(), rows.size());
    }

    /**
     * Add a column to the table representing the given series.
     *
     * @param series the series to add a column for.
     */
    public void addColumn(Series series) {
        columns.add(series);
        fireTableStructureChanged();
    }

    /**
     * Remove the column with the given index from the table.
     *
     * @param colIdx the column index.
     */
    public void removeColumn(int colIdx) {
        columns.remove(colIdx);
        fireTableStructureChanged();
    }

    /**
     * Remove the row from the table with row index given.
     *
     * @param rowIdx the row index.
     */
    public void removeRow(int rowIdx) {
        rows.remove(rowIdx);
        seriesNames.remove(rowIdx);
        fireTableRowsDeleted(rows.size(), rows.size());
    }

    /**
     * Clear all the series and line data stored in this table model.
     */
    public void clear() {
        rows.clear();
        seriesNames.clear();
        columns.clear();
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size() + BASIC_COLUMNS_COUNT;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == columns.size()) { return COLOUR_COLUMN_NAME; }
        if (columnIndex == columns.size() + 1) { return LINETYPE_COLUMN_NAME; }
        if (columnIndex == columns.size() + 2) { return LINEWIDTH_COLUMN_NAME; }
        return columns.get(columnIndex).toString();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex >= columns.size());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == columns.size()) { return rows.get(rowIndex).getLineType().getColour(); }
        if (columnIndex == columns.size() + 1) { return rows.get(rowIndex).getLineType().getStyle(); }
        if (columnIndex == columns.size() + 2) { return rows.get(rowIndex).getLineType().getWidth(); }
        return seriesNames.get(rowIndex)[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PlottableLine line = rows.get(rowIndex);
        if (columnIndex == columns.size()) {
            line.getLineType().setColour((GraphColour) aValue);
        } else if (columnIndex == columns.size() + 1) {
            line.getLineType().setStyle((LineStyle) aValue);
        } else if (columnIndex == columns.size() + 2) {
            line.getLineType().setWidth((Integer) aValue);
        } else {
            throw new IllegalArgumentException("Trying to edit an non editable cell");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

}