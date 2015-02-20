package uk.co.awe.pmat.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.awe.pmat.db.series.SeriesGroup;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * A class to transfer the analysis query data from the database to the analysis
 * model. The structure of this class is a grid of data with one column for the
 * x values, and multiple columns for the y values; each with a unique name.
 * This allows operations to applied to the columns, i.e. if there are y columns
 * with names 'A' and 'B' then you can do an operation of the form 'A'+'B'.
 * 
 * There might also be columns with series values depending on whether the data
 * is being split by series. Each series column corresponds to a series.
 * 
 * @author AWE Plc copyright 2013
 */
public class DataGrid {

	/**
	 * The series group which is used when no series are defined, i.e. every
	 * value will be a member of this group.
	 */
	public static final RowSeriesGroup NULL_SERIES_GROUP = new RowSeriesGroup(
			new String[0]);

	/**
	 * An implementation of the {@link SeriesGroup} interface which encapsulates
	 * the series group defined by the series columns of a {@link Row}.
	 */
	public static final class RowSeriesGroup extends SeriesGroup {

		private final Object[] groups;
		private final String[] names;

		/**
		 * Create a new {@code RowSeriesGroup}.
		 * 
		 * @param groups
		 *            the {@code Row} series groups.
		 */
		public RowSeriesGroup(Object[] groups) {
			this.groups = groups.clone();
			names = new String[groups.length];
			for (int idx = 0; idx < groups.length; ++idx) {
				names[idx] = groups[idx] == null ? "NULL" : groups[idx]
						.toString();
			}
		}

		@Override
		public String getName() {
			return StringUtils.joinStrings(Arrays.asList(names), " / ");
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compareTo(SeriesGroup group) {
			if (group instanceof RowSeriesGroup) {
				final RowSeriesGroup other = (RowSeriesGroup) group;
				for (int idx = 0; idx < groups.length; ++idx) {
					final int cmp;
					if (groups[idx] instanceof Comparable
							&& groups[idx].getClass().equals(
									other.groups[idx].getClass())) {
						cmp = ((Comparable) groups[idx])
								.compareTo(other.groups[idx]);
					} else {
						cmp = names[idx].compareTo(other.names[idx]);
					}
					if (cmp != 0) {
						return cmp;
					}
				}
				return 0;
			} else {
				return super.compareTo(group);
			}
		}
	}

	/**
	 * A row of data in the data grid.
	 */
	public static final class Row {
		private final Value<?> xValue;
		private final Map<String, Value<?>> yValues;
		private final RowSeriesGroup series;

		/**
		 * Create a new {@code Row}.
		 * 
		 * @param xValue
		 *            the x value of the row.
		 * @param yValues
		 *            the named y values of the row.
		 * @param series
		 *            the series group of the row.
		 */
		public Row(Value<?> xValue, Map<String, Value<?>> yValues, RowSeriesGroup series) {
            this.xValue = xValue;
            this.yValues = new HashMap<>(yValues);
            this.series = series;
        }

		/**
		 * Return the series group of this row.
		 * 
		 * @return the row series group.
		 */
		public SeriesGroup getSeriesGroup() {
			return series;
		}

		/**
		 * Return the x value of this row.
		 * 
		 * @return the row x value.
		 */
		public Value<?> getxValue() {
			return xValue;
		}

		/**
		 * Return the y value of this row with the given name.
		 * 
		 * @param name
		 *            the name of the y value to retrieve.
		 * @return the row y value.
		 */
		public Value<?> getyValue(String name) {
			return yValues.get(name);
		}

		/**
		 * Add a y value to the row with the given name.
		 * 
		 * @param name
		 *            the name of the y value.
		 * @param value
		 *            the value of the y value.
		 */
		public void addyValue(String name, Value<?> value) {
			yValues.put(name, value);
		}

		/**
		 * Return the row data as a single array of objects which can be
		 * displayed in a table.
		 * 
		 * @return the row data.
		 */
		public Object[] asTableRow() {
			Object[] yVals = yValues.values().toArray();
			Object[] tableRow = new Object[1 + yVals.length
					+ series.names.length];

			tableRow[0] = xValue;
			System.arraycopy(yVals, 0, tableRow, 1, yVals.length);
			System.arraycopy(series.names, 0, tableRow, 1 + yVals.length,
					series.names.length);

			return tableRow;
		}
	}

	private final List<Row> rows;
	private final String xAxisName;
	private final String yAxisName;

	/**
	 * Create a new {@code DataGrid}.
	 * 
	 * @param xAxisName
	 *            the name of the x axis.
	 * @param yAxisName
	 *            the name of the y axis.
	 * @param rows
	 *            the data grid rows.
	 */
	public DataGrid(String xAxisName, String yAxisName, List<Row> rows) {
        this.rows = new ArrayList<>(rows);
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
    }

	/**
	 * Return the rows of the data grid.
	 * 
	 * @return the data grid rows.
	 */
	public List<Row> getRows() {
		return rows;
	}

	/**
	 * Return the size of the data grid, i.e. how many rows it has.
	 * 
	 * @return the data grid size.
	 */
	public int size() {
		return rows.size();
	}

	/**
	 * Return whether the data grid is empty, i.e. does it contain any rows.
	 * 
	 * @return {@code true} if the data grid is empty, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return rows.isEmpty();
	}

	/**
	 * Return the name of the x axis.
	 * 
	 * @return the x axis name.
	 */
	public String getxAxisName() {
		return xAxisName;
	}

	/**
	 * Return the name of the y axis.
	 * 
	 * @return the y axis name.
	 */
	public String getyAxisName() {
		return yAxisName;
	}

	/**
	 * Return the data grid as a 2 dimensional array of height {@link #size()},
	 * with each row of the array corresponding to a {@code Row} of the data
	 * grid.
	 * 
	 * @return the table data.
	 */
	public Object[][] asTableData() {
		Object[][] tableData = new Object[rows.size()][];

		int idx = 0;
		for (Row row : rows) {
			tableData[idx] = row.asTableRow();
			++idx;
		}

		return tableData;
	}

	/**
	 * Return the names of the columns of data which is returned via
	 * {@link #asTableData()}. When the data is not split by series then this
	 * will be just the x and y axis names, otherwise it will include the names
	 * of all the series columns as well.
	 * 
	 * @return the column names.
	 */
	public String[] getColumnNames() {
		if (rows.isEmpty()) {
			return new String[] { xAxisName, yAxisName };
		} else {
			RowSeriesGroup seriesGroup = rows.get(0).series;
			String[] columnNames = new String[2 + seriesGroup.names.length];
			columnNames[0] = xAxisName;
			columnNames[1] = yAxisName;
			System.arraycopy(seriesGroup.names, 0, columnNames, 2,
					seriesGroup.names.length);
			return columnNames;
		}
	}
}
