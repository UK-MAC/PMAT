package uk.co.awe.pmat.gui.analysis;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.graph.GraphDataException;
import uk.co.awe.pmat.graph.PlottableLine;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.MainFrame;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.models.analysis.AnalysisModel;
import uk.co.awe.pmat.gui.models.analysis.SelectSeriesDialogModel;
import uk.co.awe.pmat.gui.utils.ThreadedAction;

/**
 * A dialog box used to show the series used in the plot, and to allow series to
 * be added/removed, or individual series groups to be removed. Also allows for
 * the setting of line style for each of the series groups.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SelectSeriesDialog extends JDialog {

	private final SelectSeriesDialogModel dialogModel;

	private int selectedColumn;

	/**
	 * The action to perform to remove the series group corresponding to the
	 * selected row.
	 */
	private final Action removeAction = new AbstractAction("Remove") {
		@Override
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows = seriesTable.getSelectedRows();

			if (selectedRows != null) {
				int[] modelRows = new int[selectedRows.length];
				for (int rowIdx = 0; rowIdx < selectedRows.length; ++rowIdx) {
					modelRows[rowIdx] = seriesTable
							.convertRowIndexToModel(selectedRows[rowIdx]);
				}

				try {
					dialogModel.removeSeriesLines(modelRows);
				} catch (DatabaseException ex) {
					ExceptionDialog.showDatabaseExceptionDialog(ex);
				} catch (GraphDataException ex) {
					ExceptionDialog.showExceptionDialog(ex, "");
				}
			} else {
				JOptionPane.showMessageDialog(SelectSeriesDialog.this,
						"No series groups selected");
			}
		}
	};

	/**
	 * The action to perform to remove all the series groups for a selected
	 * series column.
	 */
	private final Action removeSeriesAction = new AbstractAction(
			"Remove Series") {
		@Override
		public void actionPerformed(ActionEvent e) {
			int modelIdx = seriesTable
					.convertColumnIndexToModel(selectedColumn);
			try {
				dialogModel.removeSeries(modelIdx);
			} catch (DatabaseException ex) {
				ExceptionDialog.showDatabaseExceptionDialog(ex);
			} catch (GraphDataException ex) {
				ExceptionDialog.showExceptionDialog(ex,
						"Error updating graph data");
			} catch (DerivedDataException ex) {
				ExceptionDialog.showExceptionDialog(ex,
						"Error updating graph data");
			}
		}
	};

	/**
	 * The action to perform to close the dialog.
	 */
	private final Action closeAction = new AbstractAction("Close") {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dialogModel.updateAnalysis();
			dispose();
		}
	};

	/**
	 * Create a new dialog which can be used to modify, add or remove series
	 * groups and lines from the graph data set.
	 * 
	 * @param frame
	 *            the main window frame which will be blocked by this dialog.
	 * @param title
	 *            the caption bar title.
	 * @param analysisModel
	 *            the underlying dialog model.
	 * @param graphData
	 *            the data set for which we are modifying the series.
	 * @param taskListener
	 *            a listener to be informed of any background tasks.
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 */
	private SelectSeriesDialog(JFrame frame, String title,
			AnalysisModel analysisModel, EventHub eventHub,
			GraphData graphData, PropertyChangeListener taskListener)
			throws DatabaseException {

		super(frame, title, JDialog.ModalityType.DOCUMENT_MODAL);
		initComponents();

		dialogModel = new SelectSeriesDialogModel(analysisModel, eventHub,
				seriesTable.getColumnModel(), seriesTable.getSelectionModel(),
				graphData);

		seriesTable.setModel(dialogModel.getSeriesTableModel());
		seriesTable.setDefaultRenderer(Object.class,
				new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object obj, boolean isSelected,
							boolean hasFocus, int row, int column) {
						if (obj instanceof PlottableLine) {
							obj = ((PlottableLine) obj).getName();
						}
						return super.getTableCellRendererComponent(table, obj,
								isSelected, hasFocus, row, column);
					}
				});

		seriesSelection.setModel(dialogModel.getSeriesSelectionModel());

		seriesTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				seriesTableColumnHeaderClicked(e);
			}
		});

		addSeriesButton.setAction(new ThreadedAction("Add", taskListener) {
			@Override
			public void actionPerformedInBackground() throws Exception {
				dialogModel.addSeries();
			}

			@Override
			public void done() {
				dialogModel.updateSeriesTableModel();
			}
		});

		removeButton.setAction(removeAction);
		closeButton.setAction(closeAction);
		removeSeries.setAction(removeSeriesAction);
	}

	/**
	 * Show a pop-up menu allowing for the removal of series when the table
	 * header corresponding to that series is right-clicked.
	 * 
	 * @param evt
	 *            the mouse event corresponding to table header being clicked.
	 */
	private void seriesTableColumnHeaderClicked(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON3) {
			selectedColumn = seriesTable.columnAtPoint(evt.getPoint());
			int lineTypeIdx = seriesTable.convertColumnIndexToView(dialogModel
					.getLineTypeIndex());
			int lineColourIdx = seriesTable
					.convertColumnIndexToView(dialogModel.getLineColourIndex());

			if (selectedColumn != lineTypeIdx
					&& selectedColumn != lineColourIdx) {

				columnPopupMenu.show(this, (int) evt.getPoint().getX(),
						(int) evt.getPoint().getY());
			}
		}
	}

	/**
	 * Create and display a dialog box to add/remove series from a set of plot
	 * data and to allow the setting of line colours & line styles for the
	 * series groups.
	 * 
	 * @param title
	 *            the caption bar title to display.
	 * @param analysisModel
	 *            the analysis model from which to get series information.
	 * @param graphData
	 *            the data set for which we are modifying the series.
	 * @param taskListener
	 *            a {@code PropertyChangeListener} to be informed about the
	 *            state of any {@code SwingWorker} tasks launched.
	 */
	public static void showSeriesDialog(String title,
			AnalysisModel analysisModel, EventHub eventHub,
			GraphData graphData, PropertyChangeListener taskListener) {

		JFrame frame = MainFrame.getRootFrame();
		SelectSeriesDialog diag = null;

		try {
			diag = new SelectSeriesDialog(frame, title, analysisModel,
					eventHub, graphData, taskListener);
		} catch (DatabaseException ex) {
			ExceptionDialog.showDatabaseExceptionDialog(ex);
			return;
		}
		diag.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Move to the centre of the main frame.
		diag.setLocation(frame.getX() + frame.getWidth() / 2 - diag.getWidth()
				/ 2, frame.getY() + frame.getHeight() / 2 - diag.getHeight()
				/ 2);

		diag.setVisible(true);
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

		removeSeries.setText("Remove Series");
		columnPopupMenu.add(removeSeries);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		seriesTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null }, { null, null, null },
						{ null, null, null }, { null, null, null } },
				new String[] { "Series Group", "Colour", "Line Type" }));
		seriesScroll.setViewportView(seriesTable);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(seriesScroll, gridBagConstraints);

		buttonPanel.setLayout(new java.awt.GridBagLayout());

		closeButton.setText("Close");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(closeButton, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(seriesSelection, gridBagConstraints);

		addSeriesButton.setText("Add");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		buttonPanel.add(addSeriesButton, gridBagConstraints);

		removeButton.setText("Remove");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		buttonPanel.add(removeButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(buttonPanel, gridBagConstraints);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JButton addSeriesButton = new javax.swing.JButton();
	private final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	private final javax.swing.JButton closeButton = new javax.swing.JButton();
	private final javax.swing.JPopupMenu columnPopupMenu = new javax.swing.JPopupMenu();
	private final javax.swing.JButton removeButton = new javax.swing.JButton();
	private final javax.swing.JMenuItem removeSeries = new javax.swing.JMenuItem();
	private final javax.swing.JScrollPane seriesScroll = new javax.swing.JScrollPane();
	private final javax.swing.JComboBox<Series> seriesSelection = new javax.swing.JComboBox<Series>();
	private final javax.swing.JTable seriesTable = new javax.swing.JTable();
	// End of variables declaration//GEN-END:variables
}
