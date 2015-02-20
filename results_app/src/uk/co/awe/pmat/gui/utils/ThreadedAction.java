package uk.co.awe.pmat.gui.utils;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.gui.ExceptionDialog;

/**
 * A {@code Swing} {@code Action} which launches the task via the {@code
 * SwingWorker} to be performed on a worker thread. Any exceptions caught from
 * this task are handled by an {@code ExceptionDialog} on the {@code EDT}. A
 * callback can also be defined via the {@link #done()} method which will be
 * called on the {@code EDT} once the main task is completed.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class ThreadedAction extends AbstractAction {

	private final PropertyChangeListener taskListener;

	/**
	 * Create a new {@code ThreadedAction}.
	 * 
	 * @param name
	 *            the name of the action.
	 * @param icon
	 *            the icon to assign to the action.
	 * @param taskListener
	 *            a listener that will be informed of the state of the {@code
	 *            SwingWorker}.
	 */
	public ThreadedAction(String name, Icon icon,
			PropertyChangeListener taskListener) {
		super(name, icon);
		this.taskListener = taskListener;
	}

	/**
	 * Create a new {@code ThreadedAction}.
	 * 
	 * @param name
	 *            the name of the action.
	 * @param taskListener
	 *            a listener that will be informed of the state of the {@code
	 *            SwingWorker}.
	 */
	public ThreadedAction(String name, PropertyChangeListener taskListener) {
		super(name);
		this.taskListener = taskListener;
	}

	/**
	 * The task to perform in a worker thread. Any exception raised by this
	 * method will be caught by an {@code ExceptionDialog} to be handled on the
	 * {@code EDT}.
	 * 
	 * @throws Exception
	 *             if any exception is thrown whilst performing the action.
	 */
	public abstract void actionPerformedInBackground() throws Exception;

	/**
	 * Optional callback method that will be called on the {@code Swing EDT}
	 * after the {@link #actionPerformedInBackground()} method has finished.
	 */
	protected void done() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setEnabled(false);
		final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				try {
					actionPerformedInBackground();
				} catch (final ApplicationException ex) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane
									.showMessageDialog(null, ex.getMessage());
						}
					});
				} catch (final DatabaseException ex) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							ExceptionDialog.showDatabaseExceptionDialog(ex);
						}
					});
				} catch (final Exception ex) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							ExceptionDialog.showExceptionDialog(ex,
									"Exception in background task");
						}
					});
				}
				return null;
			}

			@Override
			protected void done() {
				ThreadedAction.this.done();
				setEnabled(true);
			}
		};
		worker.addPropertyChangeListener(taskListener);
		worker.execute();
	}

}
