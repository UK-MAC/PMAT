package uk.co.awe.pmat.db.axis;

/**
 * An {@link Axis} which is used to display parameters.
 * 
 * @author AWE Plc copyright 2013
 */
class AxisParameter extends Axis {

	private final AxisName name;
	private final String paramName;

	/**
	 * Creates a new {@code AxisParameter}.
	 * 
	 * @param name
	 *            the axis name.
	 * @param paramName
	 *            the parameter name.
	 */
	AxisParameter(AxisName name, String paramName) {
		this.name = name;
		this.paramName = paramName;
	}

	@Override
	public AxisType getType() {
		return AxisType.PARAMETER;
	}

	@Override
	public AxisName getAxisName() {
		return name;
	}

	@Override
	public Object getSubType() {
		return paramName;
	}

	@Override
	public String displayName() {
		return paramName;
	}

	@Override
	public String toString() {
		return paramName;
	}

}
