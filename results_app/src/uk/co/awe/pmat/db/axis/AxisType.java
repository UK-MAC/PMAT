package uk.co.awe.pmat.db.axis;

import uk.co.awe.pmat.db.MetaData;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public enum AxisType {
	/** An axis to display derived data */
	DERIVED,
	/** An axis to display parameters */
	PARAMETER,
	/** An axis to display results */
	RESULT,
	/** An axis to display meta-data */
	META_DATA;

	/**
	 * Create a new {@code Axis} which the given name and subtype.
	 * 
	 * @param name
	 *            the name to set on the new {@code Axis}.
	 * @param subType
	 *            the sub-type to set on the new {@code Axis}.
	 * @return a new {@code Axis}.
	 */
	public Axis newAxis(AxisName name, String subType) {
		switch (this) {
		case PARAMETER:
			return new AxisParameter(name, subType);
		case RESULT:
			return new AxisResult(name, subType);
		case META_DATA:
			return new AxisMetaData(name, MetaData.Type.valueOf(subType));
		default:
			throw new IllegalStateException("Unknown AnalysisSeriesType "
					+ this);
		}
	}
}
