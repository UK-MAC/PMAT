package uk.co.awe.pmat.gui.models;

import uk.co.awe.pmat.gui.utils.RunTableModel;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.ApplicationException;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.RunSaver;
import uk.co.awe.pmat.datafiles.DataConfig;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.datafiles.DataFileFactory;
import uk.co.awe.pmat.datafiles.hpcc.HPCCFactory;
import uk.co.awe.pmat.datafiles.pmtm.PMTMFactory;
import uk.co.awe.pmat.datafiles.skampi.SkampiFactory;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.addresults.ConfigPanel;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.utils.FileUtils;

/**
 * The model behind the results loading panel, used to find and load files which
 * contain results and use this to create and saved runs into the database.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AddResultsModel {

	private static final Logger LOG = LoggerFactory
			.getLogger(AddResultsModel.class);

	private final RunTableModel runTableModel;
	private final DefaultComboBoxModel<DataFileFactory> fileTypeModel;
	private final ConfigPanelModel configPanelModel;

	/**
	 * Creates a new {@code AddResultModel}.
	 * 
	 * @param config
	 *            the application configuration.
	 */
	public AddResultsModel(Configuration config) {
		runTableModel = new RunTableModel(config);
		configPanelModel = new ConfigPanelModel(config);

		fileTypeModel = new DefaultComboBoxModel<DataFileFactory>();
		fileTypeModel.addElement(new PMTMFactory());
		fileTypeModel.addElement(new SkampiFactory());
		fileTypeModel.addElement(new HPCCFactory());
		fileTypeModel.setSelectedElement(fileTypeModel.getElement(0));
	}

	/**
	 * Return the list of {@code Run}s which the user has selected to be added
	 * to the database.
	 * 
	 * @return the {@code Run}s to add to the database.
	 */
	public List<Run> getRunsToAdd() {
		return runTableModel.getRunsToAdd();
	}

	/**
	 * Add the given run to the database.
	 * 
	 * @param run
	 *            the run to add.
	 */
	public void addRunToDB(Run run) {
		final DataFileFactory fileType = fileTypeModel.getSelectedElement();
		if (fileType.getRunConfigData() != null) {
			fileType.getRunConfigData().populateRun(run);
		}

		Run tmp;
		try {
			tmp = configPanelModel.populateRunData(run);
		} catch (ApplicationException ex) {
			ExceptionDialog.showExceptionDialog(ex, "Error saving run "
					+ run.getFile());
			return;
		}
		final Run updatedRun = tmp;
		final Exception ex = RunSaver.saveRun(updatedRun);
		final boolean saved = (ex == null);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runTableModel.setAdded(updatedRun, saved);
				runTableModel.setException(updatedRun, ex);
			}
		});
	}

	/**
	 * Clear all the data from the models behind the config panels.
	 */
	public void clearConfigData() {
		configPanelModel.clear();
		if (fileTypeModel.getSelectedElement().getRunConfigData() != null) {
			fileTypeModel.getSelectedElement().getRunConfigData().clear();
		}
	}

	/**
	 * Returns the path of the file at the given row index.
	 * 
	 * @param row
	 *            the row index.
	 * @return the file path.
	 */
	public String getFilePathAtRow(int row) {
		return runTableModel.getRunFileAt(row).getSecond().getAbsolutePath();
	}

	/**
	 * Load the configuration data for the given rows in the row specific config
	 * panel (if one exists).
	 * 
	 * @param first
	 *            the first row.
	 * @param last
	 *            the last row.
	 * @throws DatabaseException
	 *             if no connection to the database could be created
	 */
	public void loadConfigDataForRow(int first, int last)
			throws DatabaseException {
		if (first > last) {
			throw new IllegalArgumentException("First index must be less than#"
					+ " or equal to second index. " + first + " > " + last);
		}

		final DataConfig configData = fileTypeModel.getSelectedElement()
				.getRunConfigData();

		final List<Run> runs = new ArrayList<Run>();
		// first == -1 when this method is called with an empty selection.
		if (first != -1) {
			for (int i = first; i <= last; ++i) {
				runs.add(runTableModel.getRunFileAt(i).getFirst());
			}
		}

		configPanelModel.setSelectedRuns(runs);

		if (configData != null) {
			configData.loadRuns(runs);
		}
	}

	/**
	 * Return all the files from the given file path which match the currently
	 * selected file type.
	 * 
	 * @param filePath
	 *            the file path to search for files.
	 * @return the files found.
	 */
	public List<File> getFilesToLoad(String filePath) {
		final List<File> files = FileUtils.globMatch(filePath);
		return fileTypeModel.getSelectedElement().filterFiles(files);
	}

	/**
	 * Load the given file using the currently selected file type.
	 * 
	 * @param file
	 *            the file to load.
	 * @return the loaded file.
	 * @throws DatabaseException
	 *             if no connection to the database could be created.
	 * @throws IOException
	 *             if an error occurs when reading the files.
	 */
	public DataFile<?> loadFile(File file) throws DatabaseException,
			IOException {
		LOG.debug("Loading file " + file.getAbsolutePath());
		return fileTypeModel.getSelectedElement().loadFile(file);
	}

	/**
	 * Refresh the data being displayed in the file table using the given list
	 * of files.
	 * 
	 * @param dataFiles
	 *            the files to display in the table.
	 */
	public void updateFileTable(List<DataFile<?>> dataFiles) {
		runTableModel.clear();
		for (DataFile<?> dataFile : dataFiles) {
			runTableModel.addFile(dataFile.asRun());
		}
	}

	/**
	 * Open the file associated with the given row in an external editor such as
	 * KWrite (set in the properties file).
	 * 
	 * @param row
	 *            The row for which we will open the associated file
	 * @throws IOException
	 *             if an error occurs opening the file
	 */
	public void openFileForRow(int row) throws IOException {
		runTableModel.openFileForRow(row);
	}

	/**
	 * Clear the file table.
	 */
	public void clearFileTable() {
		runTableModel.clear();
	}

	/**
	 * Returns the config panel responsible for configuring the runs being
	 * loaded.
	 * 
	 * @return the configuration panel.
	 */
	public JPanel getConfigPanel() {
		return new ConfigPanel(configPanelModel);
	}

	/**
	 * Returns the file type specific config panel responsible for configuring
	 * the runs being loaded.
	 * 
	 * @return the file type specific configuration panel.
	 */
	public JPanel getRunConfigPanel() {
		return fileTypeModel.getSelectedElement().getRunConfigPanel();
	}

	/**
	 * Returns the file filter for the selected file type.
	 * 
	 * @return the file filter.
	 */
	public FileFilter getFileFilter() {
		return fileTypeModel.getSelectedElement().getFileFilter();
	}

	/**
	 * Returns the file name filter for the selected file type.
	 * 
	 * @return the file name filter.
	 */
	public FilenameFilter getFileNameFilter() {
		return fileTypeModel.getSelectedElement().getFileFilter();
	}

	/**
	 * Returns the table model used to display the files being loaded.
	 * 
	 * @return the file table model.
	 */
	public RunTableModel getFileTableModel() {
		return runTableModel;
	}

	/**
	 * Returns the combo box model used to display the different file types
	 * available.
	 * 
	 * @return the file type model.
	 */
	public ComboBoxModel<DataFileFactory> getFileTypeModel() {
		return fileTypeModel;
	}

	/**
	 * Load the configuration data for the given files in the config panel.
	 * 
	 * @param dataFiles
	 *            the files to load the configuration data for.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	public void loadConfigData(List<DataFile<?>> dataFiles)
			throws DatabaseException {
		configPanelModel.loadData(dataFiles);
	}

}