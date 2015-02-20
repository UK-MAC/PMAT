package uk.co.awe.pmat.gui.addresults;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.models.AddResultsModel;

/**
 * The action which handles loading the results files and adding them to the
 * table.
 * 
 * @author AWE Plc copyright 2013
 */
final class LoadFilesAction extends AbstractAction {

	private static final Logger LOG = LoggerFactory
			.getLogger(LoadFilesAction.class);

	private static final String NO_FILES_FOUND = "No files found";

	private final PropertyChangeListener workerListener;
	private final AddResultsModel model;
	private final JTextField pathTextField;
	private final Action addToDBAction;
	private final Action selectAllAction;
	private final JTable fileTable;
	private final JLabel messageLabel;
	private final Object filesLock = new Object();

	private SwingWorker<Void, Void> worker;

	/**
	 * Create a new {@code LoadFilesAction}.
	 * 
	 * @param workerListener
	 *            the listener to be informed of the state of any background
	 *            tasks.
	 * @param model
	 *            the add results model.
	 * @param pathTextField
	 *            the text field to take the file path from.
	 * @param addToDBAction
	 *            the action to add the loaded files to the database.
	 * @param fileTable
	 *            the table to display the loaded files in.
	 * @param messageLabel
	 *            the label to use to display any error messages.
	 */
	LoadFilesAction(PropertyChangeListener workerListener,
			AddResultsModel model, JTextField pathTextField,
			Action addToDBAction, Action selectAllAction, JTable fileTable,
			JLabel messageLabel) {
		super("Load");
		this.workerListener = workerListener;
		this.model = model;
		this.pathTextField = pathTextField;
		this.addToDBAction = addToDBAction;
		this.selectAllAction = selectAllAction;
		this.fileTable = fileTable;
		this.messageLabel = messageLabel;
	}

	@Override
    public void actionPerformed(ActionEvent e) {

        addToDBAction.setEnabled(false);
        selectAllAction.setEnabled(false);

        if (worker != null) {
            boolean wasCancelled = worker.cancel(true);
            if (wasCancelled) { LOG.debug("Worker thread cancelled."); }
            worker = null;
        }

        model.clearFileTable();
        final String path = pathTextField.getText();

        worker = new SwingWorker<Void, Void>() {

            private final List<DataFile<?>> loadedFiles = new ArrayList<>();

            @Override
            protected Void doInBackground() throws Exception {
                final List<File> files = model.getFilesToLoad(path);
                int count = 0;
                for (File file : files) {
                    if (isCancelled()) {
                        break;
                    }
                    try {
                        synchronized (filesLock) {
                            DataFile<?> dataFile = model.loadFile(file);
                            loadedFiles.add(dataFile);
                        }
                        publish();
                        ++count;
                        if (!isCancelled()) {
                            setProgress(Math.round((100.f * count) / files.size()));
                        }
                    } catch (DatabaseException | IOException ex) {
                        LOG.debug("Error loading file " + file, ex);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Void> chunks) {
                synchronized (filesLock) {
                    model.updateFileTable(loadedFiles);
                }
            }

            @Override
            public void done() {
                synchronized (filesLock) {
                    model.updateFileTable(loadedFiles);
                }
                if (!loadedFiles.isEmpty()) {
                    try {
                        model.loadConfigData(loadedFiles);
                    } catch (DatabaseException ex) {
                        LOG.error("Error loading run configuration", ex);
                    }
                    addToDBAction.setEnabled(true);
                    selectAllAction.setEnabled(true);
                    fileTable.changeSelection(0, 0, false, false);
                    try {
                        model.loadConfigDataForRow(0, 0);
                    } catch (DatabaseException ex) {
                        ExceptionDialog.showDatabaseExceptionDialog(ex);
                    }
                } else {
                    messageLabel.setText(NO_FILES_FOUND);
                }
            }

        };
        
        worker.addPropertyChangeListener(workerListener);
        worker.execute();
    }
}
