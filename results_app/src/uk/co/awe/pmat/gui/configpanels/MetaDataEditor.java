package uk.co.awe.pmat.gui.configpanels;

import uk.co.awe.pmat.db.MetaData;

/**
 * An interface which represents an editor panel that can be used to display and
 * edit meta-data.
 * 
 * @author AWE Plc copyright 2013
 */
public interface MetaDataEditor {

	/**
	 * Display the given meta-data.
	 * 
	 * @param metaData
	 *            the meta-data to display.
	 */
	void displayData(final MetaData metaData);

	/**
	 * Set whether we are in edit or add mode. This determines whether or not we
	 * are displaying existing meta-data, and specifies the behaviour when we
	 * commit the changes of the editing.
	 * 
	 * @param editingFlag
	 *            {@code true} if the editor should be in edit mode, {@code
	 *            false} if it should be in add mode.
	 */
	void setEditing(boolean editingFlag);
}
