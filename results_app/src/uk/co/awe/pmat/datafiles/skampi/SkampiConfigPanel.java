package uk.co.awe.pmat.datafiles.skampi;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays and allows setting of run specific information for SkaMPI files and
 * allows the renaming of variables in each result set.
 * 
 * @author AWE Plc copyright 2013
 */
public final class SkampiConfigPanel extends JPanel implements ChangeListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(SkampiConfigPanel.class);

	private final SkampiConfigPanelModel model;

	/**
	 * Create a new {@code SkampiConfigPanel}.
	 * 
	 * @param model
	 *            the model driving this panel
	 */
	public SkampiConfigPanel(SkampiConfigPanelModel model) {
		initComponents();
		this.model = model;
		model.addChangeListener((ChangeListener) this);

		toggleNewVariablePanel(false);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		commBetween.setModel(model.getCommBetweenModel());
		resultSet.setModel(model.getResultSetsModel());
		variable.setModel(model.getResultSetVariables());
		becomes.setModel(model.getVariableRenamesModel());
		toggleNewVariablePanel(model.isNewVariablePanelShown());
		revalidate();
		// Set the border title to the selected file.
		((TitledBorder) getBorder()).setTitle(model.getBorderTitle());
		repaint();
	}

	/**
	 * Update the model with the variable name change when the new variable name
	 * is "Accepted".
	 */
	private void variableNameChangeOKed() {
		toggleNewVariablePanel(false);
		revalidate();

		becomes.setSelectedIndex(-1);

		if (!newVariableName.getText().isEmpty()) {
			model.addUserVariableNameChange(newVariableName.getText());
		}
	}

	/**
	 * Ignore the current variable name change when the new variable name is
	 * "Cancelled".
	 */
	private void userNameChangeCancelled() {
		toggleNewVariablePanel(false);
		revalidate();

		becomes.setSelectedIndex(-1);
	}

	/**
	 * Display a panel to allow a variable name change to be set.
	 * 
	 * @param showFlag
	 *            {@code true} if the panel should be displayed, {@code false}
	 *            otherwise.
	 */
	private void toggleNewVariablePanel(boolean showFlag) {
		newVariableName.setVisible(showFlag);
		okButton.setVisible(showFlag);
		cancelButton.setVisible(showFlag);
		becomes.setEnabled(!showFlag);
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

		setBorder(javax.swing.BorderFactory
				.createTitledBorder("Run Specific Configuration"));
		setLayout(new java.awt.GridBagLayout());

		commBetweenLabel.setText("Communication between cores");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(commBetweenLabel, gridBagConstraints);

		commBetween.setEditable(true);
		commBetween.setPreferredSize(new java.awt.Dimension(300, 24));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(commBetween, gridBagConstraints);

		resultSetLabel.setText("Variable Meanings in result set");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(resultSetLabel, gridBagConstraints);

		resultSet.setPreferredSize(new java.awt.Dimension(300, 24));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(resultSet, gridBagConstraints);

		variableLabel.setText("Variable");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(variableLabel, gridBagConstraints);

		variable.setPreferredSize(new java.awt.Dimension(300, 24));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(variable, gridBagConstraints);

		becomesLabel.setText("becomes");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(becomesLabel, gridBagConstraints);

		becomes.setPreferredSize(new java.awt.Dimension(300, 24));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(becomes, gridBagConstraints);

		newVariablePanel.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		newVariablePanel.add(newVariableName, gridBagConstraints);

		okButton.setText("Accept");
		okButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		newVariablePanel.add(okButton, gridBagConstraints);

		cancelButton.setText("Cancel");
		cancelButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		newVariablePanel.add(cancelButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(newVariablePanel, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * The function that is called when the "OK" button is clicked.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
		variableNameChangeOKed();
	}// GEN-LAST:event_okButtonActionPerformed

	/**
	 * The function that is called when the "Cancel" button is clicked.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
		userNameChangeCancelled();
	}// GEN-LAST:event_cancelButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JComboBox<String> becomes = new javax.swing.JComboBox<String>();
	private final javax.swing.JLabel becomesLabel = new javax.swing.JLabel();
	private final javax.swing.JButton cancelButton = new javax.swing.JButton();
	private final javax.swing.JComboBox<String> commBetween = new javax.swing.JComboBox<String>();
	private final javax.swing.JLabel commBetweenLabel = new javax.swing.JLabel();
	private final javax.swing.JTextField newVariableName = new javax.swing.JTextField();
	private final javax.swing.JPanel newVariablePanel = new javax.swing.JPanel();
	private final javax.swing.JButton okButton = new javax.swing.JButton();
	private final javax.swing.JComboBox<String> resultSet = new javax.swing.JComboBox<String>();
	private final javax.swing.JLabel resultSetLabel = new javax.swing.JLabel();
	private final javax.swing.JComboBox<String> variable = new javax.swing.JComboBox<String>();
	private final javax.swing.JLabel variableLabel = new javax.swing.JLabel();
	// End of variables declaration//GEN-END:variables
}