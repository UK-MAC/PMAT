package uk.co.awe.pmat.gui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A table cell editor and renderer to be used in a JTable to display an integer
 * with buttons for incrementing and decrementing.
 * 
 * @author AWE Plc copyright 2013
 */
public final class EditableNumberTableCell extends AbstractCellEditor implements
		TableCellEditor, TableCellRenderer {

	private final JLabel editorLabel;
	private final int minValue;
	private final int maxValue;
	private final JPanel editorComponent;
	private final JPanel rendererComponent;
	private final JLabel rendererLabel;

	private int editorValue;

	/**
	 * Create a new {@code EditableNumberTableCell} which will allow numbers to
	 * be set between the minimum and maximum values given.
	 * 
	 * @param minValue
	 *            The minimum value that the number can be changed to
	 * @param maxValue
	 *            The maximum value that the number can be changed to
	 */
	public EditableNumberTableCell(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;

		editorLabel = new JLabel();
		editorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rendererLabel = new JLabel();
		rendererLabel.setHorizontalAlignment(SwingConstants.CENTER);
		editorValue = minValue;

		// Add the buttons without actions to the renderer panel.

		JButton incButton = new JButton("+");
		JButton decButton = new JButton("-");

		rendererComponent = new JPanel();
		rendererComponent.setLayout(new BorderLayout());
		rendererComponent.add(this.rendererLabel, BorderLayout.CENTER);
		rendererComponent.add(decButton, BorderLayout.WEST);
		rendererComponent.add(incButton, BorderLayout.EAST);

		// And add the buttons with actions to the editor panel.

		incButton = new JButton("+");
		decButton = new JButton("-");

		incButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				incrementValue();
				updateEditorLabel();
				fireEditingStopped();
			}
		});
		decButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decrementValue();
				updateEditorLabel();
				fireEditingStopped();
			}
		});

		editorComponent = new JPanel();
		editorComponent.setLayout(new BorderLayout());
		editorComponent.add(this.editorLabel, BorderLayout.CENTER);
		editorComponent.add(decButton, BorderLayout.WEST);
		editorComponent.add(incButton, BorderLayout.EAST);
	}

	@Override
	public Object getCellEditorValue() {
		return editorValue;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		editorValue = (Integer) value;
		updateEditorLabel();
		return editorComponent;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		rendererLabel.setText(((Integer) value).toString());

		if (isSelected) {
			rendererComponent.setForeground(table.getSelectionForeground());
			rendererComponent.setBackground(table.getSelectionBackground());
		} else {
			rendererComponent.setForeground(table.getForeground());
			rendererComponent.setBackground(Color.WHITE);
		}

		return rendererComponent;
	}

	/**
	 * Increment the stored value.
	 */
	private void incrementValue() {
		editorValue = Math.min(editorValue + 1, maxValue);
	}

	/**
	 * Decrement the stored value.
	 */
	private void decrementValue() {
		editorValue = Math.max(editorValue - 1, minValue);
	}

	/**
	 * Update the displayed label to display the currently stored value.
	 */
	private void updateEditorLabel() {
		editorLabel.setText(Integer.toString(editorValue));
	}

}
