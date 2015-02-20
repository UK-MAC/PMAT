package uk.co.awe.pmat.gui.addresults;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.datafiles.DataFileFactory;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.models.AddResultsModel;
import uk.co.awe.pmat.gui.utils.DoubleClickListener;

/**
 * Class represents a View on the results and lets the user add results to a
 * database.
 * 
 * @author AWE Plc copyright 2013
 */
public class AddResultsView extends JPanel {

	private static final Logger LOG = LoggerFactory
			.getLogger(AddResultsView.class);

	private static final int ADD_COLUMN_WIDTH = 50;
	private static final int DIR_COLUMN_WIDTH = 400;

	private final AddResultsModel model;
	private final Configuration config;
	private final JFileChooser fcResults;

	private final JTable fileTable;

	private final Action doubleClickAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				final int tableRow = fileTable.getSelectedRow();
				final int modelRow = fileTable.convertRowIndexToModel(tableRow);
				model.openFileForRow(modelRow);
			} catch (IOException ex) {
				ExceptionDialog.showExceptionDialog(ex, "Failed to open file");
			}
		}
	};

	/**
	 * Create a new {@code AddResultsView}.
	 * 
	 * @param model
	 *            the model driving this view.
	 * @param config
	 *            the application configuration.
	 * @param workerListener
	 *            a listener to be informed about the state of background tasks
	 *            launched.
	 */
	public AddResultsView(final AddResultsModel model,
			final Configuration config,
			final PropertyChangeListener workerListener) {
		initComponents();

		this.model = model;
		this.config = config;

		fileTable = new RunTable(config);
		fileTable.setModel(model.getFileTableModel());
		fileTable.getColumnModel().getColumn(0).setPreferredWidth(
				DIR_COLUMN_WIDTH);
		fileTable.getColumnModel().getColumn(2).setMinWidth(ADD_COLUMN_WIDTH);
		fileTable.getColumnModel().getColumn(2).setMaxWidth(ADD_COLUMN_WIDTH);
		fileTable.addMouseListener(new DoubleClickListener(fileTable,
				doubleClickAction));
		fileTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							final int rowMin = fileTable.getSelectionModel()
									.getMinSelectionIndex();
							final int rowMax = fileTable.getSelectionModel()
									.getMaxSelectionIndex();
							final int first = fileTable
									.convertRowIndexToModel(rowMin);
							final int last = fileTable
									.convertRowIndexToModel(rowMax);
							loadConfigDataForRow(first, last);
						}
					}
				});
		fileTableScroll.setViewportView(fileTable);

		fcResults = new JFileChooser();
		fcResults.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fcResults.setFileFilter(model.getFileFilter());

		fileType.setModel(model.getFileTypeModel());

		pathTextbox.setText(config
				.getProperty(Configuration.Key.RESULTS_FILE_PATH));
		pathTextbox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				pathTextBoxChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				pathTextBoxChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				pathTextBoxChanged();
			}
		});

		setConfigPanel();

		final Action addRunsToDBAction = new AddRunsToDBAction(workerListener,
				model);
		addRunsToDBAction.setEnabled(false);
		addFilesToDbButton.setAction(addRunsToDBAction);

		final Action selectAllAction = new AbstractAction("Select All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileTable.getSelectionModel().setSelectionInterval(0,
						fileTable.getRowCount() - 1);
			}
		};
		selectAllAction.setEnabled(false);
		selectAllButton.setAction(selectAllAction);

		loadButton.setAction(new LoadFilesAction(workerListener, model,
				pathTextbox, addRunsToDBAction, selectAllAction, fileTable,
				loadMessage));
	}

	/**
	 * Load the results configuration data for the row with the given index.
	 * 
	 * @param rowIndex
	 *            the index of the row.
	 */
	private void loadConfigDataForRow(int first, int last) {
		try {
			model.loadConfigDataForRow(first, last);
		} catch (DatabaseException ex) {
			ExceptionDialog.showDatabaseExceptionDialog(ex);
		}
	}

	/**
	 * Display the general configuration panel for adding results, and also the
	 * file type specific configuration panel if one exists.
	 */
	private void setConfigPanel() {
		configPanel.removeAll();
		configPanel.add(model.getConfigPanel());
		if (model.getRunConfigPanel() != null) {
			configPanel.add(model.getRunConfigPanel());
		}
		validate();
	}

	/**
	 * Display a file selection box to allow the selection of the file path.
	 */
	private void browse() {
		File file = new File(pathTextbox.getText());
		fcResults.setSelectedFile(file);
		int result = fcResults.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			pathTextbox.setText(fcResults.getSelectedFile().getPath());
		}
	}

	/**
	 * Switch the type of files we are dealing with depending on which type is
	 * selected in the {@code fileType} combo box.
	 */
	private void changeFileType() {
		fcResults.setFileFilter(model.getFileFilter());
		model.clearFileTable();
		loadMessage.setText("");
		setConfigPanel();
	}

	/**
	 * Update the model with the text in the {@code pathTextbox}.
	 */
	private void pathTextBoxChanged() {
		config.setProperty(Configuration.Key.RESULTS_FILE_PATH, pathTextbox
				.getText());
		model.clearFileTable();
		model.clearConfigData();
		loadMessage.setText("");
		addFilesToDbButton.setEnabled(false);
	}

	/**
	 * This method is called from within the constructor to initialise the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		mainPanel = new javax.swing.JPanel();

		setLayout(new java.awt.BorderLayout());

		mainPanel.setLayout(new java.awt.GridBagLayout());

		titleLabel.setText("Add a Run or Set of Runs to the Database");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		mainPanel.add(titleLabel, gridBagConstraints);

		fileSelectionPanel.setBorder(javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		fileSelectionPanel.setLayout(new java.awt.GridBagLayout());

		fileType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fileTypeActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
		fileSelectionPanel.add(fileType, gridBagConstraints);

		browseButton.setText("Browse");
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				browseButtonActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		fileSelectionPanel.add(browseButton, gridBagConstraints);

		pathLabel.setLabelFor(pathTextbox);
		pathLabel.setText("Path to Load From");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		fileSelectionPanel.add(pathLabel, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		fileSelectionPanel.add(pathTextbox, gridBagConstraints);

		fileTypeLabel.setLabelFor(fileType);
		fileTypeLabel.setText("Results file type");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
		fileSelectionPanel.add(fileTypeLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		mainPanel.add(fileSelectionPanel, gridBagConstraints);

		configPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		configPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		mainPanel.add(configPanel, gridBagConstraints);

		loadPanel.setLayout(new java.awt.GridBagLayout());

		loadButton.setText("Load Files");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		loadPanel.add(loadButton, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		loadPanel.add(loadMessage, gridBagConstraints);

		selectAllButton.setText("Select All");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		loadPanel.add(selectAllButton, gridBagConstraints);

		addFilesToDbButton.setText("Add Files to DB");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		loadPanel.add(addFilesToDbButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		mainPanel.add(loadPanel, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		mainPanel.add(fileTableScroll, gridBagConstraints);

		add(mainPanel, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * The function that is called when the "Browse" button is clicked.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_browseButtonActionPerformed
		browse();
	}// GEN-LAST:event_browseButtonActionPerformed

	/**
	 * The function that is called when an item from the "File Type" drop down
	 * box is selected.
	 * 
	 * @param evt
	 *            the selection event.
	 */
	private void fileTypeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fileTypeActionPerformed
		changeFileType();
	}// GEN-LAST:event_fileTypeActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JButton addFilesToDbButton = new javax.swing.JButton();
	private final javax.swing.JButton browseButton = new javax.swing.JButton();
	private final javax.swing.JPanel configPanel = new javax.swing.JPanel();
	private final javax.swing.JPanel fileSelectionPanel = new javax.swing.JPanel();
	private final javax.swing.JScrollPane fileTableScroll = new javax.swing.JScrollPane();
	private final javax.swing.JComboBox<DataFileFactory> fileType = new javax.swing.JComboBox<DataFileFactory>();
	private final javax.swing.JLabel fileTypeLabel = new javax.swing.JLabel();
	private final javax.swing.JButton loadButton = new javax.swing.JButton();
	private final javax.swing.JLabel loadMessage = new javax.swing.JLabel();
	private final javax.swing.JPanel loadPanel = new javax.swing.JPanel();
	private javax.swing.JPanel mainPanel;
	private final javax.swing.JLabel pathLabel = new javax.swing.JLabel();
	private final javax.swing.JTextField pathTextbox = new javax.swing.JTextField();
	private final javax.swing.JButton selectAllButton = new javax.swing.JButton();
	private final javax.swing.JLabel titleLabel = new javax.swing.JLabel();
	// End of variables declaration//GEN-END:variables
}
