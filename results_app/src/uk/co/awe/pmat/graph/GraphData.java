package uk.co.awe.pmat.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineStyle;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;
import uk.co.awe.pmat.analysis.RestrictionCollection;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.AnalysisMethodData;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.db.series.SeriesGroup;
import uk.co.awe.pmat.utils.DefaultHashMap.Creator;
import uk.co.awe.pmat.utils.DefaultHashMap;

/**
 * A class to contain and transform the raw database data into axes and series
 * which can then be used to generate the required plots.
 *
 * @author AWE Plc copyright 2013
 */
public final class GraphData implements Plottable {

    private static final Logger LOG = LoggerFactory.getLogger(GraphData.class);
    private static final LineType DEFAULT_LINE_TYPE = new LineType(LineStyle.POINTS, GraphColour.GREEN, 1);

    private final Collection<Series> seriesList = new ArrayList<>();
    private final List<SeriesGroupLine> seriesGroupLines = new ArrayList<>();
    private final SeriesGroupLine simpleLine = new SeriesGroupLine(DEFAULT_LINE_TYPE.copy(), DataGrid.NULL_SERIES_GROUP);
    
    private final RestrictionCollection restrictionCollection;
    
    private Axis xAxis;
    private Axis yAxis;
    private AxisValueType yAxisType;
    private Rank rank;
    private Object[][] tableData = null;
    private boolean dirty = false;
    private DataGrid dataGrid;
    private String yAxisName;

    /**
     * Return a list of {@link DataPoint}s for the given line which can be
     * processed by analysis methods to produce analysis for that line.
     *
     * @param plottableLine the line to provide the data for.
     * @return the line data.
     */
    public synchronized List<DataPoint> getSeriesData(PlottableLine plottableLine) {
        final int idx = seriesGroupLines.indexOf(plottableLine);
        if (idx == -1) {
            throw new IllegalArgumentException("Unknown line " + plottableLine);
        }

        if (tableData != null) {
            final List<DataPoint> dataPoints
                    = new ArrayList<>(tableData.length);
            
            final Map<Object, Integer> xMap = new DefaultHashMap<>(new Creator<Integer>() {
                private int xIdx = 0;
                @Override
                public Integer create() {
                    return xIdx++;
                }
            });

            for (Object[] row : tableData) {
                final double x;
                if (row[0] instanceof Number) {
                    x = ((Number) row[0]).doubleValue();
                } else {
                    x = xMap.get(row[0]);
                }

                final double y = ((Number) row[idx + 1]).doubleValue();
                dataPoints.add(new DataPoint(x, y, 0.));
            }

            return dataPoints;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Return the graph data as a graph persistence object.
     *
     * @param analysisMethods the analysis methods to stored against this graph
     * data.
     * @return the graph persistence object.
     */
    synchronized Graph asGraph(List<AnalysisMethodData> analysisMethods) {
        return new Graph(xAxis, yAxis, yAxisType, rank,
                simpleLine.lineType, analysisMethods, seriesList);
    }

    /**
     * A helper class which is used to hold the {@code SeriesGroups}s used to
     * create each line in the graph data.
     */
    private final class SeriesGroupLine implements PlottableLine, Comparable<SeriesGroupLine> {
        private final LineType lineType;
        private final SeriesGroup group;

        /**
         * Create a new {@code SeriesGroupLine}.
         *
         * @param lineType the type of the line.
         * @param group the series group associated with this line.
         */
        SeriesGroupLine(LineType lineType, SeriesGroup group) {
            this.lineType = lineType;
            this.group = group;
        }

        @Override
        public LineType getLineType() {
            return lineType;
        }

        @Override
        public String getName() {
            final String name = group.getName();
            if (name.isEmpty()) {
                return getYAxis() != null ? getYAxis().displayName() : "";
            }
            return name;
        }

        @Override
        public int compareTo(SeriesGroupLine other) {
            return group.compareTo(other.group);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SeriesGroupLine)) {
                return false;
            }
            final SeriesGroupLine other = (SeriesGroupLine) obj;
            if (group != other.group && (group == null || !group.equals(other.group))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (group != null ? group.hashCode() : 0);
            return hash;
        }

    }

    /**
     * Create a new {@code GraphData} object.
     */
    public GraphData(RestrictionCollection restrictionCollection) {
        this.restrictionCollection = restrictionCollection;
        seriesGroupLines.add(simpleLine);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Returns the currently set series.
     *
     * @return the series.
     */
    public synchronized Collection<Series> getSeries() {
        return Collections.unmodifiableCollection(seriesList);
    }

    @Override
    public synchronized Axis getXAxis() {
        return xAxis;
    }

    @Override
    public synchronized Axis getYAxis() {
        return yAxis;
    }

    /**
     * Returns the y axis type currently set.
     *
     * @return the y axis type.
     */
    public synchronized AxisValueType getYAxisType() {
        return yAxisType;
    }

    /**
     * Returns the rank currently set.
     *
     * @return the rank.
     */
    public synchronized Rank getRank() {
        return rank;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Sets the x axis.
     *
     * @param xAxis the x axis to set.
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    public synchronized void setXAxis(Axis xAxis)
            throws DatabaseException, GraphDataException, DerivedDataException {
        this.xAxis = xAxis;
        dirty = true;
        updateGraphData();
    }

    /**
     * Sets the y axis.
     *
     * @param yAxis the y axis to set.
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    public synchronized void setYAxis(Axis yAxis)
            throws DatabaseException, GraphDataException, DerivedDataException {
        this.yAxis = yAxis;
        dirty = true;
        updateGraphData();
    }

    /**
     * Set the type of the y axis.
     *
     * @param yAxisType the y axis type.
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    public synchronized void setYAxisType(AxisValueType yAxisType)
            throws DatabaseException, GraphDataException, DerivedDataException {
        this.yAxisType = yAxisType;
        dirty = true;
        updateGraphData();
    }

    /**
     * Set the rank.
     *
     * @param rank the rank.
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    public synchronized void setRank(Rank rank)
            throws DatabaseException, GraphDataException, DerivedDataException {
        this.rank = rank;
        dirty = true;
        updateGraphData();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Plottable Overrides">
    @Override
    public synchronized Object[][] getTableData() {

        if (tableData != null) { return tableData.clone(); }

        if (dataGrid == null || dataGrid.isEmpty()) { return new Object[0][]; }

        // Map of x values to series column y values.
        // The y values are stored in a LinkedHashSet to perserve order but
        // remove any duplicates.
        Map<Value<?>, Map<SeriesGroup, Set<Double>>> dataMap
                = DefaultHashMap.mapOfDefaultMaps(new Creator<Set<Double>>() {
            @Override
            public Set<Double> create() {
                return new LinkedHashSet<>();
            }
        });

        /*
         * The rows that come back from the database have one x and y value per
         * row and a SeriesGroup that corresponds to the remaining columns. We
         * turn this into a map of x values -> series groups -> y values, so
         * we can build a table useful for plotting.
         */
        for (DataGrid.Row row : dataGrid.getRows()) {
            final Map<SeriesGroup, Set<Double>> seriesData = dataMap.get(row.getxValue());
            final SeriesGroup seriesGroup = row.getSeriesGroup();
            final Set<Double> yValues = seriesData.get(seriesGroup);
            
            yValues.add(yAxisType.extractValue(row.getyValue(yAxisName)));
        }

        final int numLines = seriesGroupLines.size();
        if (numLines < 1) {
            LOG.error("No series lines found.");
            return new Object[0][];
        }

        final List<Object[]> tableDataList
                = new ArrayList<>(dataGrid.size() / numLines);
        
        int rowIdx = 0;

        final List<Value<?>> orderedXValues = new ArrayList<>(dataMap.keySet());
        Collections.sort(orderedXValues);

        /*
         * Turn the map of values into a grid of data with one x value and a y
         * value for each series type per row. Some series may have more y
         * values than others for a given x value so we fill in the empty gaps
         * with null.
         */
        for (Value<?> xValue : orderedXValues) {

            int numSeriesRows = 0;
            int seriesIdx = 1;

            for (SeriesGroupLine seriesGroupLine : seriesGroupLines) {
                
                int yValueIdx = 0;
                final Set<Double> yValues = dataMap.get(xValue).get(seriesGroupLine.group);

                if (yValues != null) {
                    for (Double yValue : yValues) {
                        if (yValueIdx < numSeriesRows) {
                            tableDataList.get(rowIdx + yValueIdx)[seriesIdx] = yValue;
                        } else {
                            tableDataList.add(new Object[1 + seriesGroupLines.size()]);
                            tableDataList.get(rowIdx + yValueIdx)[0] = xValue.getValue();
                            tableDataList.get(rowIdx + yValueIdx)[seriesIdx] = yValue;
                            ++numSeriesRows;
                        }
                        ++yValueIdx;
                    }
                }
                ++seriesIdx;
            }
            
            rowIdx += numSeriesRows;
        }

        tableData = tableDataList.toArray(new Object[tableDataList.size()][]);
        return tableData.clone();
    }

    @Override
    public synchronized Collection<? extends PlottableLine> getPlottableLines() {
        return Collections.unmodifiableCollection(seriesGroupLines);
    }
    // </editor-fold>

    /**
     * Update the list of series group lines being used in the plot.
     */
    private synchronized void updateSeriesLines() {
        if (dataGrid == null) { return; }

        Set<SeriesGroupLine> seriesGroupLineSet = new HashSet<>();

        for (DataGrid.Row row : dataGrid.getRows()) {
            seriesGroupLineSet.add(new SeriesGroupLine(DEFAULT_LINE_TYPE.copy(), row.getSeriesGroup()));
        }

        seriesGroupLines.clear();
        seriesGroupLines.addAll(seriesGroupLineSet);
        Collections.sort(seriesGroupLines);
    }

    /**
     * Add a new {@code Series}, calculating the new {@code SeriesGroup}s as a
     * Cartesian product of the existing ones and the new ones of this
     * {@code Series}.
     *
     * @param series The Series to add
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    public synchronized void addSeries(Series series)
            throws DatabaseException, GraphDataException, DerivedDataException {

        seriesList.add(series);

        dirty = true;
        updateGraphData();

        updateSeriesLines();
        setLineColors();
    }

    /**
     * Remove a series.
     *
     * @param series the series to remove.
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    public synchronized void removeSeries(Series series)
            throws DatabaseException, GraphDataException, DerivedDataException {

        seriesList.remove(series);

        dirty = true;
        updateGraphData();

        updateSeriesLines();
        setLineColors();
    }

    /**
     * Remove a line from the graph and the SeriesGroup collection that is
     * associated with it.
     *
     * @param seriesLine The line on the graph to remove
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     */
    public synchronized void removeSeriesLine(PlottableLine seriesLine)
            throws DatabaseException, GraphDataException {

        /*
         * Remove the series associated with this line if there is one.
         */
        if (seriesLine instanceof SeriesGroupLine) {
            seriesGroupLines.remove((SeriesGroupLine) seriesLine);

            if (seriesGroupLines.isEmpty()) {
                /*
                 * If we have removed all series lines we need to tidy up stray
                 * series and go back to using the simple line.
                 */
                seriesList.clear();
                seriesGroupLines.add(simpleLine);
            }
        }

    }

    /**
     * Whether or not this graph data is in a plottable state.
     *
     * @return {@code true} if in a plottable state, {@code false} otherwise.
     */
    public synchronized boolean readyToPlot() {
        return (xAxis != null)
                && (yAxis != null)
                && (rank != null)
                && (yAxisType != null);
    }

    /**
     * Extract the required results from the SubRuns and put these into pots
     * depending on the xAxis selected and the seriesGroups.
     *
     * @throws DatabaseException if an error occurs communicating with the
     * database.
     * @throws GraphDataException if there is a problem updating the graph data.
     * @throws DerivedDataException if there is a problem creating the derived
     * graph data.
     */
    private synchronized void updateGraphData()
            throws DatabaseException, GraphDataException, DerivedDataException {

        if (!dirty || !readyToPlot()) { return; }

        dataGrid = DatabaseManager.getConnection().getDataGrid(xAxis, yAxis, rank,
                restrictionCollection.getRestrictions(), seriesList);
        if (yAxis.getType() == AxisType.DERIVED) {
            yAxisName = ((DerivedData) yAxis.getSubType()).evaluate(dataGrid);
        } else if (yAxis.getType() == AxisType.META_DATA) {
            yAxisName = ((MetaData.Type) yAxis.getSubType()).asFieldName();
        } else {
            yAxisName = (String) yAxis.getSubType();
        }

        LOG.debug("Found " + dataGrid.size() + " rows");
        tableData = null;
        dirty = false;
    }

    /**
     * Set the colour of the all the graph lines.
     */
    private synchronized void setLineColors() {
        int colorIdx = 0;
        GraphColour[] colours = GraphColour.values();
        for (PlottableLine line : seriesGroupLines) {
            line.getLineType().setColour(colours[colorIdx]);
            colorIdx = (colorIdx + 1) % colours.length;
        }
    }

    @Override
    public String toString() {
        final String xName = xAxis != null ? xAxis.displayName() : "";
        final String yName = yAxis != null ? yAxis.displayName() : "";
        final String rankStr = rank != null ? rank.toString() : "";
        return String.format("x: %s, y: %s (%s)", xName, yName, rankStr);
    }
}
