package uk.co.awe.pmat.db.xml;

import java.util.Map;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.db.MetaData;

/**
 * Helper class which handles the business of converting a meta-data class into
 * XML.
 * 
 * @author AWE Plc copyright 2013
 */
public class MetaDataXMLSerialiser {

	/**
	 * Convert the given meta-data into XML.
	 * 
	 * @param metaData
	 *            the meta-data to convert.
	 * @return the XML node containing the representation of the meta-data.
	 */
	public static Element toXML(MetaData metaData) {
		final Element node = new DOMElement(MetaData.class.getSimpleName());

		node
				.add(new DOMAttribute(new QName("type"), metaData.getType()
						.name()));

		for (Map.Entry<String, Object> entry : metaData.getDataMap().entrySet()) {
			final Object data = entry.getValue();

			if (data instanceof MetaData) {
				final MetaData subNode = (MetaData) data;
				node.add(subNode.toXML());
			} else if (data != null) {
				final String name = entry.getKey();
				final Element dataNode = new DOMElement(new QName("Field"));
				dataNode.add(new DOMAttribute(new QName("name"), name));
				dataNode.add(new DOMAttribute(new QName("type"), data
						.getClass().getSimpleName()));
				dataNode.add(new DOMAttribute(new QName("value"), data
						.toString()));
				node.add(dataNode);
			}
		}

		return node;
	}

}
