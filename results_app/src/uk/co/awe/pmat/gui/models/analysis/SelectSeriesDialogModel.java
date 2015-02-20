package uk.co.awe.pmat.gui.models.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineStyle;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.db.series.SeriesTag;
import uk.co.awe.pmat.db.series.SeriesParam;
import uk.co.awe.pmat.db.series.SeriesResult;
import uk.co.awe.pmat.db.series.SeriesMetaData;
import uk.co.awe.pmat.db.series.SeriesTableModel;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.graph.GraphDataException;
import uk.co.awe.pmat.graph.PlottableLine;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.utils.ComboBoxColorRenderer;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.gui.utils.EditableNumberTableCell;
import uk.co.awe.pmat.gui.utils.TableColorRenderer;

/**
 * The model underpinning the {@code SelectSeriesDialog} which is used to update
 * the graph series.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SelectSeriesDialogModel {

	private static final Logger LOG = LoggerFactory
			.getLogger(SelectSeriesDialogModel.class);

	private final SeriesTableModel seriesTableModel;
	private final TableColumnModel seriesTableColumnModel;
	private final DefaultComboBoxModel<Series> seriesSelectionModel;

	private final AnalysisModel analysisModel;
	private final EventHub eventHub;
	private final GraphData graphData;

	private boolean stateChanged = false;

	/**
	 * Create a new SelectSeriesDialogModel.
	 * 
	 * @param analysisModel
	 *            the analysis model used to feed back changes.
	 * @param seriesTableColumnModel
	 *            the column model used by the table to set it's renderers.
	 * @param selectionModel
	 *            the selection model to use in the table.
	 * @param graphData
	 *            the data underlying the graph.
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 */
	public SelectSeriesDialogModel(
            final AnalysisModel analysisModel,
            final EventHub eventHub,
            final TableColumnModel seriesTableColumnModel,
            final ListSelectionModel selectionModel,
            final GraphData graphData)
            throws DatabaseException {

        this.analysisModel = analysisModel;
        this.eventHub = eventHub;
        this.seriesTableColumnModel = seriesTableColumnModel;
        this.graphData = graphData;

        seriesTableModel = new SeriesTableModel() {
            @Override
            public void setValueAt(Object value, int row, int column) {
                for (int i = 0; i < getRowCount(); i++) {
                    if (selectionModel.isSelectedIndex(i)) {
                        super.setValueAt(value, i, column);
                    }
                }
            }
        };

        final List<String> parameterNames = analysisModel.getParameterNames();
        final List<String> resultNames = analysisModel.getResultNames();

        final List<Series> series = new ArrayList<>();

        for (String paramName : parameterNames) {
            series.add(new SeriesParam(paramName));
        }
        for (String resultName : resultNames) {
            series.add(new SeriesResult(resultName));
        }
        for (MetaData.Type type : MetaData.Type.values()) {
            series.add(new SeriesMetaData(type));
        }
        series.add(new SeriesTag());

        Collections.sort(series, new Comparator<Series>() {
            @Override public int compare(Series o1, Series o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        seriesSelectionModel = new DefaultComboBoxModel<>(series);

        seriesTableModel.addTableModelListener(new TableModelListener() {
            @Override public void tableChanged(TableModelEvent e) {
                isLineInfoModified(e);
            }
        });

        updateSeriesTableModel();
    }

	/**
	 * Return the {@code ComboBoxModel} used to populate the series selection
	 * combo box.
	 * 
	 * @return The series combo box model
	 */
	public ComboBoxModel<Series> getSeriesSelectionModel() {
		return seriesSelectionModel;
	}

	/**
	 * Return the {@code TableModel} used to populate the series table.
	 * 
	 * @return The series table model
	 */
	public TableModel getSeriesTableModel() {
		return seriesTableModel;
	}

	/**
	 * Add the {@code Series} selected via the series combo box to the list of
	 * series and update the series table accordingly.
	 * 
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 * @throws GraphDataException
	 *             if a problem occurs adding the series.
	 * @throws DerivedDataException
	 *             if an error occurs creating the derived data fields.
	 */
	public void addSeries() throws DatabaseException, GraphDataException,
			DerivedDataException {

		Series selectedSeries = seriesSelectionModel.getSelectedElement();
		graphData.addSeries(selectedSeries);
		stateChanged = true;
	}

	/**
	 * Remove the series given by the column index from the set of series and
	 * update the series table accordingly.
	 * 
	 * @param colIdx
	 *            the column of the table that displays the series to remove.
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 * @throws GraphDataException
	 *             if an error occurs rebuilding the graph data after removing
	 *             the series.
	 * @throws DerivedDataException
	 *             if an error occurs creating the derived data fields.
	 */
	public void removeSeries(int colIdx) throws DatabaseException,
			GraphDataException, DerivedDataException {

		Series series = seriesTableModel.getSeries(colIdx);
		graphData.removeSeries(series);
		seriesTableModel.removeColumn(colIdx);
		stateChanged = true;
	}

	/**
	 * Remove a number of series group lines from the series table that
	 * correspond to the row numbers given.
	 * 
	 * @param rows
	 *            The row indices of the series group lines to remove
	 * @throws DatabaseException
	 *             if no connection to the database could be established
	 * @throws GraphDataException
	 *             if an error occurs rebuilding the graph data after removing
	 *             the series group lines
	 */
	public void removeSeriesLines(int[] rows) throws DatabaseException,
			GraphDataException {

		for (int row : rows) {
			PlottableLine seriesLine = seriesTableModel.getLine(row);
			graphData.removeSeriesLine(seriesLine);
			seriesTableModel.removeRow(row);
		}
		stateChanged = true;
	}

	/**
	 * Update the analysis model with any changes that may have occurred in the
	 * series data.
	 */
	public void updateAnalysis() {
		if (stateChanged) {
			eventHub.notifyEvent(EventHub.EventType.SERIES);
		}
	}

	/**
	 * Return the column index of the line width column.
	 * 
	 * @return The line width column index
	 */
	private int getLineWidthIndex() {
		return seriesTableModel.getColumnCount() - 1;
	}

	/**
	 * Return the column index of the line type column.
	 * 
	 * @return The line type column index
	 */
	public int getLineTypeIndex() {
		return seriesTableModel.getColumnCount() - 2;
	}

	/**
	 * Return the column index of the line colour column.
	 * 
	 * @return The line colour column index
	 */
	public int getLineColourIndex() {
		return seriesTableModel.getColumnCount() - 3;
	}

	/**
	 * Set the column renderers to the appropriate types for each column.
	 */
	private void setColumnRenderers() {

        TableColumn lineWidthColumn =
                seriesTableColumnModel.getColumn(getLineWidthIndex());
        EditableNumberTableCell numberSelectorCellEditor =
                new EditableNumberTableCell(1, 9);
        lineWidthColumn.setCellEditor(numberSelectorCellEditor);
        lineWidthColumn.setCellRenderer(numberSelectorCellEditor);

        // Set the line type column to display a drop down box of
        // LineStyle choices.
        final JComboBox<LineStyle> lineTypeOptions =
                new JComboBox<>(LineStyle.values());
        TableColumn lineTypeColumn =
                seriesTableColumnModel.getColumn(getLineTypeIndex());
        lineTypeColumn.setCellEditor(
                new DefaultCellEditor(lineTypeOptions));

        // Set the line colour column to display the current line colour
        // and a drop down box of different colours to choose from.
        final JComboBox<GraphColour> lineColourOptions =
                new JComboBox<>(GraphColour.values());
        lineColourOptions.setRenderer(new ComboBoxColorRenderer());
        TableColumn lineColourColumn =
                seriesTableColumnModel.getColumn(getLineColourIndex());
        lineColourColumn.setCellEditor(
                new DefaultCellEditor(lineColourOptions));
        lineColourColumn.setCellRenderer(new TableColorRenderer(true));

        seriesTableModel.fireTableChanged(new TableModelEvent(seriesTableModel));
    }

	/**
	 * Detect whether the line info has actually changed and set the {@code
	 * stateChanged} flag accordingly.
	 * 
	 * @param evt
	 *            the table model event.
	 */
	private void isLineInfoModified(TableModelEvent evt) {
		if (evt.getSource() != seriesSelectionModel
				&& evt.getType() == TableModelEvent.UPDATE
				&& evt.getFirstRow() != TableModelEvent.HEADER_ROW
				&& evt.getFirstRow() < seriesTableModel.getRowCount()) {
			stateChanged = true;
		}
	}

	/**
	 * Refresh the series table model.
	 */
	public void updateSeriesTableModel() {

		seriesTableModel.clear();

		for (Series series : graphData.getSeries()) {
			seriesTableModel.addColumn(series);
		}

		LOG.debug("Updating Table Model with "
				+ graphData.getPlottableLines().size() + " lines");
		for (PlottableLine line : graphData.getPlottableLines()) {
			seriesTableModel.addRow(line);
		}

		// Allow the table to update itself before setting the column renderers
		// so that we have the correct number of columns, etc.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setColumnRenderers();
			}
		});
	}

}
