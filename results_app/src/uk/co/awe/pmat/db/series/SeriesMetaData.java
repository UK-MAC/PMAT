package uk.co.awe.pmat.db.series;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.MetaData;

/**
 * An implementation of the {@link Series} interface for series that are used to
 * split the graph data by system state.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SeriesMetaData implements Series {

	private static final Logger LOG = LoggerFactory
			.getLogger(SeriesMetaData.class);

	private final MetaData.Type subType;

	/**
	 * Create a new {@code SeriesSystemState}.
	 * 
	 * @param subType
	 *            the type of the system state.
	 */
	public SeriesMetaData(MetaData.Type subType) {
		this.subType = subType;
	}

	@Override
	public SeriesType getType() {
		return SeriesType.META_DATA;
	}

	@Override
	public Object getSubType() {
		return subType;
	}

	@Override
	public String toString() {
		return "System State: " + subType.displayName();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SeriesMetaData)) {
			return false;
		}
		final SeriesMetaData other = (SeriesMetaData) obj;
		if (subType != other.subType) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + (subType != null ? subType.hashCode() : 0);
		return hash;
	}

	@Override
	public Element toXML() {
		Element seriesNode = new DOMElement(new QName(Series.class
				.getSimpleName()));
		seriesNode.add(new DOMAttribute(new QName("type"), getType().name()));
		seriesNode.add(new DOMAttribute(new QName("subType"), subType.name()));
		return seriesNode;
	}

}
