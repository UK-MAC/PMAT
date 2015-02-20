package uk.co.awe.pmat.db;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * The analysis method data persistence class. This is an immutable class
 * containing the analysis method state, which can be saved to and loaded from
 * the database or XML files.
 * 
 * @author AWE Plc copyright 2013
 */
public class AnalysisMethodData implements XMLSerialisable {

	private final String className;
	private final Integer rangeFrom;
	private final Integer rangeTo;

	/**
	 * Create a new {@code AnalysisMethodData}.
	 * 
	 * @param className
	 *            the analysis method class name.
	 * @param rangeFrom
	 *            the analysis method range from.
	 * @param rangeTo
	 *            the analysis method range to.
	 */
	public AnalysisMethodData(String className, Integer rangeFrom,
			Integer rangeTo) {
		this.className = className;
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
	}

	/**
	 * Return the class name of this analysis method.
	 * 
	 * @return the class name.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Return the range from of this analysis method.
	 * 
	 * @return the range from.
	 */
	public Integer getRangeFrom() {
		return rangeFrom;
	}

	/**
	 * Return the range to of this analysis method.
	 * 
	 * @return the range to.
	 */
	public Integer getRangeTo() {
		return rangeTo;
	}

	@Override
	public Element toXML() {
		final Element node = new DOMElement(new QName(AnalysisMethodData.class
				.getSimpleName()));

		node.add(new DOMAttribute(new QName("className"), className));
		node
				.add(new DOMAttribute(new QName("rangeFrom"), rangeFrom
						.toString()));
		node.add(new DOMAttribute(new QName("rangeTo"), rangeTo.toString()));

		return node;
	}

}
