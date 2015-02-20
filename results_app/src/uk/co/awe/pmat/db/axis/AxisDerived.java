package uk.co.awe.pmat.db.axis;

import uk.co.awe.pmat.deriveddata.DerivedData;

/**
 * An {@link Axis} which is used to display derived data.
 * 
 * @author AWE Plc copyright 2013
 */
class AxisDerived extends Axis {

	private final AxisName axisName;
	private final String name;
	private final DerivedData derivedData;

	/**
	 * Create a new {@code AxisDerived}.
	 * 
	 * @param axisName
	 *            the name of the axis.
	 * @param name
	 *            the derived data name.
	 * @param derivedData
	 *            the {@code DerivedData} object.
	 */
	AxisDerived(AxisName axisName, String name, DerivedData derivedData) {
		this.axisName = axisName;
		this.name = name;
		this.derivedData = derivedData;
	}

	@Override
	public AxisType getType() {
		return AxisType.DERIVED;
	}

	@Override
	public AxisName getAxisName() {
		return axisName;
	}

	@Override
	public Object getSubType() {
		return derivedData;
	}

	@Override
	public String displayName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
