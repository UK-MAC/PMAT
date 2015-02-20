package uk.co.awe.pmat.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * The graph persistence class. This is an immutable class containing the
 * graph state, which can be saved to and loaded from the database or XML
 * files.
 *
 * @author AWE Plc copyright 2013
 */
public final class Graph implements XMLSerialisable {
    private final Axis xAxis;
    private final Axis yAxis;
    private final AxisValueType yAxisType;
    private final Rank rank;
    private final LineType lineType;
    private final Collection<AnalysisMethodData> analysisMethods = new ArrayList<>();
    private final Collection<Series> seriesList = new ArrayList<>();

    /**
     * Create a new {@code Graph}.
     *
     * @param xAxis the x axis.
     * @param yAxis the y axis.
     * @param yAxisType the y axis type.
     * @param rank the y axis rank.
     * @param lineType the line type.
     * @param analysisMethods the analysis methods applied to the graph.
     * @param seriesList the series used in the graph.
     */
    public Graph(Axis xAxis,
            Axis yAxis,
            AxisValueType yAxisType,
            Rank rank,
            LineType lineType,
            Collection<AnalysisMethodData> analysisMethods,
            Collection<Series> seriesList) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.yAxisType = yAxisType;
        this.rank = rank;
        this.lineType = lineType;
        this.analysisMethods.addAll(analysisMethods);
        this.seriesList.addAll(seriesList);
    }

    /**
     * Return the x axis for the graph.
     *
     * @return the graph x axis.
     */
    public Axis getxAxis() {
        return xAxis;
    }

    /**
     * Return the y axis for the graph.
     *
     * @return the graph y axis.
     */
    public Axis getyAxis() {
        return yAxis;
    }

    /**
     * Return the y axis type for the graph.
     *
     * @return the graph y axis type.
     */
    public AxisValueType getyAxisType() {
        return yAxisType;
    }

    /**
     * Return the analysis methods for the graph.
     *
     * @return the graph analysis methods.
     */
    public Iterable<AnalysisMethodData> getAnalysisMethods() {
        return Collections.unmodifiableCollection(analysisMethods);
    }

    /**
     * Return the series for the graph.
     *
     * @return the graph series.
     */
    public Iterable<Series> getSeries() {
        return Collections.unmodifiableCollection(seriesList);
    }

    /**
     * Return the line type for the graph.
     *
     * @return the graph line type.
     */
    public LineType getLineType() {
        return lineType;
    }

    /**
     * Update the state of this graph using the given {@code Axis} persistence
     * objects.
     *
     * @param newXAxis the x axis persistence object.
     * @param newYAxis the y axis persistence object.
     * @return the update graph.
     */
    public Graph updateAxes(Axis newXAxis, Axis newYAxis) {
        return new Graph(newXAxis, newYAxis, yAxisType, rank, lineType,
                analysisMethods, seriesList);
    }

    @Override
    public Element toXML() {
        Element node = new DOMElement(new QName(Graph.class.getSimpleName()));

        node.add(new DOMAttribute(new QName("yAxisType"), yAxisType.name()));
        node.add(new DOMAttribute(new QName("rank"), rank.name()));
        node.add(xAxis.toXML());
        node.add(yAxis.toXML());
        node.add(lineType.toXML());

        for (AnalysisMethodData method : analysisMethods) {
            node.add(method.toXML());
        }
        for (Series series : seriesList) {
            node.add(series.toXML());
        }

        return node;
    }

}
