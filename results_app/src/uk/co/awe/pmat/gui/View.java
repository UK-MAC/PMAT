package uk.co.awe.pmat.gui;

/**
 * An {@code Enum} representation of the different view that can be opened via
 * the main panel.
 * 
 * @author AWE Plc copyright 2013
 */
enum View {
	/**
	 * A view to display system configuration.
	 */
	SYSTEM_CONFIG("System Configuration"),

	/**
	 * A view to allow results to imported into the database.
	 */
	IMPORT_RESULTS("Import Results"),

	/**
	 * A view to allow analyses to be imported into the database.
	 */
	IMPORT_ANALYSES("Import Analyses"),

	/**
	 * A view to allow stored results to view, deleted or exported.
	 */
	EXPORT_RESULTS("Export Results"),

	/**
	 * A view to allow results to be loaded from PMTM files, SkaMPI files, etc.
	 */
	ADD_RESULTS("Add Results");

	private String displayName;

	/**
	 * Creates a new {@code View}.
	 * 
	 * @param displayName
	 *            the name to display in the GUI for this view type.
	 */
	View(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the name to display for this view type.
	 * 
	 * @return the display name.
	 */
	public String getDisplayName() {
		return displayName;
	}
}
