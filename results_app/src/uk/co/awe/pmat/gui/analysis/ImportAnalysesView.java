package uk.co.awe.pmat.gui.analysis;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.models.analysis.ImportAnalysesModel;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;
import uk.co.awe.pmat.utils.RegExpFilenameFilter;

/**
 * A panel for the import of PMAT export files.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ImportAnalysesView extends JPanel {

	private static final Logger LOG = LoggerFactory
			.getLogger(ImportAnalysesView.class);

	private static final String EXPORT_EXTENSION = Constants.Analysis.EXTENSION;
	private static final String EXPORT_DESCRIPTION = Constants.Analysis.DESCRIPTION;

	private final DisplayTableModel tableModel;
	private final ImportAnalysesModel importModel;
	private final JFileChooser fileChooser;
	private final Configuration config;
	private final PropertyChangeListener workerListener;

	/**
	 * Create a new {@code ImportAnalysesView}.
	 * 
	 * @param importModel
	 *            the model driving this panel.
	 * @param config
	 *            the application configuration.
	 * @param workerListener
	 *            a listener to be informed of any background tasks launched.
	 */
	public ImportAnalysesView(ImportAnalysesModel importModel,
			Configuration config, PropertyChangeListener workerListener) {

		initComponents();

		this.config = config;
		this.importModel = importModel;
		this.workerListener = workerListener;

		fileChooser = new JFileChooser();
		for (FileFilter f : fileChooser.getChoosableFileFilters()) {
			fileChooser.removeChoosableFileFilter(f);
		}
		fileChooser.setFileFilter(new RegExpFilenameFilter(EXPORT_DESCRIPTION
				+ " (" + EXPORT_EXTENSION + ")", ".*\\" + EXPORT_EXTENSION));

		importFileField.setText(config
				.getProperty(Configuration.Key.ANALYSIS_FILE_PATH));
		importFileField.getDocument().addDocumentListener(
				new DocumentListener() {
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
		pathTextBoxChanged();

		tableModel = importModel.getTableModel();
		runsTable.setModel(tableModel);
		setButtonActions();
	}

	/**
	 * Set the actions to be performed by the various buttons.
	 */
	private void setButtonActions() {
		browseButton.setAction(new AbstractAction("Browse") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showDialog(ImportAnalysesView.this,
						"Import");
				if (result == JFileChooser.APPROVE_OPTION) {
					importFileField.setText(fileChooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});
		importButton.setAction(new AbstractAction("Import") {
			@Override
			public void actionPerformed(ActionEvent e) {
				importAnalyses();
			}
		});
		loadButton.setAction(new AbstractAction("Load") {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadAnalyses();
			}
		});
		importButton.getAction().setEnabled(false);
		loadButton.getAction().setEnabled(false);
		pathTextBoxChanged();
	}

	/**
	 * When the given file path is changed, update the stored import file path
	 * and detected whether we can import the given file.
	 */
	private void pathTextBoxChanged() {
		config.setProperty(Configuration.Key.ANALYSIS_FILE_PATH,
				importFileField.getText());
		final File importFile = new File(importFileField.getText());
		loadButton.setEnabled(importFile.exists());
	}

	/**
	 * Load the analyses stored in the given file.
	 */
	private void loadAnalyses() {
		final String text = importFileField.getText();

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					importModel.loadAnalysesFromFile(new File(text));
				} catch (IOException ex) {
					ExceptionDialog.showExceptionDialog(ex,
							"Unable to load file.");
				}
				return null;
			}

			@Override
			protected void done() {
				importModel.updateTable();
				importButton.setEnabled(true);
			}
		};
		worker.addPropertyChangeListener(workerListener);
		worker.execute();
	}

	/**
	 * Import the loaded analyses into the database.
	 */
	private void importAnalyses() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				try {
					importModel.saveAnalysesIntoDB();
				} catch (Throwable ex) {
					LOG.error("Failed to import analyses", ex);
				}
				return null;
			}
		};
		worker.addPropertyChangeListener(workerListener);
		worker.execute();
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

		setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
		setLayout(new java.awt.BorderLayout());

		runsScroll.setViewportView(runsTable);

		add(runsScroll, java.awt.BorderLayout.CENTER);

		buttonPanel.setLayout(new java.awt.GridBagLayout());

		buttonSpacer.setMaximumSize(new java.awt.Dimension(0, 0));
		buttonSpacer.setMinimumSize(new java.awt.Dimension(0, 0));
		buttonSpacer.setPreferredSize(new java.awt.Dimension(0, 0));

		javax.swing.GroupLayout buttonSpacerLayout = new javax.swing.GroupLayout(
				buttonSpacer);
		buttonSpacer.setLayout(buttonSpacerLayout);
		buttonSpacerLayout.setHorizontalGroup(buttonSpacerLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 276, Short.MAX_VALUE));
		buttonSpacerLayout.setVerticalGroup(buttonSpacerLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 35, Short.MAX_VALUE));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		buttonPanel.add(buttonSpacer, gridBagConstraints);

		importButton.setText("Import");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(importButton, gridBagConstraints);

		loadButton.setText("Load");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(loadButton, gridBagConstraints);

		add(buttonPanel, java.awt.BorderLayout.PAGE_END);

		importFilePanel.setLayout(new java.awt.GridBagLayout());

		importFileLabel.setLabelFor(importFileField);
		importFileLabel.setText("Import File");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		importFilePanel.add(importFileLabel, gridBagConstraints);

		importFileField.setText("Select File...");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		importFilePanel.add(importFileField, gridBagConstraints);

		browseButton.setText("Browse");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		importFilePanel.add(browseButton, gridBagConstraints);

		add(importFilePanel, java.awt.BorderLayout.PAGE_START);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JButton browseButton = new javax.swing.JButton();
	private final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	private final javax.swing.JPanel buttonSpacer = new javax.swing.JPanel();
	private final javax.swing.JButton importButton = new javax.swing.JButton();
	private final javax.swing.JTextField importFileField = new javax.swing.JTextField();
	private final javax.swing.JLabel importFileLabel = new javax.swing.JLabel();
	private final javax.swing.JPanel importFilePanel = new javax.swing.JPanel();
	private final javax.swing.JButton loadButton = new javax.swing.JButton();
	private final javax.swing.JScrollPane runsScroll = new javax.swing.JScrollPane();
	private final javax.swing.JTable runsTable = new javax.swing.JTable();
	// End of variables declaration//GEN-END:variables

}
