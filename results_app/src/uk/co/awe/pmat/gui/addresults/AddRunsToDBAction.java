package uk.co.awe.pmat.gui.addresults;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.gui.models.AddResultsModel;

/**
 * The action used to add loaded files into the database.
 * 
 * @author AWE Plc copyright 2013
 */
final class AddRunsToDBAction extends AbstractAction {

	private static final Logger LOG = LoggerFactory
			.getLogger(AddRunsToDBAction.class);

	private final PropertyChangeListener workerListener;
	private final AddResultsModel model;

	private SwingWorker<Void, Void> worker;

	/**
	 * Create a new {@code AddRunsToDBAction}.
	 * 
	 * @param workerListener
	 *            the listener to be informed of the state of any background
	 *            tasks.
	 * @param model
	 *            the add results model.
	 */
	AddRunsToDBAction(PropertyChangeListener workerListener,
			AddResultsModel model) {
		super("Add Result To Database");
		this.workerListener = workerListener;
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (worker != null) {
			boolean wasCancelled = worker.cancel(false);
			if (wasCancelled) {
				LOG.debug("Worker thread cancelled.");
			}
			worker = null;
		}

		worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				final List<Run> runs = model.getRunsToAdd();
				int count = 0;
				for (Run run : runs) {
					if (isCancelled()) {
						break;
					}
					try {
						model.addRunToDB(run);
					} catch (Exception ex) {
						LOG.debug("Error saving run: " + run, ex);
						throw ex;
					}
					++count;
					if (!isCancelled()) {
						setProgress(Math.round((100.f * count) / runs.size()));
					}
				}
				return null;
			}

		};

		worker.addPropertyChangeListener(workerListener);
		worker.execute();
	}

}
