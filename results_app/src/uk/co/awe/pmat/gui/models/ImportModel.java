package uk.co.awe.pmat.gui.models;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.ApplicationException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;
import uk.co.awe.pmat.utils.Pair;

/**
 * The model driving the result import panel used to import results from PMAT
 * results export files.
 *
 * @author AWE Plc copyright 2013
 */
public final class ImportModel {

    private static final Logger LOG = LoggerFactory.getLogger(ImportModel.class);

    private final DisplayTableModel tableModel;
    private final Map<Long, Run> runs = new HashMap<>();
    private final PropertyChangeListener workerListener;
    
    private File importFile;
    
    private final Action loadAction = new AbstractAction("Load") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                loadFromFile();
            } catch (ApplicationException ex) {
                ExceptionDialog.showExceptionDialog(ex, "No import file selected");
            }
        }
    };
    
    private final Action importAction = new AbstractAction("Import") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                saveIntoDB();
            } catch (ApplicationException ex) {
                ExceptionDialog.showExceptionDialog(ex, "No import file selected");
            }
        }
    };
    
    /**
     * Create a new {@code ImportModel}.
     *
     */
    public ImportModel(PropertyChangeListener workerListener) {
        this.workerListener = workerListener;
        final String[] editableCols = new String[] {"Is Private"};
        final List<String> columnNames = new ArrayList<>(Run.TABLE_COLUMNS.size());
        final List<Class<?>> columnTypes = new ArrayList<>(Run.TABLE_COLUMNS.size());
        for (Run.Column column : Run.TABLE_COLUMNS) {
            columnNames.add(column.getColumnName());
            columnTypes.add(column.getColumnClass());
        }
        tableModel = new DisplayTableModel(columnNames, columnTypes, editableCols);
    }

    /**
     * Return the table model used to display the loaded {@code Run}s.
     *
     * @return the table model.
     */
    public DisplayTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Load all the runs in the given file.
     *
     * @throws ApplicationException if no file has been selected before calling
     * this method.
     * @throws IOException if an error occurs loading the runs.
     */
    public void loadFromFile() throws ApplicationException {
        if (importFile == null) {
            throw new ApplicationException("No file selected");
        }
        
        LOG.debug("Loading file: " + importFile.getAbsoluteFile());

        runs.clear();
        
        final SwingWorker<Void, Pair<Long, Run>> worker = new SwingWorker<Void, Pair<Long, Run>>() {
            @Override
            protected Void doInBackground() throws Exception {
                final XMLSerialiser serialiser = new XMLSerialiser();
                final SwingWorker<Void, Pair<Long, Run>> thisWorker = this;

                serialiser.deserialiseRuns(importFile, false, new XMLSerialiser.LoadedAction() {
                    private Long runIdx = 0L;
                    @Override
                    public void doAction(XMLSerialisable node) {
                        LOG.debug("Loaded data: " + node);
                        publish(new Pair<>(runIdx, (Run) node));
                        ++runIdx;
                    }
                    @Override
                    public boolean isCancelled() {
                        return thisWorker.isCancelled();
                    }
                });
                
                return null;
            }

            @Override
            protected void process(List<Pair<Long, Run>> chunks) {
                for (Pair<Long, Run> pair : chunks) {
                    runs.put(pair.getFirst(), pair.getSecond());
                }
                updateTable();
            }

            @Override
            protected void done() {
                importAction.setEnabled(true);
            }
        };
        
        worker.addPropertyChangeListener(workerListener);
        worker.execute();
    }

    /**
     * Update the table with the loaded runs.
     */
    public void updateTable() {
        tableModel.removeAllRows();
        for (Map.Entry<Long, Run> entry : runs.entrySet()) {
            tableModel.addRow(entry.getKey(), entry.getValue().asTableRow());
        }
    }

    /**
     * Save the loaded runs into the database.
     */
    public void saveIntoDB() throws ApplicationException {
        if (importFile == null) {
            throw new ApplicationException("No file selected");
        }
        
        final int numRuns = runs.size();
        
        final SwingWorker<Void, Pair<Long, Exception>> worker = new SwingWorker<Void, Pair<Long, Exception>>() {
            @Override
            protected Void doInBackground() throws Exception {
                final XMLSerialiser serialiser = new XMLSerialiser();
                final SwingWorker<Void, Pair<Long, Exception>> thisWorker = this;

                serialiser.deserialiseRuns(importFile, true, new XMLSerialiser.LoadedAction() {
                    private Long runIdx = 0L;
                    @Override
                    public void doAction(XMLSerialisable node) {
                        try {
                            final Run run = (Run) node;
                            DatabaseManager.getConnection().save(run);
                            publish(new Pair<>(runIdx, (Exception) null));
                        } catch (Exception ex) {
                            publish(new Pair<>(runIdx, ex));
                        }
                        ++runIdx;
                        final int progress = (int) (((float) runIdx) / numRuns * 100);
                        System.err.println(progress);
                        setProgress(progress);
                    }

                    @Override
                    public boolean isCancelled() {
                        return thisWorker.isCancelled();
                    }
                });
                
                return null;
            }

            @Override
            protected void process(List<Pair<Long, Exception>> chunks) {
                for (Pair<Long, Exception> pair : chunks) {
                    if (pair.getSecond() == null) {
                        tableModel.setSuccessful(pair.getFirst());
                    } else {
                        tableModel.setFailed(pair.getFirst(), pair.getSecond());
                    }
                }
            }
        };
        
        worker.addPropertyChangeListener(workerListener);
        worker.execute();
    }

    public Action getImportAction() {
        return importAction;
    }

    public Action getLoadAction() {
        return loadAction;
    }

    public void setImportFile(File importFile) {
        this.importFile = importFile;
        loadAction.setEnabled(importFile.exists());
        importAction.setEnabled(false);
    }

}
