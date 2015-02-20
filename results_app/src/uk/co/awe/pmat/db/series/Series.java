package uk.co.awe.pmat.db.series;

import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * An interface for a graph series. This is used to query that possible series
 * groups that can be obtained for a given series, and to return series group
 * objects for required series groups.
 * 
 * @author AWE Plc copyright 2013
 */
public interface Series extends XMLSerialisable {

	/**
	 * Returns the {@link SeriesType} of the series.
	 * 
	 * @return the series type.
	 */
	SeriesType getType();

	/**
	 * Returns the series sub type, which will be different for each
	 * {@link SeriesType}.
	 * 
	 * @return the series sub type.
	 */
	Object getSubType();
}
