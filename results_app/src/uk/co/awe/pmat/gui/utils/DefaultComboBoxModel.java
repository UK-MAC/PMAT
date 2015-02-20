package uk.co.awe.pmat.gui.utils;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ComboBoxModel;

/**
 * A typed version of {@code Swing}s {@code DefaultComboBoxModel} which also
 * contains some additional methods useful to its use in this application.
 * 
 * @author AWE Plc copyright 2013
 * @param <T>
 *            The type of the entities that this combo box model will hold
 */
public final class DefaultComboBoxModel<T> extends AbstractListModel<T>
		implements ComboBoxModel<T> {

	private static final Action NULL_ACTION = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	};

	private final ActionEvent selectionEvent = new ActionEvent(this,
			ActionEvent.ACTION_PERFORMED, "selected");
	private final List<T> elements = new ArrayList<T>();

	private final String header;

	private Action selectionAction;
	private T selectedItem = null;
	private boolean showHeader = true;
	private boolean headerSelected = false;

	/**
	 * Create an empty combo box model.
	 */
	@SuppressWarnings("unchecked")
	public DefaultComboBoxModel() {
		this(null, Collections.EMPTY_LIST);
	}

	/**
	 * Create an empty combo box model with a text header that is displayed
	 * before anything has been selected.
	 * 
	 * @param header
	 *            the text header.
	 */
	@SuppressWarnings("unchecked")
	public DefaultComboBoxModel(String header) {
		this(header, Collections.EMPTY_LIST);
	}

	/**
	 * Create a combo box model populated with items from the given array.
	 * 
	 * @param items
	 *            the array of items with which to populate the model.
	 */
	@SuppressWarnings("unchecked")
	public DefaultComboBoxModel(final T... items) {
		this(null, Arrays.asList(items));
	}

	/**
	 * Create a combo box model populated with items from the given array and
	 * with a text header that is displayed before anything has been selected.
	 * 
	 * @param header
	 *            the text header.
	 * @param items
	 *            the array of items with which to populate the model.
	 */
	@SuppressWarnings("unchecked")
	public DefaultComboBoxModel(String header, final T... items) {
		this(header, Arrays.asList(items));
	}

	/**
	 * Create a combo box model populated with items from the given list.
	 * 
	 * @param items
	 *            the list of item with which to populate the model.
	 */
	public DefaultComboBoxModel(Collection<T> items) {
		this(null, items);
	}

	/**
	 * Create a combo box model populated with items from the given list and
	 * with a text header that is displayed before anything has been selected.
	 * 
	 * @param header
	 *            the text header.
	 * @param items
	 *            the list of item with which to populate the model.
	 */
	public DefaultComboBoxModel(String header, Collection<T> items) {
		this(header, items, NULL_ACTION);
	}

	/**
	 * Create a combo box model populated with items from the given list and
	 * with a text header that is displayed before anything has been selected.
	 * 
	 * @param header
	 *            the text header.
	 * @param items
	 *            the list of item with which to populate the model.
	 * @param selectionAction
	 *            the action to perform on item selection.
	 */
	public DefaultComboBoxModel(String header, Collection<T> items,
			Action selectionAction) {
		this.header = header;
		this.selectionAction = selectionAction;

		elements.addAll(items);

		resetSelection();
	}

	/**
	 * Specify the {@link Action} to be performed when an item is selected.
	 * 
	 * @param action
	 *            the action to perform.
	 */
	public void setSelectionAction(Action action) {
		selectionAction = action;
	}

	/**
	 * Get the selected element. This is typed version of
	 * {@link #getSelectedItem()}.
	 * 
	 * @return the selected element.
	 */
	public T getSelectedElement() {
		return headerSelected ? null : selectedItem;
	}

	/**
	 * Return whether an element has been selected. The header does not count as
	 * an element.
	 * 
	 * @return {@code true} if an element has been selected, {@code false}
	 *         otherwise.
	 */
	public boolean hasSelectedElement() {
		return !headerSelected && selectedItem != null;
	}

	/**
	 * Get the index of given element in the model.
	 * 
	 * @param element
	 *            the given element.
	 * @return the index.
	 */
	public int getIndexOf(T element) {
		return elements.indexOf(element) + (showHeader ? 1 : 0);
	}

	/**
	 * Add an element to the model.
	 * 
	 * @param element
	 *            the element to add.
	 */
	public void addElement(T element) {
		elements.add(element);
		fireIntervalAdded(this, elements.size() - 1, elements.size() - 1);
		if (elements.size() == 1) {
			setSelectedElement(element);
		}
	}

	/**
	 * Insert an element into the model at a given index location.
	 * 
	 * @param element
	 *            the element to insert.
	 * @param index
	 *            the index at which to insert the element.
	 */
	public void insertElementAt(T element, int index) {
		elements.add(index - (showHeader ? 1 : 0), element);
		fireIntervalAdded(this, index, index);
	}

	/**
	 * Remove a given element from the model.
	 * 
	 * @param element
	 *            the element to remove.
	 */
	@SuppressWarnings("unchecked")
	public void removeElement(T element) {
		if (element == null) {
			throw new NullPointerException();
		}

		int index = elements.indexOf(element) + (showHeader ? 1 : 0);

		if (element.equals(selectedItem)) {
			if (index == 0) {
				setSelectedElement(getSize() == 1 ? null
						: getElement(index + 1));
			} else {
				setSelectedElement(getElement(index - 1));
			}
		}

		elements.remove(element);
		fireIntervalRemoved(this, index, index);
	}

	/**
	 * Remove all elements from the model.
	 */
	public void removeAllElements() {
		if (!elements.isEmpty()) {
			int firstIndex = 0;
			int lastIndex = elements.size();
			elements.clear();
			resetSelection();
			fireIntervalRemoved(this, firstIndex, lastIndex);
		}
	}

	/**
	 * Reset all the fields related to item selection back to their default
	 * states.
	 */
	private void resetSelection() {
		showHeader = (header != null);
		selectedItem = null;
		headerSelected = false;

		if (showHeader) {
			headerSelected = true;
		} else if (!elements.isEmpty()) {
			selectedItem = elements.get(0);
		}
	}

	/**
	 * Add all elements from a given collection to the model.
	 * 
	 * @param elementCollection
	 *            the collection of elements to add.
	 */
	public void addAllElements(Collection<T> elementCollection) {
		int beginIdx = elements.size();
		elements.addAll(elementCollection);
		int endIdx = elements.size();
		if (!showHeader && selectedItem == null && !elements.isEmpty()) {
			setSelectedElement(getElement(0));
		}
		fireIntervalAdded(this, beginIdx, endIdx);
	}

	/**
	 * Set the selected element to the given element if it exists it the model,
	 * or to {@code null} if it does not.
	 * 
	 * This is a type safe version of {@link #setSelectedItem(Object)} and
	 * should be used instead.
	 * 
	 * @param element
	 *            the element to set as selected.
	 */
	public void setSelectedElement(T element) {
		setSelectedItem(element);
	}

	/**
	 * Set the model to have the header selected.
	 */
	public void selectHeader() {
		setSelectedItem(header);
	}

	/**
	 * Return the element at the given index.
	 * 
	 * @param index
	 *            the index in the list for which to return the element.
	 * @return the model element, or {@code null} if the index is invalid.
	 */
	public T getElement(int index) {
		final int startIdx = showHeader ? 1 : 0;

		if (index >= startIdx && index < getSize()) {
			return elements.get(index - startIdx);
		} else {
			return null;
		}
	}

	// <editor-fold defaultstate="collapsed" desc="ComboBoxModel overrides">
	@Override
	@SuppressWarnings("unchecked")
	@Deprecated
	public void setSelectedItem(Object item) {
		if (item != null && item.equals(header)) {
			if (!headerSelected) {
				headerSelected = true;
				fireContentsChanged(this, -1, -1);
			}
		} else {
			headerSelected = false;

			if ((selectedItem == null && item != null)
					|| !(selectedItem != null && selectedItem.equals(item))) {
				showHeader = false;
				selectedItem = (T) item;
				fireContentsChanged(this, -1, -1);
				selectionAction.actionPerformed(selectionEvent);
			}
		}
	}

	@Override
	@Deprecated
	public Object getSelectedItem() {
		return headerSelected ? header : selectedItem;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="ListModel overrides">
	@Override
	public int getSize() {
		return elements.size() + (showHeader ? 1 : 0);
	}

	@Override
	@Deprecated
	public T getElementAt(int index) {
		if (showHeader && index == 0) {
			return null; // header;
		} else {
			return getElement(index);
		}
	}
	// </editor-fold>

}
