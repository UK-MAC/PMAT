package uk.co.awe.pmat.gui.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.utils.Pair;

/**
 * A table model used to display results files and store the {@code Run} objects
 * loaded from these files awaiting saving into the database.
 *
 * @author AWE Plc copyright 2013
 */
public final class RunTableModel implements TableModel {

    private static final Logger LOG = LoggerFactory.getLogger(RunTableModel.class);

    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final Color ADDED_COLOR = new Color(0xAA, 0xFF, 0xAA);
    private static final Color FAILED_COLOR = new Color(0xFF, 0x99, 0x99);
    private static final String[] COLUMN_NAMES = {"Directory", "File Name", "Add"};

    private final List<FileInfo> files = new ArrayList<>();
    private final List<TableModelListener> listeners = new ArrayList<>();
    private final Configuration config;

    /**
     * Creates a new {@code RunTableModel}.
     *
     * @param config the application configuration.
     */
    public RunTableModel(Configuration config) {
        this.config = config;
    }
    
    /**
     * A new file to the table.
     *
     * @param data the data loaded from the file.
     */
    public void addFile(Run data) {
        final File file = new File(data.getFile());
        files.add(new FileInfo(file, data, true));
        fireListeners(files.size());
    }

    /**
     * Return all the {@code Run}s corresponding to the files which have been
     * marked as to be saved into the database.
     *
     * @return the {@code Run}s to add the database.
     */
    public List<Run> getRunsToAdd() {
        final List<Run> filesToAdd = new ArrayList<>();

        for (FileInfo fileInfo : files) {
            if (fileInfo.isToAdd()) {
                filesToAdd.add(fileInfo.getRun());
            }
        }

        return filesToAdd;
    }

    /**
     * Open up the file at the given row index in an external file editor.
     *
     * @param row the row index.
     * @throws IOException if an error occurs launching the external file
     * editor.
     */
    public void openFileForRow(int row) throws IOException {
        final File file = files.get(row).getFile();
        LOG.info("Opening file " + file.getAbsolutePath());
        String editor = config
                .getProperty(Configuration.Key.FILE_EDITOR);
        ProcessBuilder procBuilder = new ProcessBuilder(editor,
                file.getAbsolutePath());
        procBuilder.start();
    }

    /**
     * Set the success state of adding the given run to the database.
     *
     * @param run the run which has been added.
     * @param success {@code true} if the run was successfully added to the
     * database, {@code false} otherwise.
     */
    public void setAdded(Run run, boolean success) {
        for (FileInfo fileInfo : files) {
            if (fileInfo.getRun().equals(run)) {
                if (success) {
                    fileInfo.setState(FileInfo.State.ADDED);
                    fileInfo.setToAdd(false);
                } else {
                    fileInfo.setState(FileInfo.State.FAILED);
                }
            }
            fireListeners(files.indexOf(fileInfo));
        }
    }

    /**
     * Returns the exception stored against the given row index.
     *
     * @param row the row index.
     * @return the exception stored against the row, or {@code null}.
     */
    public Exception getRowException(int row) {
        return files.get(row).getException();
    }

    /**
     * Sets the exception to stored against the given run.
     *
     * @param run the run to stored the exception against.
     * @param exception the exception to store.
     */
    public void setException(Run run, Exception exception) {
        for (FileInfo fileInfo : files) {
            if (fileInfo.getRun().equals(run)) {
                fileInfo.setException(exception);
            }
        }
    }

    /**
     * Return the file and it's run data file for the given row index.
     *
     * @param row the row index.
     * @return the run data and file.
     */
    public Pair<Run, File> getRunFileAt(int row) {
        final FileInfo fileInfo = files.get(row);
        return new Pair<>(fileInfo.getRun(), fileInfo.getFile());
    }

    /**
     * Toggle the "to add" state of the row with the given row index.
     * 
     * @param row the row index.
     */
    public void toggleRow(int row) {
        files.get(row).setToAdd(!files.get(row).isToAdd());
        fireListeners(row);
    }

    /**
     * Inform the table model listeners of a change to the row with the given
     * row index.
     *
     * @param row the row index.
     */
    private void fireListeners(int row) {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this, row));
        }
    }

    /**
     * Inform the table model listeners of a change to the table data.
     */
    private void fireListeners() {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return (column == COLUMN_NAMES.length - 1);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == COLUMN_NAMES.length - 1)
                ? Boolean.class
                : String.class;
    }

    /**
     * Clear all the data in the table.
     */
    public void clear() {
        files.clear();
        fireListeners();
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileInfo row = files.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getFile().getParent();
            case 1:
                return row.getFile().getName();
            case 2:
                return row.isToAdd();
            default:
                throw new IllegalArgumentException("Invalid columnIndex: "
                        + columnIndex);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COLUMN_NAMES.length - 1) {
            if (!(aValue instanceof Boolean)) {
                throw new IllegalArgumentException("Trying to set row "
                        + rowIndex + " column " + columnIndex
                        + " with illegal value " + aValue);
            }
            files.get(rowIndex).setToAdd((Boolean) aValue);
        }
        fireListeners(rowIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * Returns the colour that should be used for the row with the given row
     * index.
     *
     * @param rowIdx the row index.
     * @return the colour to use.
     */
    public Color getRowColor(int rowIdx) {
        switch (files.get(rowIdx).getState()) {
            case ADDED:
                return ADDED_COLOR;
            case FAILED:
                return FAILED_COLOR;
            default:
                return DEFAULT_COLOR;
        }
    }

}

/**
 * A helper class which is used to store the state of files in the
 * {@code RunTableModel}.
 * 
 * @author AWE Plc copyright 2013
 */
class FileInfo {

    private final File file;
    private final Run run;
    private boolean toAdd;
    private State state;
    private Exception exception;

    /**
     * Creates a new {@code FileInfo}.
     *
     * @param file the file which is being loaded.
     * @param run the run data for the file.
     * @param toAdd {@code true} if the file should be added to the database,
     * {@code false} otherwise.
     */
    FileInfo(File file, Run run, boolean toAdd) {
        this.file = file;
        this.run = run;
        this.toAdd = toAdd;
        state = State.INCOMPLETE;
    }

    /**
     * An {@code Enum} representation of the state of the run data.
     */
    public enum State {
        /** The run data does not have all required fields completed  */
        INCOMPLETE,
        /** The run data is complete and is awaiting saving into the database */
        COMPLETE,
        /** The run data was successfully saved into the database */
        ADDED,
        /** An error occurred trying to save the run data into the database. */
        FAILED
    }

    /**
     * Returns any exception stored against the file.
     *
     * @return the stored exception or {@code null} if nothing has been stored.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the file.
     *
     * @return the file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the run data.
     *
     * @return the run data.
     */
    public Run getRun() {
        return run;
    }

    /**
     * Returns the state of the run data.
     *
     * @return the run data state.
     */
    public State getState() {
        return state;
    }

    /**
     * Returns whether the run data should be added to the database.
     *
     * @return {@code true} if the run data should be added to the database,
     * {@code false} otherwise.
     */
    public boolean isToAdd() {
        return toAdd;
    }

    /**
     * Sets whether the run data should be added to the database.
     *
     * @param add {@code true} if the run data should be added to the database,
     * {@code false} otherwise.
     */
    public void setToAdd(boolean add) {
        toAdd = add;
    }

    /**
     * Sets the state of the run data.
     *
     * @param state the run data state.
     */
    void setState(State state) {
        this.state = state;
    }

    /**
     * Sets the exception stored against the file.
     *
     * @param exception the exception to store.
     */
    void setException(Exception exception) {
        this.exception = exception;
    }

}
