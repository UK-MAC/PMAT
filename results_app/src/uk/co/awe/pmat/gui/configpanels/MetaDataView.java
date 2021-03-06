package uk.co.awe.pmat.gui.configpanels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.gui.models.MetaDataDisplayModel;

/**
 * A panel to display all the different types of meta-data stored in the
 * database and to allow editing/adding of these types.
 * 
 * @author AWE Plc copyright 2013
 */
public class MetaDataView extends JPanel implements ChangeListener {

	private MetaDataEditor configEditor;

	/**
	 * Create a new {@code MetaDataEditor}.
	 * 
	 * @param configDataModel
	 *            the model driving this panel.
	 */
	public MetaDataView(final MetaDataDisplayModel configDataModel) {
		super();
		initComponents();

		configEditor = null;

		for (MetaData.Type type : MetaData.Type.values()) {
			JPanel panel = new MetaDataDisplayPanel(type, configDataModel,
					(ChangeListener) this);
			displayPanel.add(type.displayName(), panel);
		}
		displayPanel.addChangeListener((ChangeListener) this);

		editButton.setAction(new AbstractAction("Edit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEditPanel(true);
			}
		});
		addButton.setAction(new AbstractAction("Add") {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEditPanel(false);
			}
		});
		removeButton.setAction(new AbstractAction("Remove") {
			@Override
			public void actionPerformed(ActionEvent e) {
				MetaDataDisplayPanel panel = (MetaDataDisplayPanel) displayPanel
						.getSelectedComponent();
				panel.deleteSelectedMetaData();
				refreshTable();
			}
		});
		displayPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				removeEditPanel();
			}
		});

		editButton.setEnabled(false);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		MetaDataDisplayPanel panel = (MetaDataDisplayPanel) displayPanel
				.getSelectedComponent();
		editButton.setEnabled(panel.getSelectedMetaData() != null);
	}

	/**
	 * Creates and displays an edit panel. The type of the panel is decided by
	 * the parameter editPanelName.
	 * 
	 * @param editingFlag
	 *            whether the panel should be in "edit" or "add" mode.
	 */
	private void setEditPanel(boolean editingFlag) {
		setButtonsEnabled(false);

		final MetaDataDisplayPanel selectedPanel = (MetaDataDisplayPanel) displayPanel
				.getSelectedComponent();
		final MetaData.Type type = selectedPanel.getType();

		editPanel.removeAll();
		configEditor = new MetaDataEditorPanel(this, type);
		configEditor.setEditing(editingFlag);

		final MetaData data = selectedPanel.getSelectedMetaData();
		if (editingFlag && data != null) {
			configEditor.displayData(data);
		}

		editPanel.add((JPanel) configEditor, BorderLayout.CENTER);
		editPanel.validate();
	}

	/**
	 * Refresh the currently displayed panel.
	 */
	void refreshTable() {
		MetaDataDisplayPanel currentPanel = (MetaDataDisplayPanel) displayPanel
				.getSelectedComponent();
		currentPanel.stateChanged(new ChangeEvent(this));
	}

	/**
	 * Remove the currently displayed editor panel.
	 */
	void removeEditPanel() {
		configEditor = null;
		editPanel.removeAll();
		editPanel.repaint();

		setButtonsEnabled(true);
	}

	/**
	 * Set the enabled state of the add/edit/remove buttons.
	 * 
	 * @param flag
	 *            {@code true} if the buttons should be enabled, {@code false}
	 *            otherwise.
	 */
	private void setButtonsEnabled(boolean flag) {
		addButton.setEnabled(flag);
		editButton.setEnabled(flag);
		removeButton.setEnabled(flag);
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

		displayPanel = new javax.swing.JTabbedPane();
		buttonsPanel = new javax.swing.JPanel();
		addButton = new javax.swing.JButton();
		editButton = new javax.swing.JButton();
		removeButton = new javax.swing.JButton();
		editPanel = new javax.swing.JPanel();

		setMinimumSize(new java.awt.Dimension(422, 223));
		setPreferredSize(new java.awt.Dimension(422, 223));
		setLayout(new java.awt.GridBagLayout());

		displayPanel.setMinimumSize(new java.awt.Dimension(5, 200));
		displayPanel.setName("displayPanel"); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(displayPanel, gridBagConstraints);

		buttonsPanel.setName("buttonsPanel"); // NOI18N
		buttonsPanel.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.RIGHT));

		addButton.setText("Add");
		addButton.setMaximumSize(new java.awt.Dimension(90, 25));
		addButton.setMinimumSize(new java.awt.Dimension(90, 25));
		addButton.setName("addButton"); // NOI18N
		addButton.setPreferredSize(new java.awt.Dimension(90, 25));
		buttonsPanel.add(addButton);

		editButton.setText("Edit");
		editButton.setMaximumSize(new java.awt.Dimension(90, 25));
		editButton.setMinimumSize(new java.awt.Dimension(90, 25));
		editButton.setName("editButton"); // NOI18N
		editButton.setPreferredSize(new java.awt.Dimension(90, 25));
		buttonsPanel.add(editButton);

		removeButton.setText("Remove");
		removeButton.setMaximumSize(new java.awt.Dimension(90, 25));
		removeButton.setMinimumSize(new java.awt.Dimension(90, 25));
		removeButton.setName("removeButton"); // NOI18N
		removeButton.setPreferredSize(new java.awt.Dimension(90, 25));
		buttonsPanel.add(removeButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		add(buttonsPanel, gridBagConstraints);

		editPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		editPanel.setMinimumSize(new java.awt.Dimension(500, 300));
		editPanel.setName("editPanel"); // NOI18N
		editPanel.setPreferredSize(new java.awt.Dimension(500, 300));
		editPanel.setLayout(new java.awt.BorderLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		add(editPanel, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addButton;
	private javax.swing.JPanel buttonsPanel;
	private javax.swing.JTabbedPane displayPanel;
	private javax.swing.JButton editButton;
	private javax.swing.JPanel editPanel;
	private javax.swing.JButton removeButton;
	// End of variables declaration//GEN-END:variables

}
