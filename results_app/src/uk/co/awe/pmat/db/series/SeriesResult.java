package uk.co.awe.pmat.db.series;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * An implementation of the {@link Series} interface for series that are used to
 * split the graph data by result value.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SeriesResult implements Series {

	private final String resultName;

	/**
	 * Create a new {@code SeriesResult}.
	 * 
	 * @param resultName
	 *            the name of the result.
	 */
	public SeriesResult(String resultName) {
		this.resultName = resultName;
	}

	@Override
	public SeriesType getType() {
		return SeriesType.RESULT;
	}

	@Override
	public String getSubType() {
		return resultName;
	}

	@Override
	public String toString() {
		return "Result: " + StringUtils.capitaliseWords(resultName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SeriesResult)) {
			return false;
		}
		final SeriesResult other = (SeriesResult) obj;
		if ((resultName == null) ? (other.resultName != null) : !resultName
				.equals(other.resultName)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (resultName != null ? resultName.hashCode() : 0);
		return hash;
	}

	@Override
	public Element toXML() {
		Element seriesNode = new DOMElement(new QName(Series.class
				.getSimpleName()));
		seriesNode.add(new DOMAttribute(new QName("type"), getType().name()));
		seriesNode.add(new DOMAttribute(new QName("subType"), resultName));
		return seriesNode;
	}

}
