package uk.co.awe.pmat.db;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * The derived data persistence class. This is an immutable class containing the
 * derived data state, which can be saved to and loaded from the database or XML
 * files.
 * 
 * @author AWE Plc copyright 2013
 */
public class DerivedData implements XMLSerialisable {

	private final String name;
	private final String equation;

	/**
	 * Create a new {@code DerivedData}.
	 * 
	 * @param name
	 *            the name of the derived data.
	 * @param equation
	 *            the derived data equation.
	 */
	public DerivedData(String name, String equation) {
		this.name = name;
		this.equation = equation;
	}

	/**
	 * Return the equation of the derived data.
	 * 
	 * @return the derived data equation.
	 */
	public String getEquation() {
		return equation;
	}

	/**
	 * Return the name of the derived data.
	 * 
	 * @return the derived data name.
	 */
	public String getName() {
		return name;
	}

	@Override
	public Element toXML() {
		Element node = new DOMElement(new QName(DerivedData.class
				.getSimpleName()));
		node.add(new DOMAttribute(new QName("name"), name));
		node.add(new DOMAttribute(new QName("equation"), equation));
		return node;
	}

}
