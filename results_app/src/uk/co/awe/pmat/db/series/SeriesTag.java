package uk.co.awe.pmat.db.series;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;

/**
 * An implementation of the {@link Series} interface for series that are used to
 * split the graph data by run tag.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SeriesTag implements Series {

	@Override
	public SeriesType getType() {
		return SeriesType.TAG;
	}

	@Override
	public String getSubType() {
		return null;
	}

	@Override
	public String toString() {
		return "Tag";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SeriesTag)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public Element toXML() {
		Element seriesNode = new DOMElement(new QName(Series.class
				.getSimpleName()));
		seriesNode.add(new DOMAttribute(new QName("type"), getType().name()));
		return seriesNode;
	}

}
