package uk.co.awe.pmat.db.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.BaseElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.Run;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public final class XMLSerialiser {

	private static final Logger LOG = LoggerFactory
			.getLogger(XMLSerialiser.class);
	private static final String ROOTNODE = "PMATExport";

	/**
	 * Interface for actions to be performed once an XML node has been fully
	 * read. For example, this might be to save that node into the database.
	 */
	public interface LoadedAction {
		void doAction(XMLSerialisable node);

		boolean isCancelled();
	}

	/**
	 * Serialise a collection of {@code XMLSerialisable} objects and write them
	 * to a g-zipped file.
	 * 
	 * @param file
	 *            the file to write to.
	 * @param objects
	 *            the objects to serialise.
	 * @throws IOException
	 *             if an error occurs during serialisation.
	 */
	public void serialise(File file, List<XMLSerialisable> objects) throws IOException {
        try (OutputStream out = new GZIPOutputStream(new FileOutputStream(file))) {
            serialise(out, objects);
        }
    }

	/**
	 * Serialise a collection of XMLSerialisable objects and write them to a
	 * given output stream.
	 * 
	 * @param out
	 *            the output stream to write to.
	 * @param xmlSerialisables
	 *            the objects to serialise.
	 * @throws IOException
	 *             if an error occurs during serialisation.
	 */
	public void serialise(OutputStream out, List<XMLSerialisable> xmlSerialisables) throws IOException {
        final List<XMLSerialisable> objects = new ArrayList<>(xmlSerialisables);
        SAXWriter saxWriter = null;

        try {
            saxWriter = new SAXWriter();

            Element root = new BaseElement(ROOTNODE);

            root.add(new DOMAttribute(new QName("version"), Constants.Application.VERSION));

            saxWriter.setContentHandler(new XMLWriter(out));

            saxWriter.getContentHandler().startDocument();
            saxWriter.writeOpen(root);

            // Remove the objects as we process to allow the garbage collector
            // to reclaim them.
            while (!objects.isEmpty()) {
                final XMLSerialisable obj = objects.get(0);
                LOG.debug("Exporting " + obj);
                saxWriter.write(obj.toXML());
                objects.remove(0);
            }

            saxWriter.writeClose(root);
            saxWriter.getContentHandler().endDocument();
        } catch (XMLSerialisationException | SAXException ex) {
            throw new IOException("Error writing XML.", ex);
        }
    }

	/**
	 * De-serialise an export file performing the given action after each
	 * {@link Run} loaded.
	 * 
	 * @param importFile
	 *            The file to import from
	 * @param loadRunData
	 *            a flag to tell whether we should loaded in all the data for
	 *            imported {@link Run}s or just the meta data. This is used to
	 *            speed up the load when we just want to see what is in the
	 *            file.
	 * @param loadedAction
	 *            an action to perform once we have fully loaded an {@link Run}
	 *            from the XML.
	 * @throws IOException
	 *             if a problem occurs during de-serialisation.
	 */
	public void deserialiseRuns(
            File importFile,
            boolean loadRunData,
            LoadedAction loadedAction)
            throws IOException {

        final SAXReader reader = new SAXReader();
        reader.setDefaultHandler(new ElementHandler() {
            @Override
            public void onStart(ElementPath arg0) {
                LOG.error("Unknown node: " + arg0.getCurrent().getName());
            }
            @Override public void onEnd(ElementPath arg0) { /* Do Nothing */ }
        });

        final RootHandler rootHandler = new RootHandler(Run.class, loadedAction, loadRunData);
        reader.addHandler("/" + ROOTNODE, rootHandler);

        try (InputStream in = new GZIPInputStream(new FileInputStream(importFile))) {
            reader.read(in);
        } catch (XMLSerialisationException | DocumentException ex) {
            throw new IOException("Error parsing XML.", ex);
        }
    }

	/**
	 * De-serialise an export file performing the given action after each
	 * {@link Analysis} loaded.
	 * 
	 * @param importFile
	 *            The file to import from
	 * @param loadedAction
	 *            an action to perform once we have fully loaded an
	 *            {@link Analysis} from the XML.
	 * @throws IOException
	 *             if a problem occurs during de-serialisation
	 */
	public void deserialiseAnalyses(
            File importFile,
            LoadedAction loadedAction)
            throws IOException {

        final SAXReader reader = new SAXReader();
        reader.setDefaultHandler(new ElementHandler() {
            @Override
            public void onStart(ElementPath arg0) {
                LOG.error("Unknown node: " + arg0.getCurrent().getName());
            }
            @Override public void onEnd(ElementPath arg0) { /* Do Nothing */ }
        });

        final RootHandler rootHandler = new RootHandler(Analysis.class, loadedAction, false);
        reader.addHandler("/" + ROOTNODE, rootHandler);

        try (InputStream in = new GZIPInputStream(new FileInputStream(importFile))) {
            reader.read(in);
        } catch (XMLSerialisationException | DocumentException ex) {
            throw new IOException("Error parsing XML.", ex);
        }
    }

	/**
	 * A helper class which handles the deserialisation of the root node of an
	 * XML document tree.
	 * 
	 * @author AWE Plc copyright 2013
	 * @param <T>
	 *            the type of the root node, i.e. what class of object will be
	 *            created by deserialising it.
	 */
	private static class RootHandler implements ElementHandler {

		private final ElementHandler elementHandler;
		private final Class<?> rootNodeClass;

		/**
		 * Create a new {@code RootHandler}.
		 * 
		 * @param dbMapping
		 *            the mapping used to communicate with the database.
		 * @param rootNodeClass
		 *            the class of the root node element.
		 */
		RootHandler(Class<?> rootNodeClass, LoadedAction loadedAction,
				boolean loadRunData) {
			this.rootNodeClass = rootNodeClass;
			if (Run.class.equals(rootNodeClass)) {
				elementHandler = new RunElementHandler(loadedAction,
						loadRunData);
			} else if (Analysis.class.equals(rootNodeClass)) {
				elementHandler = new AnalysisElementHandler(loadedAction);
			} else {
				throw new IllegalArgumentException("Unknown root node class "
						+ rootNodeClass);
			}
		}

		/**
		 * Start processing the XML tree at the root node, checking that the
		 * file version and the application version agree.
		 * 
		 * @param path
		 *            The XML path of the starting element
		 */
		@Override
		public void onStart(ElementPath path) {

			Element element = path.getCurrent();
			LOG.debug("Start of node: " + path.getCurrent().getName());

			String version = element.attributeValue(new QName("version"));
			String appVersion = Constants.Application.VERSION;

			if (!appVersion.equals(version)) {
				throw new XMLSerialisationException(getClass(),
						"Export version does not match application version.");
			}
			path.addHandler(rootNodeClass.getSimpleName(), elementHandler);
		}

		/**
		 * Finish processing the XML tree.
		 * 
		 * @param path
		 *            The XML path of the end element
		 */
		@Override
		public void onEnd(ElementPath path) {
			LOG.debug("End of node: " + path.getCurrent().getName());
		}

	}

}