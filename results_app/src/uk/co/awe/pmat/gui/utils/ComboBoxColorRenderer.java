package uk.co.awe.pmat.gui.utils;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import uk.co.awe.pmat.GraphColour;

/**
 * An implementation of the Swing ListCellRenderer interface which is used to
 * render a colour in a table as a cell of that colour.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ComboBoxColorRenderer extends JLabel implements
		ListCellRenderer<GraphColour> {

	/**
	 * Create a new {@code ComboBoxColorRenderer} instance.
	 */
	public ComboBoxColorRenderer() {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends GraphColour> list, GraphColour cellColor,
			int index, boolean isSelected, boolean cellHasFocus) {

		if (cellColor != null) {
			setBackground(cellColor.getColor());
			setForeground(cellColor.getColor());

			setText(cellColor.toString());
		}

		return this;
	}

}
