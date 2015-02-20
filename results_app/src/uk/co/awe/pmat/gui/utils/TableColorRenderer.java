package uk.co.awe.pmat.gui.utils;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import uk.co.awe.pmat.GraphColour;

/**
 * A cell renderer for use in a {@code JTable} which displays a
 * {@link GraphColour} object by displaying a block of the given colour.
 * 
 * @author AWE Plc copyright 2013
 */
public final class TableColorRenderer extends JLabel implements
		TableCellRenderer {

	private Border unselectedBorder = null;
	private Border selectedBorder = null;
	private boolean isBordered = true;

	/**
	 * Create a new {@code TableColorRenderer} instance with or without a
	 * border.
	 * 
	 * @param isBordered
	 *            Whether or not to display a border
	 */
	public TableColorRenderer(boolean isBordered) {
		this.isBordered = isBordered;
		setOpaque(true); // MUST do this for background to show up.
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object cell,
			boolean isSelected, boolean hasFocus, int row, int column) {

		GraphColour cellColor = (GraphColour) cell;
		setBackground(cellColor.getColor());

		if (isBordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getSelectionBackground());
				}
				setBorder(selectedBorder);
			} else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}

		Color color = cellColor.getColor();
		setToolTipText("RGB value: " + color.getRed() + ", " + color.getGreen()
				+ ", " + color.getBlue());
		return this;
	}
}
