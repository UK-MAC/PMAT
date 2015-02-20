package uk.co.awe.pmat.gui.addresults;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.gui.utils.RunTableModel;

/**
 * An extension of JTable to add row colouring for runs that have succeeded or
 * failed and tool tip text.
 * 
 * @author AWE Plc copyright 2013
 */
public final class RunTable extends JTable {

	private Configuration config;

	/**
	 * Create a new {@code RunTable}.
	 * 
	 * @param config
	 *            the application configuration.
	 */
	public RunTable(Configuration config) {
		this.config = config;

		this.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent evt) {
				if (evt.getKeyChar() == ' ') {
					toggleSelectedRows();
				}
			}
		});
	}

	@Override
	public RunTableModel getModel() {
		return (RunTableModel) super.getModel();
	}

	@Override
	protected TableModel createDefaultDataModel() {
		return new RunTableModel(config);
	}

	/**
	 * Display in a ToolTip the file and any exceptions that have a occurred for
	 * the row we are hovering over.
	 * 
	 * @param event
	 *            The mouse event that triggered this method
	 * @return The text to display in the ToolTip
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		int row = rowAtPoint(event.getPoint());
		if (row == -1) {
			return null;
		}
		StringBuilder toolTipText = new StringBuilder("<html>");
		toolTipText.append(getModel().getRunFileAt(row).getSecond()
				.getAbsolutePath());
		Throwable ex = getModel().getRowException(row);
		toolTipText.append("<br/>");
		while (ex != null) {
			toolTipText.append("<br/>").append(ex.getClass().getSimpleName())
					.append(": ").append(ex.getMessage());
			ex = ex.getCause();
		}
		return toolTipText.toString().replace("/", "&#47;") + "</html>";
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component comp = super.prepareRenderer(renderer, row, column);
		if (!isRowSelected(row)) {
			comp.setBackground(getModel().getRowColor(row));
		}
		return comp;
	}

	/**
	 * Toggle the "add file" status of the selected rows.
	 */
	private void toggleSelectedRows() {
		for (int row : getSelectedRows()) {
			getModel().toggleRow(row);
		}
	}
}
