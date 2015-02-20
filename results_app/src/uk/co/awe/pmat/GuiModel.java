package uk.co.awe.pmat;

import javax.swing.event.ChangeListener;

/**
 * An interface representing any class that drives a GUI view.
 * 
 * @author AWE Plc copyright 2013
 */
public interface GuiModel {

	/**
	 * Add a change listener to the model. This listener should be informed
	 * whenever anything changes the model in such a way that the view should be
	 * updated.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	void addChangeListener(ChangeListener listener);
}
