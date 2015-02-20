package uk.co.awe.pmat.db.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DerivedData;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.xml.XMLSerialiser.LoadedAction;
import uk.co.awe.pmat.utils.Pair;

/**
 * The {@link ElementHandler} responsible for task of de-serialising the
 * {@code Analysis} root nodes of an PMAT analysis export file.
 *
 * @author AWE Plc copyright 2013
 */
final class AnalysisElementHandler implements ElementHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisElementHandler.class);

    private final List<Graph> axes = new ArrayList<>();
    private final List<DerivedData> derivedData = new ArrayList<>();
    private final List<Restriction> restrictions = new ArrayList<>();
    private final List<Pair<String, String>> renames = new ArrayList<>();
    private final LoadedAction loadedAction;
    
    private String creator;
    private Date date;
    private String notes;
    private boolean isPrivate;

    public AnalysisElementHandler(LoadedAction loadedAction) {
        this.loadedAction = loadedAction;
    }

    private final ElementHandler notesElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
            notes = path.getCurrent().getTextTrim();
        }
    };

    private final ElementHandler derivedDataElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());

            final Element node = path.getCurrent();
            final String name = node.attributeValue(new QName("name"));
            final String equation = node.attributeValue(new QName("equation"));
            derivedData.add(new DerivedData(name, equation));
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
        }
    };

    private final ElementHandler renameElementHandler = new ElementHandler() {
        @Override
        public void onStart(ElementPath path) {
            LOG.debug("Start of node: " + path.getCurrent().getName());

            final Element node = path.getCurrent();
            final String from = node.attributeValue(new QName("from"));
            final String to = node.attributeValue(new QName("to"));
            renames.add(new Pair<>(from, to));
        }

        @Override
        public void onEnd(ElementPath path) {
            LOG.debug("End of node: " + path.getCurrent().getName());
        }
    };

    @Override
    public void onStart(ElementPath path) {
        LOG.debug("Start of node: " + path.getCurrent().getName());
        final Element node = path.getCurrent();

        creator = node.attributeValue(new QName("creator"));
        try {
            DateFormat df = new SimpleDateFormat(XMLSerialisable.DATE_FORMAT_STRING);
            date = df.parse(node.attributeValue(new QName("date")));
        } catch (ParseException ex) {
            LOG.debug("Error parsing date: " + node.attributeValue(new QName("date")), ex);
            date = new Date();
        }
        isPrivate = Boolean.parseBoolean(node.attributeValue(new QName("isPrivate")));

        final ElementHandler graphElementHandler = new GraphElementHandler(axes);
        final ElementHandler restrictionElementHandler = new RestrictionElementHandler(restrictions);

        path.addHandler("Notes", notesElementHandler);
        path.addHandler(Graph.class.getSimpleName(), graphElementHandler);
        path.addHandler(DerivedData.class.getSimpleName(), derivedDataElementHandler);
        path.addHandler(Restriction.class.getSimpleName(), restrictionElementHandler);
        path.addHandler("Rename", renameElementHandler);
    }

    @Override
    public void onEnd(ElementPath path) {
        LOG.debug("End of node: " + path.getCurrent().getName());
        final Analysis newAnalysis = new Analysis(creator, date, notes,
                isPrivate, axes, derivedData, restrictions, renames);
        loadedAction.doAction(newAnalysis);
    }

}
