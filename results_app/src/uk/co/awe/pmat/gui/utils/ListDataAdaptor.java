package uk.co.awe.pmat.gui.utils;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * An adaptor for the {@code ListDataListener} interface which allows an action
 * to be defined on the {@link #contentsChanged(ListDataEvent)} event by
 * overriding the {@link #selectionChanged(ListDataEvent)} method.
 * 
 * @author AWE Plc copyright 2013
 */
public class ListDataAdaptor implements ListDataListener {

	@Override
	public void intervalAdded(ListDataEvent e) {
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		if (isListSelection(e)) {
			selectionChanged(e);
		}
	}

	/**
	 * This method will be called whenever the list selections has
	 * <i>actually</i> changed.
	 * 
	 * @param evt
	 *            the selection event.
	 */
	protected void selectionChanged(ListDataEvent evt) {
	}

	/**
	 * Determine whether the {@code ListDataEvent} given is for a valid list
	 * selection.
	 * 
	 * @param e
	 *            the event.
	 * @return {@code true} if the event is for a list selection, {@code false}
	 *         otherwise.
	 */
	private boolean isListSelection(ListDataEvent e) {
		return e.getType() == ListDataEvent.CONTENTS_CHANGED
				&& e.getIndex0() == -1 && e.getIndex1() == -1;
	}

}
