package uk.co.awe.pmat.db.xml;

import java.io.Serializable;
import org.dom4j.Element;

/**
 * Interface to create the XML serialising structure of the data.
 * 
 * @author AWE Plc copyright 2013
 */
public interface XMLSerialisable extends Serializable {

	/**
	 * The string used in the XML to represent null objects.
	 */
	String NULL = "NULL";

	/**
	 * The DateFormat to be used in exporting and importing.
	 */
	String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Serialisation of object to XML.
	 * 
	 * @return an XML node representation of the object.
	 */
	Element toXML();

}
