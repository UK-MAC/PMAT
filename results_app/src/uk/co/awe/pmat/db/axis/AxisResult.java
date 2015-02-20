package uk.co.awe.pmat.db.axis;

/**
 * An {@link Axis} which is used to display results.
 * 
 * @author AWE Plc copyright 2013
 */
class AxisResult extends Axis {

	private final AxisName name;
	private final String resultName;

	/**
	 * Creates a new {@code AxisResult}.
	 * 
	 * @param name
	 *            the axis name.
	 * @param resultName
	 *            the result name.
	 */
	AxisResult(AxisName name, String resultName) {
		this.name = name;
		this.resultName = resultName;
	}

	@Override
	public AxisType getType() {
		return AxisType.RESULT;
	}

	@Override
	public AxisName getAxisName() {
		return name;
	}

	@Override
	public Object getSubType() {
		return resultName;
	}

	@Override
	public String displayName() {
		return resultName;
	}

	@Override
	public String toString() {
		return resultName;
	}

}
