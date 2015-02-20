package uk.co.awe.pmat.gui.configpanels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Application;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Machine;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Compiler;
import uk.co.awe.pmat.db.Mpi;
import uk.co.awe.pmat.db.OperatingSystem;
import uk.co.awe.pmat.db.Processor;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * A panel to display the fields necessary to add or update MetaData and commit
 * the changes to the database. This panel handles all the meta data types
 * except the {@code MetaDataType.SYSTEM} type.
 * 
 * @author AWE Plc copyright 2013
 */
class MetaDataEditorPanel extends JPanel implements MetaDataEditor {

    private static final Logger LOG = LoggerFactory.getLogger(MetaDataEditorPanel.class);

    private final MetaData.Type type;
    private final LinkedHashMap<String, Class<?>> fields;
    private final Map<String, ValueComponent> valueComponents = new HashMap<>();

    private boolean isEditing;
    private MetaData displayedData;

    /**
     * An interface to allow the extraction of a "value" for the components used
     * in this panel.
     */
    private interface ValueComponent {
        /**
         * Return the value held in this component.
         *
         * @return the components value.
         */
        Object getValue();
    }

    /**
     * An extension of the {@code JTextField} class which implements the
     * {@code ValueComponent} interface to return the value stored in the field
     * text.
     */
    private static final class ValueTextField extends JTextField
            implements ValueComponent {

        /**
         * Create a new {@code ValueTextField}.
         */
        private ValueTextField() {
            super();
        }

        /**
         * Create a new {@code ValueTextField} displaying the given text.
         *
         * @param text the text to display.
         */
        private ValueTextField(String text) {
            super(text);
        }

        @Override
        public Object getValue() {
            return getText();
        }
    }

    /**
     * An extension of the {@code JCheckBox} class which implements the
     * {@code ValueComponent} interface to return whether the check box is
     * selected or not.
     */
    private static final class ValueCheckBox extends JCheckBox
            implements ValueComponent {

        /**
         * Create a new {@code ValueCheckBox}.
         *
         * @param text the label for the check box.
         * @param selected the starting selection state of the check box.
         */
        private ValueCheckBox(String text, boolean selected) {
            super(text, selected);
        }

        @Override
        public Object getValue() {
            return isSelected();
        }
    }

    /**
     * Create a new {@code MetaDataEditorPanel}.
     * 
     * @param dbMapping the mapping used to communicate with the database.
     * @param parent the meta data view panel which contains this editor panel.
     * @param type the meta data type this panel is editing.
     */
    MetaDataEditorPanel(final MetaDataView parent,
            final MetaData.Type type) {
        initComponents();

        this.type = type;
        fields = type.getFields();

        createDisplayComponents(null);

        cancelButton.setAction(new AbstractAction("Cancel") {
            @Override public void actionPerformed(ActionEvent e) {
                parent.removeEditPanel();
            }
        });
        commitButton.setAction(new AbstractAction("Commit") {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    commitData();
                    parent.refreshTable();
                    parent.removeEditPanel();
                } catch (DatabaseException ex) {
                    LOG.debug("Failed to save meta-data", ex);
                    messageLabel.setText(ex.getMessage());
                }
            }
        });
        clearButton.setAction(new AbstractAction("Clear") {
            @Override public void actionPerformed(ActionEvent e) {
                clearData();
            }
        });
    }

    @Override
    public void displayData(final MetaData metaData) {
        createDisplayComponents(metaData);
        displayedData = metaData;
    }

    /**
     * Create the components needed to display/edit the meta data and populate
     * said components with the given meta data, or leave them blank if the
     * meta data is {@code null}.
     * 
     * @param metaData the meta data to display, or {@code null} to create
     * blank components.
     */
    private void createDisplayComponents(final MetaData metaData) {
        labelPanel.removeAll();
        inputPanel.removeAll();

        labelPanel.setLayout(new GridLayout(fields.size(), 1, 5, 5));
        inputPanel.setLayout(new GridLayout(fields.size(), 1, 5, 5));

        List<Object> data = new ArrayList<>(fields.size());
        int idx = 0;
        for (String name : fields.keySet()) {
            labelPanel.add(new JLabel(StringUtils.normaliseCamelCase(name)), idx);
            if (metaData != null) {
                data.add(metaData.getData(name));
            } else {
                data.add(null);
            }
            ++idx;
        }

        idx = 0;
        for (Map.Entry<String, Class<?>> entry : fields.entrySet()) {
            final Object obj = data.get(idx);
            final Class<?> fieldType = entry.getValue();
            final ValueComponent valueComp;
            if (String.class.equals(fieldType)
                    || Integer.class.equals(fieldType)
                    || Double.class.equals(fieldType)) {
                final ValueTextField field;
                if (obj == null) {
                    field = new ValueTextField();
                    inputPanel.add(field, idx);
                } else {
                    field = new ValueTextField(obj.toString());
                    inputPanel.add(field, idx);
                }
                valueComp = field;
            } else if (Boolean.class.equals(fieldType)) {
                final ValueCheckBox box;
                if (obj == null) {
                    box = new ValueCheckBox((String) null, false);
                    inputPanel.add(box, idx);
                } else {
                    box = new ValueCheckBox((String) null, (Boolean) obj);
                    inputPanel.add(box, idx);
                }
                valueComp = box;
            } else {
                throw new IllegalArgumentException("Cannot display type " + fieldType);
            }
            valueComponents.put(entry.getKey(), valueComp);
            ++idx;
        }
    }

    @Override
    public void setEditing(final boolean editingFlag) {
        isEditing = editingFlag;
        if (isEditing) {
            commitButton.setText("Update");
        } else {
            commitButton.setText("Add");
        }
    }

    /**
     * Clear the data displayed in the components.
     */
    private void clearData() {
        createDisplayComponents(null);
        revalidate();
    }

    /**
     * Commit the currently entered data to the database.
     * 
     * @throws DatabaseException if an error occurs whilst committing the data.
     */
    private void commitData() throws DatabaseException {
        
        final Map<String, Object> valueMap = new HashMap<>(fields.size());
        for (Map.Entry<String, Class<?>> entry : fields.entrySet()) {
            final String fieldName = entry.getKey();
            final Class<?> fieldType = entry.getValue();
            Object value = valueComponents.get(fieldName).getValue();
            if (value != null) {
                String valueStr = value.toString();
                if (fieldType.equals(String.class)) {
                    value = valueStr;
                } else if (fieldType.equals(Integer.class)) {
                    if (!valueStr.isEmpty()) {
                        value = Integer.valueOf(valueStr);
                    } else {
                        value = null;
                    }
                } else if (fieldType.equals(Boolean.class)) {
                    if (!valueStr.isEmpty()) {
                        value = Boolean.valueOf(valueStr);
                    } else {
                        value = null;
                    }
                } else {
                    throw new IllegalStateException("Unknown value type " + fieldType);
                }
            }
            valueMap.put(fieldName, value);
        }
        
        final MetaData newData;
        switch (type) {
            case APPLICATION:
                newData = new Application(valueMap);
                break;
            case COMPILER:
                newData = new Compiler(valueMap);
                break;
            case MACHINE:
                newData = new Machine(valueMap);
                break;
            case MPI:
                newData = new Mpi(valueMap);
                break;
            case OPERATING_SYSTEM:
                newData = new OperatingSystem(valueMap);
                break;
            case PROCESSOR:
                newData = new Processor(valueMap);
                break;
            default:
                throw new IllegalStateException("Unknown metadata type " + type);
        }
        
        if (displayedData == null) {
            DatabaseManager.getConnection().save(newData);
        } else {
            DatabaseManager.getConnection().update(displayedData, newData);
        }
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

        buttonPanel = new javax.swing.JPanel();
        commitButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        dataPanel = new javax.swing.JPanel();
        rowPanel = new javax.swing.JPanel();
        labelPanel = new javax.swing.JPanel();
        inputPanel = new javax.swing.JPanel();
        messageLabel = new javax.swing.JLabel();
        spacer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        buttonPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        commitButton.setText("Add");
        commitButton.setMaximumSize(new java.awt.Dimension(90, 25));
        commitButton.setMinimumSize(new java.awt.Dimension(90, 25));
        commitButton.setPreferredSize(new java.awt.Dimension(90, 25));
        buttonPanel.add(commitButton);

        clearButton.setText("Clear");
        clearButton.setMaximumSize(new java.awt.Dimension(90, 25));
        clearButton.setMinimumSize(new java.awt.Dimension(90, 25));
        clearButton.setPreferredSize(new java.awt.Dimension(90, 25));
        buttonPanel.add(clearButton);

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(90, 25));
        cancelButton.setMinimumSize(new java.awt.Dimension(90, 25));
        cancelButton.setPreferredSize(new java.awt.Dimension(90, 25));
        buttonPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        add(buttonPanel, gridBagConstraints);

        dataPanel.setLayout(new java.awt.GridBagLayout());

        rowPanel.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout labelPanelLayout = new javax.swing.GroupLayout(labelPanel);
        labelPanel.setLayout(labelPanelLayout);
        labelPanelLayout.setHorizontalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        labelPanelLayout.setVerticalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rowPanel.add(labelPanel, gridBagConstraints);

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rowPanel.add(inputPanel, gridBagConstraints);

        messageLabel.setForeground(java.awt.Color.red);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rowPanel.add(messageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        dataPanel.add(rowPanel, gridBagConstraints);

        spacer.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout spacerLayout = new javax.swing.GroupLayout(spacer);
        spacer.setLayout(spacerLayout);
        spacerLayout.setHorizontalGroup(
            spacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        spacerLayout.setVerticalGroup(
            spacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        dataPanel.add(spacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(dataPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton commitButton;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JPanel rowPanel;
    private javax.swing.JPanel spacer;
    // End of variables declaration//GEN-END:variables

}
