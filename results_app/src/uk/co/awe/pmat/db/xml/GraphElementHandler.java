package uk.co.awe.pmat.db.xml;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineStyle;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.db.AnalysisMethodData;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.db.series.SeriesType;


/**
 * The {@link ElementHandler} responsible for deserialising the {@code Graph}
 * nodes of a {@code Analysis} object.
 *
 * @author AWE Plc copyright 2013
 */
final class GraphElementHandler implements ElementHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GraphElementHandler.class);

    private final List<Graph> axes;
    private final List<AnalysisMethodData> analysisMethods = new ArrayList<>();
    private final List<Series> seriesList = new ArrayList<>();
    private final Map<AxisName, Axis> axisMap = new EnumMap<>(AxisName.class);

    private AxisValueType yAxisType;
    private Rank rank;
    private LineType lineType;

    private final ElementHandler analysisMethodElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());

            final Element node = path.getCurrent();
            final String className = node.attributeValue(new QName("className"));
            final Integer rangeFrom = Integer.parseInt(node.attributeValue(new QName("rangeFrom")));
            final Integer rangeTo = Integer.parseInt(node.attributeValue(new QName("rangeTo")));
            analysisMethods.add(new AnalysisMethodData(className, rangeFrom, rangeTo));
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
        }
    };

    private final ElementHandler seriesElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());

            final Element node = path.getCurrent();
            final SeriesType type = SeriesType.valueOf(node.attributeValue(new QName("type")));
            final String subType = node.attributeValue(new QName("subType"));
            seriesList.add(type.newSeries(subType));
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
        }
    };

    private final ElementHandler lineTypeElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());

            final Element node = path.getCurrent();

            final GraphColour color = GraphColour.valueOf(node.attributeValue(new QName("color")));
            final LineStyle style = LineStyle.valueOf(node.attributeValue(new QName("style")));
            final Integer width = Integer.valueOf(node.attributeValue(new QName("width")));

            lineType = new LineType(style, color, width);
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
        }
    };

    private final ElementHandler axisElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());

            final Element node = path.getCurrent();
            final AxisType type = AxisType.valueOf(node.attributeValue(new QName("type")));
            final AxisName name = AxisName.valueOf(node.attributeValue(new QName("name")));
            final String subType = node.attributeValue(new QName("subType"));
            axisMap.put(name, type.newAxis(name, subType));
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
        }
    };

    /**
     * Create a new {@code GraphElementHandler}.
     *
     * @param dbMapping the mapping used to connect to the database.
     * @param axes the list of axes we are adding to.
     */
    GraphElementHandler(List<Graph> axes) {
        this.axes = axes;
    }

    @Override
    public void onStart(ElementPath path) {
        LOG.debug("Start of node: " + path.getCurrent().getName());

        final Element node = path.getCurrent();
        yAxisType = AxisValueType.valueOf(node.attributeValue(new QName("yAxisType")));
        rank = Rank.valueOf(node.attributeValue(new QName("rank")));

        path.addHandler(AnalysisMethodData.class.getSimpleName(), analysisMethodElementHandler);
        path.addHandler(LineType.class.getSimpleName(), lineTypeElementHandler);
        path.addHandler(Series.class.getSimpleName(), seriesElementHandler);
        path.addHandler(Axis.class.getSimpleName(), axisElementHandler);
    }

    @Override
    public void onEnd(ElementPath path) {
        LOG.debug("End of node: " + path.getCurrent().getName());

        Axis x1 = axisMap.get(AxisName.X1);
        Axis y1 = axisMap.get(AxisName.Y1);
        axes.add(new Graph(x1, y1, yAxisType, rank, lineType, analysisMethods, seriesList));
    }
}
