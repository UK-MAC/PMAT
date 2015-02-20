package uk.co.awe.pmat.gui;

import uk.co.awe.pmat.gui.utils.DoubleClickListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.gui.utils.ThreadedAction;
import uk.co.awe.pmat.gui.models.analysis.ViewResultDialogModel;
import uk.co.awe.pmat.gui.models.ViewResultsModel;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.gui.utils.DisplayTable;

/**
 * A view to display all the results stored in the database and allow viewing of
 * a particular result.
 * 
 * @author AWE Plc copyright 2013
 */
public class ViewResultsView extends JPanel {

	private static final Logger LOG = LoggerFactory
			.getLogger(ViewResultsView.class);

	private static final String EXPORT_EXTENSION = Constants.Export.EXTENSION;
	private static final String EXPORT_DESCRIPTION = Constants.Export.DESCRIPTION;

	private final ViewResultsModel viewResultsModel;
	private final JFileChooser fileChooser;

	private DisplayTableModel tableModel;

	/**
	 * Create a new {@code ViewResultsView}.
	 * 
	 * @param viewResultsModel
	 *            the model driving this view.
	 * @param workerListener
	 *            a listener to be informed about any worker tasks launched.
	 */
	public ViewResultsView(final ViewResultsModel viewResultsModel,
			final PropertyChangeListener workerListener) {

		initComponents();

		this.viewResultsModel = viewResultsModel;

		populateDisplayTable();

		fileChooser = new JFileChooser();
		fileChooser.removeChoosableFileFilter(fileChooser
				.getAcceptAllFileFilter());
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return (f.isDirectory() || f.getName().toLowerCase().endsWith(
						EXPORT_EXTENSION));
			}

			@Override
			public String getDescription() {
				return EXPORT_DESCRIPTION;
			}
		});

		displayTable.addMouseListener(new DoubleClickListener(displayTable,
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						final int row = displayTable.getSelectedRow();
						final int modelRow = displayTable
								.convertRowIndexToModel(row);
						final Long id = tableModel.getId(modelRow);
						final ViewResultDialogModel dialogModel = viewResultsModel
								.getViewResultDialogModel(id);
						ViewResultDialog.showResultDialog(ViewResultsView.this,
								dialogModel, workerListener);
					}
				}));

		removeButton.setAction(new ThreadedAction("Remove", workerListener) {
			@Override
			public void actionPerformedInBackground() throws Exception {
				removeSelectedRows();
			}

			@Override
			public void done() {
				populateDisplayTable();
			}
		});

		exportButton.setAction(new ThreadedAction("Export", workerListener) {
			@Override
			public void actionPerformedInBackground() throws Exception {
				exportSelectedRows();
			}
		});
	}

	/**
	 * Reload the results and populate the table with them.
	 */
	private void populateDisplayTable() {
		try {
			tableModel = viewResultsModel.getResultsTableModel();
			displayTable.setModel(tableModel);
		} catch (DatabaseException ex) {
			ExceptionDialog.showDatabaseExceptionDialog(ex);
		}
	}

	/**
	 * Remove the currently selected rows from the database.
	 */
	private void removeSelectedRows() {
		int[] selectedRows = displayTable.getSelectedRows();

		String msg = "Are you sure you want to remove " + selectedRows.length
				+ " rows?";
		int result = JOptionPane.showConfirmDialog(this, msg, "Confirm remove",
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			for (int row : selectedRows) {
				try {
					int modelRow = displayTable.convertRowIndexToModel(row);
					Long id = tableModel.getId(modelRow);
					viewResultsModel.deleteRun(id);
				} catch (DatabaseException ex) {
					ExceptionDialog.showDatabaseExceptionDialog(ex);
				} catch (IOException ex) {
					ExceptionDialog.showExceptionDialog(ex,
							"Error removing run file");
				}
			}
		}
	}

	/**
	 * Export the selected rows to a file chosen via a file selection dialog.
	 */
	public void exportSelectedRows() {
        int[] selectedRows = displayTable.getSelectedRows();

        int result = fileChooser.showDialog(this, "Export");

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(EXPORT_EXTENSION)) {
                file = new File(file.getAbsolutePath() + EXPORT_EXTENSION);
            }

            if (file.exists()) {
                result = JOptionPane.showConfirmDialog(this,
                        "File " + file.getAbsolutePath()
                        + " already exists. Do you want to overwrite?",
                        "Confirm overwrite",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            final List<Long> ids = new ArrayList<>();
            for (int row : selectedRows) {
                int modelRow = displayTable.convertRowIndexToModel(row);
                ids.add(tableModel.getId(modelRow));
            }

            try {
                viewResultsModel.exportRowsToFile(file, ids);
                JOptionPane.showMessageDialog(this,
                        "Runs succesfully exported to the file \n"
                        + file.getAbsolutePath());
            } catch (IOException ex) {
                ExceptionDialog.showExceptionDialog(ex, "Failed to export runs");
            } catch (DatabaseException ex) {
                ExceptionDialog.showDatabaseExceptionDialog(ex);
            }
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

		setMinimumSize(new java.awt.Dimension(422, 223));
		setPreferredSize(new java.awt.Dimension(422, 223));
		setLayout(new java.awt.GridBagLayout());

		buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5,
				5, 5));
		buttonPanel.setName("buttonPanel"); // NOI18N
		buttonPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

		exportButton.setText("Export Run");
		exportButton.setName("exportButton"); // NOI18N
		buttonPanel.add(exportButton);

		removeButton.setText("Remove Run");
		removeButton.setName("removeButton"); // NOI18N
		buttonPanel.add(removeButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		add(buttonPanel, gridBagConstraints);

		displayScroll.setName("displayScroll"); // NOI18N

		displayTable.setAutoCreateRowSorter(true);
		displayTable.setName("displayTable"); // NOI18N
		displayScroll.setViewportView(displayTable);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(displayScroll, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	private final javax.swing.JScrollPane displayScroll = new javax.swing.JScrollPane();
	private final javax.swing.JTable displayTable = new DisplayTable();
	private final javax.swing.JButton exportButton = new javax.swing.JButton();
	private final javax.swing.JButton removeButton = new javax.swing.JButton();
	// End of variables declaration//GEN-END:variables

}
