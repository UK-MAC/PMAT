package uk.co.awe.pmat.db.axis;

/**
 * An {@code Enum} representation of the different axes that a Plotter can plot
 * on.
 * 
 * @author AWE Plc copyright 2013
 */
public enum AxisName {
	/** The first X axis */
	X1("x"),
	/** The second X axis */
	X2("x2"),
	/** The first Y axis */
	Y1("y"),
	/** The second Y axis */
	Y2("y2");

	private final String displayName;

	/**
	 * Creates a new {@code AxisName}.
	 * 
	 * @param displayName
	 *            the display name.
	 */
	private AxisName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
