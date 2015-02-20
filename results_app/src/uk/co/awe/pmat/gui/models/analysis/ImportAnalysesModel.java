package uk.co.awe.pmat.gui.models.analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;

/**
 * The model driving the import analyses panel.
 * 
 * @author AWE Plc copyright 2013
 */
public class ImportAnalysesModel {

    private static final Logger LOG = LoggerFactory.getLogger(ImportAnalysesModel.class);

    private final DisplayTableModel tableModel;
    private final Map<Long, Analysis> analyses = new HashMap<>();

    /**
     * Create a new {@code ImportAnalysesModel}.
     */
    public ImportAnalysesModel() {
        tableModel = new DisplayTableModel(Analysis.TABLE_COLUMN_NAMES,
                Analysis.TABLE_COLUMN_TYPES);
    }

    /**
     * Return the table model used to display the loaded analyses.
     *
     * @return the table model.
     */
    public DisplayTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Load the analyses from the given file and display them in the analyses
     * table.
     * 
     * @param importFile the file to import.
     * @throws IOException if an error occurs importing the analyses from the
     * file.
     */
    public void loadAnalysesFromFile(File importFile) throws IOException {
        LOG.debug("Loading file: " + importFile.getAbsoluteFile());

        final XMLSerialiser serialiser = new XMLSerialiser();
        
        analyses.clear();
        serialiser.deserialiseAnalyses(importFile, new XMLSerialiser.LoadedAction() {
            private Long runIdx = 0L;
            @Override
            public void doAction(XMLSerialisable node) {
                analyses.put(runIdx, (Analysis) node);
                ++runIdx;
                LOG.debug("Loaded data: " + node);
            }
            @Override
            public boolean isCancelled() {
                return false;
            }            
        });

        LOG.debug("Loaded " + analyses.size() + " analyses");
    }

    /**
     * Update the analyses table.
     */
    public void updateTable() {
        tableModel.removeAllRows();
        for (Map.Entry<Long, Analysis> entry : analyses.entrySet()) {
            tableModel.addRow(entry.getKey(), entry.getValue().asTableRow());
        }
    }

    /**
     * Save the loaded analyses into the database.
     */
    public void saveAnalysesIntoDB() {
        if (analyses == null) {
            throw new IllegalStateException("No runs loaded from file.");
        }

        for (Map.Entry<Long, Analysis> entry : analyses.entrySet()) {
            try {
                DatabaseManager.getConnection().save(entry.getValue());
                tableModel.setSuccessful(entry.getKey());
            } catch (Exception ex) {
                tableModel.setFailed(entry.getKey(), ex);
            }
        }
    }

}
