package uk.co.awe.pmat.db.axis;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * A class to represent an Axis in a plot.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class Axis implements Comparable<Axis>, XMLSerialisable {

	/**
	 * Create a new {@code Axis} to display the given {@code DerivedData}.
	 * 
	 * @param name
	 *            the name of the axis.
	 * @param label
	 *            the label to display with the axis.
	 * @param derivedData
	 *            the derived data to display on the axis.
	 * @return the new axis.
	 */
	public static Axis newDerivedAxis(AxisName name, String label,
			DerivedData derivedData) {
		return new AxisDerived(name, label, derivedData);
	}

	/**
	 * Returns the data type of the axis, see {@link AxisType}.
	 * 
	 * @return the axis type.
	 */
	public abstract AxisType getType();

	/**
	 * Returns the axis name, see {@link AxisName}.
	 * 
	 * @return the axis name.
	 */
	public abstract AxisName getAxisName();

	/**
	 * Returns the sub-type of the axis, which will depend on the axis type.
	 * 
	 * @return the axis sub-type.
	 */
	public abstract Object getSubType();

	/**
	 * Returns the name to display against this axis.
	 * 
	 * @return the axis display name.
	 */
	public abstract String displayName();

	@Override
	public int compareTo(Axis axis) {
		int res = getType().compareTo(axis.getType());
		if (res == 0) {
			res = getSubType().toString().compareTo(
					axis.getSubType().toString());
		}
		return res;
	}

	@Override
	public Element toXML() {
		final Element node = new DOMElement(new QName(Axis.class
				.getSimpleName()));

		node.addAttribute(new QName("type"), getType().name());
		node.addAttribute(new QName("name"), getAxisName().name());

		return node;
	}
}
