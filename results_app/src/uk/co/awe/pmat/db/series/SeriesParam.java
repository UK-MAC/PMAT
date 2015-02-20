package uk.co.awe.pmat.db.series;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * An implementation of the {@link Series} interface for series that are used to
 * split the graph data by parameter value.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SeriesParam implements Series {

	private final String paramName;

	/**
	 * Create a new {@code SeriesParam}.
	 * 
	 * @param paramName
	 *            the name of the parameter.
	 */
	public SeriesParam(String paramName) {
		this.paramName = paramName;
	}

	@Override
	public SeriesType getType() {
		return SeriesType.PARAMETER;
	}

	@Override
	public String getSubType() {
		return paramName;
	}

	@Override
	public String toString() {
		return "Parameter: " + StringUtils.capitaliseWords(paramName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SeriesParam)) {
			return false;
		}
		final SeriesParam other = (SeriesParam) obj;
		if ((paramName == null) ? (other.paramName != null) : !paramName
				.equals(other.paramName)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (paramName != null ? paramName.hashCode() : 0);
		return hash;
	}

	@Override
	public Element toXML() {
		Element seriesNode = new DOMElement(new QName(Series.class
				.getSimpleName()));
		seriesNode.add(new DOMAttribute(new QName("type"), getType().name()));
		seriesNode.add(new DOMAttribute(new QName("subType"), paramName));
		return seriesNode;
	}

}
