package uk.co.awe.pmat.db.criteria;

/**
 * A {@code Enum} representation of the different ways to apply restrictions on
 * the data.
 * 
 * @author AWE Plc copyright 2013
 */
public enum Comparator {
	/** Restrict to equal values. */
	EQ("=="),
	/** Restrict to not equal values. */
	NE("!="),
	/** Restrict to values greater than. */
	GT(">"),
	/** Restrict to values greater than or equal to. */
	GE(">="),
	/** Restrict to values less than. */
	LT("<"),
	/** Restrict to values less than or equal to. */
	LE("<=");

	/**
	 * Creates a new {@code Comparator}.
	 * 
	 * @param displayName
	 *            the display name of the comparator.
	 */
	private Comparator(String displayName) {
		this.displayName = displayName;
	}

	private final String displayName;

	@Override
	public String toString() {
		return displayName;
	}
}
