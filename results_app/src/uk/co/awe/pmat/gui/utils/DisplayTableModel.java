package uk.co.awe.pmat.gui.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

/**
 * A table model with a hidden field that allows the assignment of an ID to
 * each row.
 *
 * @author AWE Plc copyright 2013
 */
public final class DisplayTableModel extends DefaultTableModel {

    private static final Color DEFAULT_COLOR_LIGHT = Color.WHITE;
    private static final Color DEFAULT_COLOR_DARK = new Color(0xEE, 0xEE, 0xEE);
    private static final Color SUCCESS_COLOR = new Color(0xAA, 0xFF, 0xAA);
    private static final Color FAILED_COLOR = new Color(0xFF, 0x99, 0x99);

    /**
     * Different possible states of rows in the table, used to when the table
     * rows are operated on, e.g. saving objects into the database corresponding
     * to each row.
     */
    enum State { SUCCESS, FAILED, DEFAULT }
    
    private final Map<Integer, Long> rowIds;
    private final List<Class<?>> columnTypes;
    private final Set<String> editableColumnNames;
    private final Map<Long, State> states = new HashMap<>();
    private final Map<Long, Exception> exceptions = new HashMap<>();

    /**
     * Create a new instance of {@code DisplayTableModel}.
     *
     * @param columnNames the names of the columns.
     * @param columnTypes the types of the data in each column.
     * @param editableColumnNames the names of any column that should be
     * editable. Any column not specified here will be non editable.
     */
    public DisplayTableModel(List<String> columnNames,
            List<Class<?>> columnTypes,
            String... editableColumnNames) {

        super(columnNames.toArray(), 0);
        if (columnNames.size() != columnTypes.size()) {
            throw new IllegalArgumentException(
                    "Number of columnTypes must be equal to number of columnNames.");
        }
        rowIds = new HashMap<>();
        this.columnTypes = new ArrayList<>(columnTypes);

        this.editableColumnNames = new HashSet<>();
        this.editableColumnNames.addAll(Arrays.asList(editableColumnNames));
    }

    /**
     * Create a new instance of {@code DisplayTableModel}.
     *
     * @param columnNames the names of the columns.
     * @param columnTypes the types of the data in each column.
     * @param editableColumnNames the names of any column that should be
     * editable. Any column not specified here will be non editable.
     */
    public DisplayTableModel(List<String> columnNames,
            List<Class<?>> columnTypes,
            List<String> editableColumnNames) {
        this(columnNames, columnTypes, editableColumnNames.toArray(new String[editableColumnNames.size()]));
    }

    /**
     * Returns the entity id of a given row.
     *
     * @param rowNumber the row number.
     * @return the entity id.
     */
    public Long getId(Integer rowNumber) {
        return rowIds.get(rowNumber);
    }

    /**
     * Add a row to the table, along with its associated entity id.
     *
     * @param entityId the entity id.
     * @param rowData the data to be added to the row.
     */
    public void addRow(Long entityId, List<Object> rowData) {
        super.addRow(rowData.toArray());
        rowIds.put(getRowCount() - 1, entityId);
    }

    @Override
    public void removeRow(int row) {
        super.removeRow(row);
        final long rowId = rowIds.get(row);
        states.remove(rowId);
        rowIds.remove(row);
    }

    /**
     * Remove all the rows from the table.
     */
    public void removeAllRows() {
        while (getRowCount() > 0) {
            super.removeRow(0);
        }
        states.clear();
        rowIds.clear();
    }

    /**
     * Returns the type of the column as defined in the constructor.
     *
     * @param columnIndex the column index.
     * @return the column type.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes.get(columnIndex);
    }

    /**
     * Returns whether the cell is editable. A cell is always non editable
     * unless it's column is specified in the {@code editableColumnNames}
     * parameter of the constructor.
     *
     * @param row the row whose value is to be queried.
     * @param column the column whose value is to be queried.
     * @return {@code true} if the cell is editable, {@code false}
     * otherwise.
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return editableColumnNames.contains(getColumnName(column));
    }

    /**
     * Return the background colour that the given row should be displayed with.
     *
     * @param rowIdx the row index.
     * @return the background colour.
     */
    Color getRowColor(int rowIdx) {
        switch (getRowState(rowIdx)) {
            case SUCCESS:
                return SUCCESS_COLOR;
            case FAILED:
                return FAILED_COLOR;
            default:
                return (rowIdx % 2 == 0 ? DEFAULT_COLOR_LIGHT : DEFAULT_COLOR_DARK);
        }
    }

    /**
     * Return an exception stored against the given row.
     *
     * @param row the index of the row.
     * @return the exception stored against the row, or {@code null}.
     */
    Exception getRowException(int row) {
        final Long id = rowIds.get(row);
        return exceptions.get(id);
    }

    /**
     * Return information stored against the given row, useful to display tool
     * tip text.
     *
     * @param row the index of the row.
     * @return the row information.
     */
    String getRowInfo(int row) {
        return "";
    }

    /**
     * Return the {@link State} of a row in the table.
     * 
     * @param row the index of the row.
     * @return the state of the row.
     */
    State getRowState(int row) {
        final Long id = rowIds.get(row);
        State state = states.get(id) == null ? State.DEFAULT : states.get(id);
        return state;
    }

    /**
     * Set the row with the given ID to have the {@code State.SUCCESS} state.
     *
     * @param id the row ID.
     */
    public void setSuccessful(Long id) {
        states.put(id, State.SUCCESS);
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Set the row with the given ID to have the {@code State.FAILED} state and
     * store the causing exception against that row.
     *
     * @param id the row ID.
     * @param ex the exception which cause the failure.
     */
    public void setFailed(Long id, Exception ex) {
        exceptions.put(id, ex);
        states.put(id, State.FAILED);
        fireTableChanged(new TableModelEvent(this));
    }

}