package uk.co.awe.pmat.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.db.series.SeriesGroup;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * An implementation of {@link PlottableLine} to hold a collection of
 * SeriesGroups which together form the restrictions on the graph data which
 * makes up a particular line on the graph.
 *
 * @author AWE Plc copyright 2013
 */
public class GraphLine implements PlottableLine {

    private final LineType lineType;
    private final List<SeriesGroup> seriesGroups = new ArrayList<>();

    /**
     * Create a new {@code GraphLine}.
     */
    public GraphLine() {
        super();
        this.lineType = new LineType();
    }

    @Override
    public LineType getLineType() {
        return lineType;
    }

    @Override
    public String getName() {
        return StringUtils.joinStrings(getNames(), " / ");
    }

    /**
     * Returns the names of the series groups that make up this line.
     * 
     * @return the series groups names.
     */
    public Collection<String> getNames() {
        final List<String> names = new ArrayList<>();
        for (SeriesGroup group : seriesGroups) {
            names.add(group.getName());
        }
        return names;
    }

    /**
     * Get the series group with the given index.
     *
     * @param columnIndex the index of the series group.
     * @return the series group.
     */
    public SeriesGroup getSeriesGroup(int columnIndex) {
        return seriesGroups.get(columnIndex);
    }

    /**
     * Add a series group to the line.
     *
     * @param seriesGroup the series group to add.
     */
    void addSeriesGroup(SeriesGroup seriesGroup) {
        seriesGroups.add(seriesGroup);
    }

    /**
     * Add all the series group in the given line to this line.
     *
     * @param line the line for whose series groups we are adding.
     */
    void addSeriesGroups(GraphLine line) {
        seriesGroups.addAll(line.seriesGroups);
    }

}
