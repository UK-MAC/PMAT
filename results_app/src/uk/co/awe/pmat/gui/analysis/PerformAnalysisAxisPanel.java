package uk.co.awe.pmat.gui.analysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.gui.models.analysis.PerformAnalysisAxisModel;
import uk.co.awe.pmat.gui.utils.ThreadedAction;

/**
 * A panel to allow for the selection of an analysis method to be applied to
 * a graph series.
 *
 * @author AWE Plc copyright 2013
 */
public final class PerformAnalysisAxisPanel extends JPanel implements ChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(PerformAnalysisAxisPanel.class);

    private static final Icon CROSS_ICON =
            new ImageIcon(ClassLoader.getSystemResource("cross16.png"));
    private static final Icon PLUS_ICON =
            new ImageIcon(ClassLoader.getSystemResource("plus16.png"));

    private final PerformAnalysisAxisModel performAnalysisAxisModel;

    /**
     * A class to handle the instantiation and display of the analysis methods
     * stored in the {@code analysisTypeSelection} combo box.
     */
    private static class AnalysisListCellRenderer extends DefaultListCellRenderer {

        private Map<Class<AnalysisMethod>, AnalysisMethod> analysisMethods
                = new HashMap<>();

        @Override
        @SuppressWarnings("unchecked")
        public Component getListCellRendererComponent(JList<?> list,
            Object value, int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof Class) {
                Class<AnalysisMethod> cls = (Class<AnalysisMethod>) value;
                if (!analysisMethods.containsKey(cls)) {
                    try {
                        analysisMethods.put(cls, cls.newInstance());
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.error("Failed to instantiate AnalysisMethod "
                                + cls.getSimpleName(), ex);
                        analysisMethods.put(cls, null);
                    }
                }
                AnalysisMethod analysisMethod = analysisMethods.get(cls);
                if (analysisMethod == null) {
                    setText("Malformed AnalysisMethod " + cls.getSimpleName());
                    setEnabled(false);
                } else {
                    setText(analysisMethods.get(cls).analysisMethodName());
                }
            }
            return this;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateRangeFields();
        updateButtons();
        revalidate();
    }

    /**
     * Create a new {@code PerformAnalysisAxisPanel}.
     *
     * @param performAnalysisAxisModel the model driving this panel.
     * @param workerListener a listener to be kept informed of the state of
     * any {@code SwingWorker}s launched.
     */
    public PerformAnalysisAxisPanel(final PerformAnalysisAxisModel performAnalysisAxisModel,
            final PropertyChangeListener workerListener) {

        initComponents();
        this.performAnalysisAxisModel = performAnalysisAxisModel;

        analysisTypeSelection.setModel(performAnalysisAxisModel.getAnalysisTypeModel());
        analysisTypeSelection.setRenderer(new AnalysisListCellRenderer());

        axisSelection.setModel(performAnalysisAxisModel.getAxisModel());

        performAnalysisAxisModel.setAnalysisShown(showButton.isSelected());

        showButton.setAction(new ThreadedAction("Show", workerListener) {
            @Override public void actionPerformedInBackground() {
                performAnalysisAxisModel.setAnalysisShown(showButton.isSelected());
            }
        });

        updateAddRemoveButton();
        stateChanged(null);
    }

    /**
     * Update the range fields.
     */
    private void updateRangeFields() {
        Integer rangeFrom = performAnalysisAxisModel.getRangeFrom();
        Integer rangeTo = performAnalysisAxisModel.getRangeTo();

        rangeFromField.setText(
                rangeFrom == null ? "" : rangeFrom.toString());
        rangeToField.setText(
                rangeTo == null ? "" : rangeTo.toString());
    }

    /**
     * Update the add/remove button depending on whether this is the bottom
     * panel or not.
     */
    private void updateAddRemoveButton() {
        if (performAnalysisAxisModel.isCurrent()) {
            addRemoveButton.setIcon(PLUS_ICON);
        } else {
            addRemoveButton.setIcon(CROSS_ICON);
        }
    }

    /**
     * Update the config/equation buttons depending on whether there they should
     * be enabled or not.
     */
    private void updateButtons() {
        configButton.setEnabled(
                performAnalysisAxisModel.isConfigurable());
        equationButton.setEnabled(
                performAnalysisAxisModel.hasEquations());
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(analysisTypeSelection, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(axisSelection, gridBagConstraints);

        rangePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        rangePanel.setLayout(new java.awt.GridBagLayout());

        rangeFromLabel.setText("From:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rangePanel.add(rangeFromLabel, gridBagConstraints);

        rangeFromField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rangeFromFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        rangePanel.add(rangeFromField, gridBagConstraints);

        rangeToLabel.setText("To:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rangePanel.add(rangeToLabel, gridBagConstraints);

        rangeToField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rangeToFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        rangePanel.add(rangeToField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(rangePanel, gridBagConstraints);

        configButton.setText("Configure");
        configButton.setEnabled(false);
        configButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(configButton, gridBagConstraints);

        lineTypeButton.setText("Line Type");
        lineTypeButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(lineTypeButton, gridBagConstraints);

        showButton.setSelected(true);
        showButton.setText("Show");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(showButton, gridBagConstraints);

        addRemoveButton.setMaximumSize(new java.awt.Dimension(25, 25));
        addRemoveButton.setMinimumSize(new java.awt.Dimension(25, 25));
        addRemoveButton.setPreferredSize(new java.awt.Dimension(25, 25));
        addRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRemoveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(addRemoveButton, gridBagConstraints);

        equationButton.setText("Equation");
        equationButton.setEnabled(false);
        equationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(equationButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

/**
 * Action to perform on Add/Remove button click.
 *
 * @param evt the click event.
 */
private void addRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRemoveButtonActionPerformed
    if (performAnalysisAxisModel.isCurrent()) {
        performAnalysisAxisModel.addAxisAnalysis();
    } else {
        performAnalysisAxisModel.removeAxisAnalysis();
    }
}//GEN-LAST:event_addRemoveButtonActionPerformed

/**
 * Action to perform when the "range from" field loses focus.
 *
 * @param evt the focus event.
 */
private void rangeFromFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rangeFromFieldFocusLost
    try {
        rangeFromField.setBackground(new JTextField().getBackground());
        int rangeFrom = Integer.parseInt(rangeFromField.getText());
        performAnalysisAxisModel.setRangeFrom(rangeFrom);
    } catch (NumberFormatException ex) {
        rangeFromField.setBackground(Color.red);
    }
}//GEN-LAST:event_rangeFromFieldFocusLost

/**
 * Action to perform when the "range to" field loses focus.
 *
 * @param evt the focus event.
 */
private void rangeToFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rangeToFieldFocusLost
    try {
        rangeFromField.setBackground(new JTextField().getBackground());
        int rangeTo = Integer.parseInt(rangeToField.getText());
        performAnalysisAxisModel.setRangeTo(rangeTo);
    } catch (NumberFormatException ex) {
        rangeFromField.setBackground(Color.red);
    }
}//GEN-LAST:event_rangeToFieldFocusLost

/**
 * Action to perform on Config button click.
 *
 * @param evt the click event.
 */
private void configButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configButtonActionPerformed
    Point location = MouseInfo.getPointerInfo().getLocation();
    performAnalysisAxisModel.showConfiguration(this, location);
}//GEN-LAST:event_configButtonActionPerformed

/**
 * Action to perform on Equation button click.
 *
 * @param evt the click event.
 */
private void equationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equationButtonActionPerformed
    Point location = MouseInfo.getPointerInfo().getLocation();
    performAnalysisAxisModel.showEquations(this, location);
}//GEN-LAST:event_equationButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton addRemoveButton = new javax.swing.JButton();
    private final javax.swing.JComboBox<Class<AnalysisMethod>> analysisTypeSelection = new javax.swing.JComboBox<Class<AnalysisMethod>>();
    private final javax.swing.JComboBox<GraphData> axisSelection = new javax.swing.JComboBox<GraphData>();
    private final javax.swing.JButton configButton = new javax.swing.JButton();
    private final javax.swing.JButton equationButton = new javax.swing.JButton();
    private final javax.swing.JButton lineTypeButton = new javax.swing.JButton();
    private final javax.swing.JTextField rangeFromField = new javax.swing.JTextField();
    private final javax.swing.JLabel rangeFromLabel = new javax.swing.JLabel();
    private final javax.swing.JPanel rangePanel = new javax.swing.JPanel();
    private final javax.swing.JTextField rangeToField = new javax.swing.JTextField();
    private final javax.swing.JLabel rangeToLabel = new javax.swing.JLabel();
    private final javax.swing.JToggleButton showButton = new javax.swing.JToggleButton();
    // End of variables declaration//GEN-END:variables

}
