package uk.co.awe.pmat.db.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.xml.XMLSerialiser.LoadedAction;

/**
 * The {@link ElementHandler} that is responsible for the task of deserialising
 * an XML node into a {@code Run} object.
 * 
 * @author AWE Plc copyright 2013
 */
final class RunElementHandler implements ElementHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(RunElementHandler.class);

	private final MetaDataElementHandler metaDataElementHandler = new MetaDataElementHandler();
	private final DataSetElementHandler dataSetElementHandler = new DataSetElementHandler();
	private final LoadedAction loadedAction;
	private final boolean loadRunData;

	private String runId;
	private Date runDate;
	private String user;
	private boolean isPrivate;
	private String file;
	private String tag;
	private List<String> flags;

	private class FlagElementHandler implements ElementHandler {
		@Override
		public void onStart(ElementPath path) {
			LOG.debug("Start of node: " + path.getCurrent().getName());
			final Element node = path.getCurrent();
			final String flag = node.attributeValue(new QName("value"));
			flags.add(flag);
		}

		@Override
		public void onEnd(ElementPath path) {
			LOG.debug("Start of node: " + path.getCurrent().getName());
		}
	}

	/**
	 * Create a new {@code RunElementHandler}.
	 * 
	 * @param dbMapping
	 *            the mapping used to communicate with the database.
	 */
	RunElementHandler(LoadedAction loadedAction, boolean loadRunData) {
		if (loadedAction == null) {
			throw new NullPointerException();
		}
		this.loadedAction = loadedAction;
		this.loadRunData = loadRunData;
	}

	@Override
	public void onStart(ElementPath path) {
		LOG.debug("Start of node: " + path.getCurrent().getName());
		final Element node = path.getCurrent();

		runId = node.attributeValue(new QName("runId"));
		try {
			DateFormat dateFormat = new SimpleDateFormat(
					XMLSerialisable.DATE_FORMAT_STRING, Locale.UK);
			runDate = dateFormat.parse(node
					.attributeValue(new QName("runDate")));
		} catch (ParseException ex) {
			LOG.debug("Error parsing date: "
					+ node.attributeValue(new QName("runDate")), ex);
			runDate = new Date();
		}
		user = node.attributeValue(new QName("user"));
		isPrivate = false;

		path.addHandler("MetaData", metaDataElementHandler);
		path.addHandler("Flag", new FlagElementHandler());

		if (loadRunData) {
			path.addHandler("RunData", dataSetElementHandler);
		}
	}

	@Override
	public void onEnd(ElementPath path) {
		LOG.debug("End of node: " + path.getCurrent().getName());
		final List<MetaData> metaData = metaDataElementHandler.getMetaData();
		final Run newData = new Run(runId, runDate, new Date(), metaData, user,
				isPrivate, file, tag);
		newData.addAllDataSets(dataSetElementHandler.getDataSets());
		if (flags != null) {
			newData.addFlags(flags);
		}

		if (loadedAction.isCancelled()) {
			// Kill the SAX parsing.
			throw new RuntimeException();
		}
		loadedAction.doAction(newData);
	}

}
