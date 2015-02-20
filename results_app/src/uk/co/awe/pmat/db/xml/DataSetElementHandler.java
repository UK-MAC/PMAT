package uk.co.awe.pmat.db.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;

/**
 * The {@link ElementHandler} responsible for de-serialising the {@code DataSet}
 * nodes of a {@code Run} object.
 *
 * @author AWE Plc copyright 2013
 */
final class DataSetElementHandler implements ElementHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DataSetElementHandler.class);

    private final List<RunData> dataSets = new ArrayList<>();
    private final List<Value<?>> parameters = new ArrayList<>();
    private final List<Value<Double>> results = new ArrayList<>();

    @Override
    public void onStart(ElementPath path) {
        LOG.debug("Start of node: " + path.getCurrent().getName());

        parameters.clear();
        results.clear();

        path.addHandler("Parameters", new ParameterElementHandler());
        path.addHandler("Results", new ResultElementHandler());
    }

    @Override
    public void onEnd(ElementPath path) {
        LOG.debug("End of node: " + path.getCurrent().getName());
        dataSets.add(new RunData(parameters, results));
    }

    /**
     * Return the {@code DataSet}s created from parsing the XML.
     * 
     * @return the created {@code DataSet}s.
     */
    Collection<RunData> getDataSets() {
        return dataSets;
    }

    /**
     * A {@link ElementHandler} to handle the parameter nodes of the
     * {@code DataSet}.
     */
    private final class ParameterElementHandler implements ElementHandler {
        private final ValueElementHandler<Comparable<?>> handler = new ValueElementHandler<>();

        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());
            path.addHandler("Value", handler);
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
            parameters.addAll(handler.getValues());
        }
    }

    /**
     * A {@link ElementHandler} to handle the results nodes of the
     * {@code DataSet}.
     */
    private final class ResultElementHandler implements ElementHandler {
        private final ValueElementHandler<Double> handler = new ValueElementHandler<>();

        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());
            path.addHandler("Value", handler);
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
            results.addAll(handler.getValues());
        }
    }
}
