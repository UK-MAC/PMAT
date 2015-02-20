package uk.co.awe.pmat.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import uk.co.awe.pmat.db.MetaData;

/**
 * An extension of the {@link JTable} class which properly handles the display
 * of objects that implement the {@code DatabaseObject} interface.
 * 
 * @author AWE Plc copyright 2013
 */
public final class DisplayTable extends JTable {

	private DisplayTableModel tableModel;

	/**
	 * Create a new {@code DisplayTable}.
	 */
	public DisplayTable() {
		setDefaultRenderer(boolean.class, booleanCellRenderer);
		setDefaultRenderer(Boolean.class, booleanCellRenderer);
	}

	/**
	 * Create a new {@code DisplayTable}.
	 * 
	 * @param tableModel
	 *            the model driving this table.
	 */
	public DisplayTable(DisplayTableModel tableModel) {
		super();
		this.tableModel = tableModel;
	}

	@Override
	public void setModel(TableModel model) {
		if (model instanceof DisplayTableModel) {
			this.tableModel = (DisplayTableModel) model;
		} else {
			this.tableModel = null;
		}
		super.setModel(model);
	}

	private final TableCellRenderer dbObjCellRenderer = new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			final MetaData dbObj = (MetaData) value;
			return super.getTableCellRendererComponent(table, dbObj
					.displayName(), isSelected, hasFocus, row, column);
		}
	};

	private final TableCellRenderer booleanCellRenderer = new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setSelected((Boolean) value);
			checkBox.setHorizontalAlignment(JCheckBox.CENTER);
			checkBox.setOpaque(true);
			checkBox.setBackground(getRowBackground(row));
			return checkBox;
		}
	};

	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		if (columnClass == null) {
			return null;
		} else {
			Object renderer = defaultRenderersByColumnClass.get(columnClass);
			if (renderer != null) {
				return (TableCellRenderer) renderer;
			} else if (MetaData.class.isAssignableFrom(columnClass)) {
				return dbObjCellRenderer;
			} else {
				return getDefaultRenderer(columnClass.getSuperclass());
			}
		}
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component comp = super.prepareRenderer(renderer, row, column);
		comp.setBackground(getRowBackground(row));
		return comp;
	}

	/**
	 * Return the background colour that the row with the given index should be
	 * displayed with.
	 * 
	 * @param row
	 *            the row index.
	 * @return the background colour.
	 */
	private Color getRowBackground(int row) {
		if (isRowSelected(row)
				&& tableModel.getRowState(row) == DisplayTableModel.State.DEFAULT) {
			return getSelectionBackground();
		} else {
			return tableModel.getRowColor(row);
		}
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

		DisplayTableModel.State state = tableModel.getRowState(row);
		switch (state) {
		case DEFAULT:
			return null;
		case SUCCESS:
			toolTipText.append(tableModel.getRowInfo(row));
			break;
		case FAILED:
			Throwable ex = tableModel.getRowException(row);
			toolTipText.append("<br/>");
			while (ex != null) {
				toolTipText.append("<br/>").append(
						ex.getClass().getSimpleName()).append(": ").append(
						ex.getMessage());
				ex = ex.getCause();
			}
			break;
		default:
			throw new IllegalStateException("Unknown state " + state);
		}

		return toolTipText.toString().replace("/", "&#47;") + "</html>";
	}
}