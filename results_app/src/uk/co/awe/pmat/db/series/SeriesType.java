package uk.co.awe.pmat.db.series;

import uk.co.awe.pmat.db.MetaData;

/**
 * An {@code Enum} representation of the different series types.
 * 
 * @author AWE Plc copyright 2013
 */
public enum SeriesType {

	/**
	 * Series that divide the graph data by parameter value.
	 */
	PARAMETER,

	/**
	 * Series that divide the graph data by result value.
	 */
	RESULT,

	/**
	 * Series that divide the graph data by system state.
	 */
	META_DATA,

	/**
	 * Series that divide the graph data by application name.
	 */
	TAG;

	/**
	 * Create a new {@code Series} of this series type.
	 * 
	 * @param subType
	 *            the sub type used when creating the series.
	 * @return the new series.
	 */
	public Series newSeries(String subType) {
		switch (this) {
		case PARAMETER:
			return new SeriesParam(subType);
		case RESULT:
			return new SeriesResult(subType);
		case META_DATA:
			return new SeriesMetaData(MetaData.Type.valueOf(subType));
		case TAG:
			return new SeriesTag();
		default:
			throw new IllegalStateException("Unknown AnalysisSeriesType "
					+ this);
		}
	}
}
