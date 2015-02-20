package uk.co.awe.pmat.utils;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A helper class which handles the notification of {@code ChangeListener}s.
 * This is used by the GUI model classes to notify the GUI view classes when
 * there are updates that need to be displayed. The notification always happens
 * on the {@code EDT}.
 *
 * @author AWE Plc copyright 2013
 */
public final class ChangeEventSupport {

    private final Object source;
    private final List<ChangeListener> listeners = new ArrayList<>();

    /**
     * Create a new {@code ChangeEventSupport}.
     *
     * @param source the object that will be used as the {@code source} of the
     * {@link ChangeEvent}s.
     */
    public ChangeEventSupport(Object source) {
	if (source == null) {
	    throw new NullPointerException();
	}
	this.source = source;
    }

    /**
     * Add a {@code ChangeListener} to the collection of listeners to be
     * notified.
     *
     * @param listener the {@code ChangeListener} to add.
     */
    public void addChangeListener(ChangeListener listener) {
	if (listener == null) {
	    return;
	}
        listeners.add(listener);
    }

    /**
     * Remove a {@code ChangeListener} from the collection of listeners to be
     * notified.
     *
     * @param listener the {@code ChangeListener} to remove.
     */
    public void removeChangeListener(ChangeListener listener) {
	if (listener == null) {
	    return;
	}
        listeners.remove(listener);
    }

    /**
     * Fire a change event, notifying all registered {@code ChangeListener}s.
     * If the method is called from a thread that is not the {@code EDT}, then
     * the notifications will be append to the {@code EDT} queue.
     */
    public void fireChangeEvent() {
        if (SwingUtilities.isEventDispatchThread()) {
            notifyListeners();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() { notifyListeners(); }
            });
        }
    }

    /**
     * Notify all the registered {@code ChangeListener}s.
     */
    private void notifyListeners() {
        final ChangeEvent event = new ChangeEvent(source);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }

}
