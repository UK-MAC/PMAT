package uk.co.awe.pmat.graph;

/**
 * An {@code Enum} representation of the different plot types.
 * 
 * @author AWE Plc copyright 2013
 */
public enum PlotType {
	/**
	 * A normal (scatter) plot.
	 */
	NORMAL("Normal"),

	/**
	 * A bar chart.
	 */
	BAR("Bar Chart"),

	/**
	 * A Kiviat (spider web) plot.
	 */
	KIVIAT("Kiviat");

	private final String displayName;

	/**
	 * Create a new {@code PlotType}.
	 * 
	 * @param displayName
	 *            the name to use when displaying this plot type.
	 */
	private PlotType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
