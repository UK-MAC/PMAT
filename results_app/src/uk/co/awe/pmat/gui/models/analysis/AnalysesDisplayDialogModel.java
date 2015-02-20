package uk.co.awe.pmat.gui.models.analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;

/**
 * The model that underpins the {@code AnalysesDisplayDialog}. This is used
 * to display the saved analyses.
 *
 * @author Hollcombe (Tessella plc)
 * @see uk.co.awe.pmat.gui.analysis.AnalysesDisplayDialog
 */
public final class AnalysesDisplayDialogModel {

    private final Map<Long, Analysis> analysisMap = new HashMap<>();
    private final AnalysisModel analysisModel;
    private final DisplayTableModel displayTableModel;

    private boolean currentUserOnly;

    /**
     * Create a new {@code AnalysesDisplayDialogModel} instance with a
     * connection to the overarching {@code AnalysisModel}.
     *
     * @param analysisModel The model underpinning all the analysis panels
     * @throws DatabaseException if no connection to the database could
     * be established
     */
    public AnalysesDisplayDialogModel(AnalysisModel analysisModel)
            throws DatabaseException {

        this.analysisModel = analysisModel;
        displayTableModel = new DisplayTableModel(Analysis.TABLE_COLUMN_NAMES, Analysis.TABLE_COLUMN_TYPES);
        currentUserOnly = true;

        updateDisplayTableModel();
    }

    /**
     * Export the analysis given by the selected row to a file.
     *
     * @param rowIdx The index of the selected row
     * @param file The file to export the analysis to
     * @throws DatabaseException if no connection to the database could
     * be established
     * @throws IOException if an error occurs when writing the analysis to file
     */
    public void exportSelectedRow(int rowIdx, File file)
            throws DatabaseException, IOException {

        Long id = displayTableModel.getId(rowIdx);
        Analysis analysis = analysisMap.get(id);
        analysisModel.exportAnalysis(analysis, file);
    }

    /**
     * Return a table model that holds all the saved analyses.
     *
     * @return The saved analyses table model
     */
    public TableModel getAnalysesDisplayTableModel() {
        return displayTableModel;
    }

    /**
     * Load the analysis given by the selected row and use it to update all the
     * relevant analysis panes.
     *
     * @param rowIdx the index of the selected row.
     * @return the loaded analysis.
     */
    public AnalysisModel loadSelectedRow(int rowIdx) {

        Long id = displayTableModel.getId(rowIdx);
        Analysis analysis = analysisMap.get(id);
        return analysisModel.loadAnalysis(analysis);
    }

    /**
     * Return whether we are showing all saved analyses or just the ones for the
     * current user.
     *
     * @return {@code true} if just showing analyses for the current user,
     * {@code false} otherwise
     */
    public boolean getCurrentUserOnly() {
        return currentUserOnly;
    }

    /**
     * Set whether we should show all saved analyses or just ones for the
     * current user.
     *
     * @param currentUserOnly {@code true} if we should just show analyses for
     * the current user, {@code false} otherwise
     * @throws DatabaseException if no connection to the database could
     * be established
     */
    public void setCurrentUserOnly(boolean currentUserOnly)
            throws DatabaseException {
        this.currentUserOnly = currentUserOnly;
        updateDisplayTableModel();
    }

    /**
     * Delete the currently selected analysis from the database.
     *
     * @param modelRow The row corresponding to the analysis to delete, this
     * should be the "model" and not the "table" row index
     * @throws DatabaseException if no connection to the database could
     * be established
     */
    public void deleteSelectedAnalysis(int modelRow)
            throws DatabaseException {
        Long id = displayTableModel.getId(modelRow);
        Analysis analysis = analysisMap.get(id);
        analysisModel.deleteAnalysis(analysis);
        updateDisplayTableModel();
    }

    /**
     * Update the table model displaying the the analyses.
     *
     * @throws DatabaseException if no connection to the database could
     * be established
     */
    private void updateDisplayTableModel()
            throws DatabaseException {
        displayTableModel.removeAllRows();

        List<Analysis> analyses = analysisModel.getSaveAnalysisModel()
                .getSavedAnalyses(currentUserOnly);

        Long id = 0L;
        for (Analysis analysis : analyses) {
            analysisMap.put(id, analysis);
            displayTableModel.addRow(id, analysis.asTableRow());
            ++id;
        }
    }

}
