package uk.co.awe.pmat.db.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Application;
import uk.co.awe.pmat.db.Compiler;
import uk.co.awe.pmat.db.Machine;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Mpi;
import uk.co.awe.pmat.db.OperatingSystem;
import uk.co.awe.pmat.db.Processor;

/**
 * The {@link ElementHandler} responsible for task of de-serialising the
 * {@code MetaData} nodes of a {@code Run} object.
 *
 * @author AWE Plc copyright 2013
 */
final class MetaDataElementHandler implements ElementHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MetaDataElementHandler.class);

    private final List<MetaData> metaData = new ArrayList<>();
    private final Map<String, Object> fields = new HashMap<>();

    private MetaData.Type type;

    @Override
    public void onStart(ElementPath path) {
        LOG.debug("Start of node: " + path.getCurrent().getName());
        final Element node = path.getCurrent();

        final String typeStr = node.attributeValue(new QName("type"));
        type = MetaData.Type.valueOf(typeStr);
        fields.clear();

        path.addHandler("Field", new FieldHandler());
    }

    @Override
    public void onEnd(ElementPath path) {
        LOG.debug("End of node: " + path.getCurrent().getName());
        switch (type) {
            case APPLICATION: metaData.add(new Application(fields)); break;
            case COMPILER: metaData.add(new Compiler(fields)); break;
            case MACHINE: metaData.add(new Machine(fields)); break;
            case MPI: metaData.add(new Mpi(fields)); break;
            case OPERATING_SYSTEM: metaData.add(new OperatingSystem(fields)); break;
            case PROCESSOR: metaData.add(new Processor(fields)); break;
            default:
                throw new IllegalStateException("Unknown meta data " + type);
        }
    }

    /**
     * Return the {@code MetaData} created from parsing the XML.
     *
     * @return the created {@code MetaData}.
     */
    List<MetaData> getMetaData() {
        return metaData;
    }

    /**
     * An {@code ElementHandler} to handle the individual fields of the
     * meta-data.
     */
    private final class FieldHandler implements ElementHandler {
        @Override
        public void onStart(ElementPath path) {
            final Element node = path.getCurrent();
            final String name = node.attributeValue(new QName("name"));
            final Object value;
            final String valueStr = node.attributeValue(new QName("value"));
            final String valueType = node.attributeValue(new QName("type"));
            switch (valueType) {
                case "String":
                    value = valueStr;
                    break;
                case "Integer":
                    value = Integer.parseInt(valueStr);
                    break;
                case "Boolean":
                    value = Boolean.valueOf(valueStr);
                    break;
                default:
                    throw new IllegalStateException("Unknown value type " + valueType);
            }
            fields.put(name, value);
        }

        @Override
        public void onEnd(ElementPath path) {
        }
    }

}