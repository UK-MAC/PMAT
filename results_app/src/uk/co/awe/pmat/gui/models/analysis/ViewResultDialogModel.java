package uk.co.awe.pmat.gui.models.analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.utils.Pair;

/**
 * The model driving the view results dialog.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ViewResultDialogModel {

	private final Run run;

	/**
	 * Create a new {@code ViewResultDialogModel}.
	 * 
	 * @param run
	 *            the run to view.
	 */
	public ViewResultDialogModel(Run run) {
		this.run = run;
	}

	/**
	 * Extract the run information, e.g. run date, user, application.
	 * 
	 * @return pairs for information name and values.
	 */
	public List<Pair<String, String>> getRunInfo() throws DatabaseException {

        run.loadFlags();
        
        final List<Pair<String, String>> info = new ArrayList<>();
        
        for (Run.Column column : Run.VIEW_FIELDS) {
            final Object data = run.getColumnData(column);
            final String dataString;
            if (data instanceof MetaData) {
                dataString = ((MetaData) data).displayName();
            } else if (data instanceof Date) {
                final DateFormat df = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
                dataString = df.format((Date) data);
            } else {
                dataString = data == null ? "" : data.toString();
            }
            info.add(new Pair<>(column.getColumnName(), dataString));
        }

        return info;
    }

	/**
	 * Return a table model containing all the results stored in the run.
	 * 
	 * @return the result table model.
	 * @throws DatabaseException
	 *             if an error occurs retrieving the results from the database.
	 */
	public DefaultTableModel getResultDataTableModel() throws DatabaseException {
        final String[] columnNames = new String[]{"DataSet", "Name", "Rank", "Value"};
        final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        final List<Comparable[]> rows = new ArrayList<>();

        run.loadDataSets();
        final Collection<RunData> dataSets = run.getDataSets();
        
        int dataSetIdx = 0;
        for (RunData dataSet : dataSets) {
            for (Value<?> param : dataSet.getParameters()) {
                rows.add(new Comparable<?>[] {dataSetIdx, param.getName(), param.getRank(), param.getValue()});
            }
            for (Value<Double> result : dataSet.getResults()) {
                rows.add(new Comparable<?>[] {dataSetIdx, result.getName(), result.getRank(), result.getValue()});
            }
            ++dataSetIdx;
        }

        Collections.sort(rows, new java.util.Comparator<Comparable[]>() {
            @Override
            public int compare(Comparable[] o1, Comparable[] o2) {
                for (int idx = 0; idx < o1.length; ++idx) {
                    @SuppressWarnings("unchecked")
                    int cmp = o1[idx].compareTo(o2[idx]);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return 0;
            }
        });

        for (Comparable[] row : rows) {
            tableModel.addRow(row);
        }

        return tableModel;
    }
}
