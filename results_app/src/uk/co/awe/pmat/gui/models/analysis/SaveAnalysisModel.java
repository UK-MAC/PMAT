package uk.co.awe.pmat.gui.models.analysis;

import java.util.List;
import javax.swing.event.ChangeListener;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.utils.ChangeEventSupport;

/**
 * A class to handle the loading, saving and exporting on user analyses to/from
 * the database.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SaveAnalysisModel implements GuiModel {

	private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
	private final AnalysisModel analysisModel;
	private final Configuration config;

	private String notes;
	private boolean isPrivate;
	private String creator;

	/**
	 * Create a new model with a reference to the over arching analysis model
	 * and the application properties.
	 * 
	 * @param analysisModel
	 *            the analysis model.
	 * @param config
	 *            the application properties.
	 */
	public SaveAnalysisModel(AnalysisModel analysisModel, Configuration config) {
		this.analysisModel = analysisModel;
		this.config = config;
		creator = null;
	}

	/**
	 * Returns a model that drives a dialog box displaying all the analyses
	 * currently saved in the database.
	 * 
	 * @return A model for a dialog box
	 * @throws DatabaseException
	 *             if no connection to the database could be established
	 */
	public AnalysesDisplayDialogModel getAnalysesDisplayDialogModel()
			throws DatabaseException {

		return new AnalysesDisplayDialogModel(analysisModel);
	}

	/**
	 * Return the analysis creator to save with the current analyses. This is
	 * either a value that has been set via {@link #setCreator(String)} or
	 * defaults to the current user.
	 * 
	 * @return The analysis creator
	 */
	public String getCreator() {
		return (creator == null ? config
				.getProperty(Configuration.Key.NAME_IN_DATABASE) : creator);
	}

	/**
	 * Return the notes to save with the current analysis.
	 * 
	 * @return The analysis notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Set the creator to save with the current analysis.
	 * 
	 * @param creator
	 *            The analysis creator
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Set the notes to save with the current analysis.
	 * 
	 * @param notes
	 *            The analysis notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Set whether this analysis should be marked as private in the database.
	 * 
	 * @param setAsPrivate
	 *            {@code true} if the analysis should be marked as private,
	 *            {@code false} otherwise.
	 */
	public void setPrivate(boolean setAsPrivate) {
		isPrivate = setAsPrivate;
	}

	/**
	 * Save the current analysis to the database.
	 * 
	 * @throws DatabaseException
	 *             if no connection to the database could be established
	 */
	public void saveAnalysis() throws DatabaseException {
		analysisModel.saveAnalysis();
	}

	/**
	 * Return all the analyses currently saved in the database.
	 * 
	 * @param currentUserOnly
	 *            Whether to return analyses saved by this user only or all
	 *            saved analyses
	 * @return The saved analyses
	 * @throws DatabaseException
	 *             if not connection to the database could be established
	 */
	public List<Analysis> getSavedAnalyses(boolean currentUserOnly)
			throws DatabaseException {
		if (currentUserOnly) {
			final String userName = config
					.getProperty(Configuration.Key.NAME_IN_DATABASE);
			return DatabaseManager.getConnection().getSavedAnalyses(userName);
		} else {
			return DatabaseManager.getConnection().getSavedAnalyses(null);
		}
	}

	/**
	 * Return whether this analysis will be marked as private in the database.
	 * 
	 * @return {@code true} if the analysis has/will be marked as private in the
	 *         database, {@code false} otherwise
	 */
	boolean isPrivate() {
		return isPrivate;
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		eventSupport.addChangeListener(listener);
	}

	/**
	 * Update this models state from the given persistence object.
	 * 
	 * @param analysis
	 *            the persistence object.
	 */
	void updateFromAnalysis(Analysis analysis) {
		creator = analysis.getCreator();
		notes = analysis.getNotes();
		isPrivate = analysis.isDataPrivate();
	}

}
