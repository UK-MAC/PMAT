package uk.co.awe.pmat.gui.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import uk.co.awe.pmat.gui.models.analysis.ViewResultDialogModel;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;

/**
 * Model allowing results to be added to the database
 *
 * @author ofjp
 */
public final class ViewResultsModel {

    private static final Logger LOG = LoggerFactory.getLogger(ViewResultsModel.class);

    private final Map<Long, Run> runMap = new HashMap<>();

    /**
     * Return the table model used to show the {@code Run}s stored in the
     * database.
     *
     * @return the {@code Run}s table model.
     * @throws DatabaseException if an error occurs loading the {@code Run}s
     * from the database.
     */
    public DisplayTableModel getResultsTableModel() throws DatabaseException {

        final List<String> columnNames = new ArrayList<>(Run.TABLE_COLUMNS.size());
        final List<Class<?>> columnTypes = new ArrayList<>(Run.TABLE_COLUMNS.size());
        for (Run.Column column : Run.TABLE_COLUMNS) {
            columnNames.add(column.getColumnName());
            columnTypes.add(column.getColumnClass());
        }
        
        final DisplayTableModel tableModel =
                new DisplayTableModel(columnNames, columnTypes);

        try {
            List<Run> runs = DatabaseManager.getConnection().getRuns();

            Long id = 0L;
            for (Run run : runs) {
                tableModel.addRow(id, run.asTableRow());
                runMap.put(id, run);
                ++id;
            }
        } catch (RuntimeException ex) {
            throw ex; // Catch everything except RuntimeExceptions
        } catch (Exception ex) {
            LOG.error("Unable to populate view table!", ex);
        }

        return tableModel;
    }

    /**
     * Remove an entity from the database.
     *
     * @param id ID of the entity to remove.
     * @throws DatabaseException if no connection to the database could
     * be established
     */
    public void deleteRun(Long id) throws DatabaseException, IOException {
        final Run run = runMap.get(id);
        DatabaseManager.getConnection().delete(run);
        final String runFile = run.getFile();
        if (runFile != null && !runFile.isEmpty()) {
            final Path path = FileSystems.getDefault().getPath(runFile);
            final Path storeDir = FileSystems.getDefault()
                    .getPath(Constants.Application.RESULT_STORE_DIRECTORY);
            if (!path.startsWith(storeDir)) {
                throw new IOException("Attempting to delete a stored result file"
                        + " which is not in the result store directory. " + path);
            }
            LOG.info("Removing result file " + path);
            Files.deleteIfExists(path);
        }
    }

    /**
     * Export the selected runs to XML and write to the file provided.
     *
     * @param file The file to write the XML to
     * @param ids The IDs of the Runs to export
     * @throws IOException on write error
     * @throws DatabaseException if no connection to the database could
     * be established
     */
    public void exportRowsToFile(File file, List<Long> ids) throws IOException, DatabaseException {
        List<XMLSerialisable> dataToExport = new ArrayList<>(ids.size());

        for (Long id : ids) {
            try {
                Run run = runMap.get(id);
                run.loadDataSets();
                run.loadFlags();
                dataToExport.add(run);
            } catch (Exception ex) {
                LOG.error("Export error", ex);
            }
        }

        XMLSerialiser serialiser = new XMLSerialiser();
        LOG.debug("Exporting " + ids.size() + " rows to file " + file);
        serialiser.serialise(file, dataToExport);
    }

    /**
     * Return the {@code ViewResultDialogModel} used to display a dialog box
     * showing all the results stored for the {@code Run} with the given id.
     *
     * @param id the id of the {@code Run} to display.
     * @return the dialog model.
     */
    public ViewResultDialogModel getViewResultDialogModel(Long id) {
        return new ViewResultDialogModel(runMap.get(id));
    }

}