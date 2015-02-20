/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.awe.pmat.gui.events;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.utils.DefaultHashMap;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public class EventHub {

	private static final Logger LOG = LoggerFactory.getLogger(EventHub.class);

	private final Map<EventType, List<EventListener>> eventListeners = DefaultHashMap
			.mapOfLists();

	private PropertyChangeListener taskListener;

	public enum EventType {
		/** AXIS */
		AXIS,
		/** ANALYSIS */
		ANALYSIS,
		/** DERIVED_DATA */
		DERIVED_DATA,
		/** FILTERS */
		FILTERS,
		/** GRAPH */
		GRAPH,
		/** SERIES */
		SERIES;
	}

	public EventHub(PropertyChangeListener taskListener) {
		this.taskListener = taskListener;
	}

	public void addEventListener(EventListener listener,
			EventType... eventTypes) {
		if (eventTypes.length == 0) {
			for (EventType eventType : EventType.values()) {
				eventListeners.get(eventType).add(listener);
			}
		} else {
			for (EventType eventType : eventTypes) {
				eventListeners.get(eventType).add(listener);
			}
		}
	}

	/**
	 * Let all required models know about a particular type of analysis change
	 * that has occurred.
	 * 
	 * @param eventType
	 *            the type of the analysis change.
	 */
	public void notifyEvent(final EventType eventType) {
		LOG.debug("Broadcasting event: " + eventType);
		for (EventListener listener : eventListeners.get(eventType)) {
			notifyEvent(listener, eventType);
		}
	}

	/**
	 * Perform the actual change notification in a worker thread via a {@code
	 * SwingWorker}.
	 * 
	 * @param listener
	 *            the listener that will be informed of the state of the {@code
	 *            SwingWorker}.
	 * @param eventType
	 *            the type of the analysis change event.
	 */
	private void notifyEvent(final EventListener listener,
			final EventType eventType) {
		// SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		// @Override protected Void doInBackground() throws Exception {
		// listener.receiveEvent(eventType);
		// return null;
		// }
		// };
		// worker.addPropertyChangeListener(taskListener);
		// worker.execute();
		listener.receiveEvent(eventType);
	}
}
