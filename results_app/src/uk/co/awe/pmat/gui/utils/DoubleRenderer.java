package uk.co.awe.pmat.gui.utils;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class used to render a double in a table, giving it some precision.
 * 
 * @author AWE Plc copyright 2013
 */
public final class DoubleRenderer extends DefaultTableCellRenderer {

	/**
	 * Create a new {@code DoubleRenderer}.
	 */
	public DoubleRenderer() {
		super();
	}

	@Override
	protected void setValue(Object value) {
		setText(value.toString());
		setHorizontalAlignment(RIGHT);
	}
}
