package uk.co.awe.pmat.db.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.ErrorType;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Value;

/**
 * The {@link ElementHandler} that is responsible for the task of deserialising
 * an XML node into a {@code Value} object.
 *
 * @author AWE Plc copyright 2013
 * @param <T> the type of the {@code Value<T>} object.
 */
final class ValueElementHandler <T extends Comparable<?>> implements ElementHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DataSetElementHandler.class);

    private final List<Value<T>> values = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void onStart(ElementPath path) {
        LOG.debug("Start of node: " + path.getCurrent().getName());
        final Element node = path.getCurrent();
        final String name = node.attributeValue(new QName("name"));
        final String type = node.attributeValue(new QName("type"));
        final String valueStr = node.attributeValue(new QName("value"));
        final String errorStr = node.attributeValue(new QName("error"));
        final String rankStr = node.attributeValue(new QName("rank"));
        final String errorTypeStr = node.attributeValue(new QName("errorType"));
        final String countStr = node.attributeValue(new QName("count"));
        final String pauseCountStr = node.attributeValue(new QName("pauseCount"));

        final T value;
        final T error;
        if (String.class.getSimpleName().equals(type)) {
            value = (T) valueStr;
            error = errorStr == null ? null : (T) errorStr;
        } else if (Integer.class.getSimpleName().equals(type)) {
            value = (T) Integer.valueOf(valueStr);
            error = errorStr == null ? null : (T) Integer.valueOf(errorStr);
        } else if (Double.class.getSimpleName().equals(type)) {
            value = (T) Double.valueOf(valueStr);
            error = errorStr == null ? null : (T) Double.valueOf(errorStr);
        } else {
            throw new IllegalStateException("Unknown type: " + type);
        }
        final ErrorType errorType = errorTypeStr == null  ? null : ErrorType.valueOf(errorTypeStr);
        final Rank rank           = valueStr == null      ? null : Rank.valueOf(rankStr);
        final Long count          = countStr == null      ? null : Long.valueOf(countStr);
        final Long pauseCount     = pauseCountStr == null ? null : Long.valueOf(pauseCountStr);

        values.add(new Value<>(name, value, error, errorType, rank, count, pauseCount));
    }

    @Override
    public void onEnd(ElementPath path) {
        LOG.debug("End of node: " + path.getCurrent().getName());
    }

    /**
     * Return the collection of {@code Value} objects that have been
     * deserialised.
     *
     * @return the {@code Value}s.
     */
    public Collection<Value<T>> getValues() {
        return values;
    }
}
