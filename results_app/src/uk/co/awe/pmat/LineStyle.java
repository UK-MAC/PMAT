package uk.co.awe.pmat;

/**
 * An {@code Enum} representation of the style of the line.
 */
public enum LineStyle {

	/** POINTS */
	POINTS("Points"),
	/** LINE */
	LINE("Line"),
	/** LINE_AND_POINTS */
	LINE_AND_POINTS("Line and Points"),
	/** BARS */
	BARS("Bars"),
	/** DOTS */
	DOTS("Dots"),
	/** STEPS */
	STEPS("Steps"),
	/** IMPULSES */
	IMPULSES("Impulses");

	private String displayName;

	/**
	 * Create a new {@code LineStyle}.
	 * 
	 * @param displayName
	 *            the name to display.
	 */
	private LineStyle(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return this.displayName;
	}
}
