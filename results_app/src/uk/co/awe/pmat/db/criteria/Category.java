package uk.co.awe.pmat.db.criteria;

/**
 * An {@code Enum} representation of the different categories of data
 * restrictions which can be created.
 * 
 * @author AWE Plc copyright 2013
 */
public enum Category {
	/** Restrict the data on application properties. */
	APPLICATION("Application"), COMPILER("Compiler"), MACHINE("Machine"), MPI(
			"MPI"), OPERATING_SYSTEM("Operating System"), PROCESSOR("Processor"),
	/** Restrict the data on result properties. */
	RESULT("Result"),
	/** Restrict the data on parameter properties. */
	PARAMETER("Parameter"),
	/** Restrict the data on run properties. */
	RUN("Run");

	/**
	 * Creates a new {@code Category}.
	 * 
	 * @param displayName
	 *            the name to display for this category.
	 */
	private Category(String displayName) {
		this.displayName = displayName;
	}

	private String displayName;

	@Override
	public String toString() {
		return displayName;
	}
}
