package uk.co.awe.pmat.gui.analysis;

import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.models.analysis.AnalysesDisplayDialogModel;
import uk.co.awe.pmat.gui.models.analysis.AnalysisModel;
import uk.co.awe.pmat.utils.FileUtils;
import uk.co.awe.pmat.utils.RegExpFilenameFilter;

/**
 * A dialog to display all the saved analyses and to allow them to be loaded,
 * exported or deleted.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AnalysesDisplayDialog extends JDialog {

	private static final Logger LOG = LoggerFactory
			.getLogger(AnalysesDisplayDialog.class);

	private static final String DEFAULT_EXPORT_FILENAME = Constants.Analysis.DEFAULT_FILENAME;
	private static final String DEFAULT_EXPORT_EXTENSION = Constants.Analysis.EXTENSION;
	private static final String ANALYSIS_DIALOG_TITLE = Constants.Application.ANALYSIS_DIALOG_TITLE;
	private static final String EXPORT_FILE_DESCRIPTION = Constants.Analysis.DESCRIPTION;

	private final AnalysesDisplayDialogModel analysesDisplayDialogModel;

	private Callable<AnalysisModel> loadAnalysisTask;

	/**
	 * Create a new {@code AnalysesDisplayDialog}.
	 * 
	 * @param parent
	 *            the dialog parent.
	 * @param model
	 *            the model driving this dialog.
	 */
	private AnalysesDisplayDialog(Window parent,
			AnalysesDisplayDialogModel model) {
		super(parent, ANALYSIS_DIALOG_TITLE, DEFAULT_MODALITY_TYPE);
		initComponents();

		setLocation(parent.getX() + parent.getWidth() / 2 - getWidth() / 2,
				parent.getY() + parent.getHeight() / 2 - getHeight() / 2);

		analysesDisplayDialogModel = model;
		analysesDisplayTable.setModel(model.getAnalysesDisplayTableModel());

		for (FileFilter f : exportFileChooser.getChoosableFileFilters()) {
			exportFileChooser.removeChoosableFileFilter(f);
		}
		exportFileChooser
				.setFileFilter(new RegExpFilenameFilter(EXPORT_FILE_DESCRIPTION
						+ " (" + DEFAULT_EXPORT_EXTENSION + ")", ".*\\"
						+ DEFAULT_EXPORT_EXTENSION));

		loadAnalysisTask = null;
		currentUserOnly.setSelected(analysesDisplayDialogModel
				.getCurrentUserOnly());
	}

	/**
	 * Update the model with the value of the "Current User Only" check box.
	 */
	private void currentUserOnlySelected() {
		try {
			analysesDisplayDialogModel.setCurrentUserOnly(currentUserOnly
					.isSelected());
		} catch (DatabaseException ex) {
			ExceptionDialog.showDatabaseExceptionDialog(ex);
		}
	}

	/**
	 * Show the {@code AnalysisDisplayDialog}.
	 * 
	 * @param component
	 *            the component the dialog will be centred on.
	 * @param config
	 *            the application configuration.
	 * @param taskListener
	 *            a listener to be kept informed of the state of any {@code
	 *            SwingWorker}s launched.
	 * @return the loaded {@code AnalysisModel} if the load button was clicked,
	 *         otherwise {@code null}.
	 * @throws DatabaseException
	 *             if a problem was encountered communicating with the database.
	 */
	public static Callable<AnalysisModel> showAnalysesDisplayDialog(
			Component component, Configuration config,
			PropertyChangeListener taskListener) throws DatabaseException {

		AnalysisModel analysisModel = new AnalysisModel(config, taskListener);
		AnalysesDisplayDialog dialog = new AnalysesDisplayDialog(SwingUtilities
				.getWindowAncestor(component), new AnalysesDisplayDialogModel(
				analysisModel));
		dialog.setVisible(true);

		return dialog.loadAnalysisTask;
	}

	/**
	 * Delete the analysis corresponding the currently selected row from the
	 * database.
	 */
	private void deleteSelectedAnalysis() {
		int selectedRow = analysesDisplayTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Now analysis selected");
		} else {
			int modelRow = analysesDisplayTable
					.convertRowIndexToModel(selectedRow);
			try {
				analysesDisplayDialogModel.deleteSelectedAnalysis(modelRow);
			} catch (DatabaseException ex) {
				ExceptionDialog.showDatabaseExceptionDialog(ex);
			}
		}
	}

	/**
	 * Export the analysis corresponding the currently selected row to an
	 * analysis export file.
	 */
	private void exportSelectedAnalysis() {
		if (analysesDisplayTable.getSelectedRow() == -1) {
			JOptionPane.showMessageDialog(this, "No row selected");
		} else {
			int rowIdx = analysesDisplayTable
					.convertRowIndexToModel(analysesDisplayTable
							.getSelectedRow());
			try {
				int result = exportFileChooser.showDialog(this,
						"Export Analysis");
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = exportFileChooser.getSelectedFile();
					file = FileUtils.getSaveAsFile(file,
							DEFAULT_EXPORT_FILENAME, DEFAULT_EXPORT_EXTENSION);
					analysesDisplayDialogModel.exportSelectedRow(rowIdx, file);
					JOptionPane.showMessageDialog(this,
							"Analyses successfully exported to "
									+ file.getAbsolutePath());
				}
			} catch (DatabaseException ex) {
				ExceptionDialog.showDatabaseExceptionDialog(ex);
			} catch (IOException ex) {
				ExceptionDialog.showExceptionDialog(ex,
						"Failed to export analysis");
			}
		}
	}

	/**
	 * Load the analysis corresponding the currently selected row from the
	 * database.
	 */
	private void loadSelectedAnalysis() {
		if (analysesDisplayTable.getSelectedRow() == -1) {
			JOptionPane.showMessageDialog(this, "No row selected");
		} else {
			final int rowIdx = analysesDisplayTable
					.convertRowIndexToModel(analysesDisplayTable
							.getSelectedRow());
			loadAnalysisTask = new Callable<AnalysisModel>() {
				@Override
				public AnalysisModel call() throws Exception {
					return analysesDisplayDialogModel.loadSelectedRow(rowIdx);
				}
			};
			setVisible(false);
		}
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

		exportFileChooser = new javax.swing.JFileChooser();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		analysesScroll.setViewportView(analysesDisplayTable);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(analysesScroll, gridBagConstraints);

		bottomPanel.setLayout(new java.awt.GridBagLayout());

		currentUserOnly.setText("Current User Only");
		currentUserOnly.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				currentUserOnlyActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		bottomPanel.add(currentUserOnly, gridBagConstraints);

		buttonPanel.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

		loadButton.setText("Load");
		loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadButtonActionPerformed(evt);
			}
		});
		buttonPanel.add(loadButton);

		exportButton.setText("Export");
		exportButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportButtonActionPerformed(evt);
			}
		});
		buttonPanel.add(exportButton);

		delete.setText("Delete");
		delete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteActionPerformed(evt);
			}
		});
		buttonPanel.add(delete);

		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});
		buttonPanel.add(closeButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		bottomPanel.add(buttonPanel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(bottomPanel, gridBagConstraints);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Action to perform on load button click.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_loadButtonActionPerformed
		loadSelectedAnalysis();
	}// GEN-LAST:event_loadButtonActionPerformed

	/**
	 * Action to perform on close button click.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_closeButtonActionPerformed

	/**
	 * Action to perform on export button click.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportButtonActionPerformed
		exportSelectedAnalysis();
	}// GEN-LAST:event_exportButtonActionPerformed

	/**
	 * Action to perform on delete button click.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void deleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_deleteActionPerformed
		deleteSelectedAnalysis();
	}// GEN-LAST:event_deleteActionPerformed

	/**
	 * Action to perform on current user only check box click.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void currentUserOnlyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_currentUserOnlyActionPerformed
		currentUserOnlySelected();
	}// GEN-LAST:event_currentUserOnlyActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JTable analysesDisplayTable = new javax.swing.JTable();
	private final javax.swing.JScrollPane analysesScroll = new javax.swing.JScrollPane();
	private final javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
	private final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	private final javax.swing.JButton closeButton = new javax.swing.JButton();
	private final javax.swing.JCheckBox currentUserOnly = new javax.swing.JCheckBox();
	private final javax.swing.JButton delete = new javax.swing.JButton();
	private final javax.swing.JButton exportButton = new javax.swing.JButton();
	private javax.swing.JFileChooser exportFileChooser;
	private final javax.swing.JButton loadButton = new javax.swing.JButton();
	// End of variables declaration//GEN-END:variables

}
