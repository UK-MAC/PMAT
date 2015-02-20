package uk.co.awe.pmat.gui.addresults;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.gui.models.ConfigPanelModel;

/**
 * Displays and allows setting of general information for a run.
 *
 * @author AWE Plc copyright 2013
 */
public final class ConfigPanel extends JPanel implements ChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigPanel.class);

    private final ConfigPanelModel model;
    private final Map<MetaData.Type, JComboBox<MetaData>> metaDataComboBoxes
            = new EnumMap<>(MetaData.Type.class);

    /**
     * A helper class which is used to properly render {@code MetaData} objects
     * in a combo box list.
     */
    private static final class MetaDataListCellRender extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            if (value instanceof MetaData) {
                MetaData metaData = (MetaData) value;
                return super.getListCellRendererComponent(list,
                        metaData.displayName(), index, isSelected, cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
        }
    }

    /**
     * Create a new {@code ConfigPanel}.
     * 
     * @param model the model driving this panel.
     */
    public ConfigPanel(final ConfigPanelModel model) {
        initComponents();
        this.model = model;
        this.model.addChangeListener((ChangeListener) this);
        
        final GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = 3;
        labelGbc.fill = GridBagConstraints.VERTICAL;
        labelGbc.anchor = GridBagConstraints.EAST;
        labelGbc.insets = new Insets(5, 5, 5, 5);
        
        final GridBagConstraints comboGbc = new GridBagConstraints();
        comboGbc.gridx = 1;
        comboGbc.gridy = 3;
        comboGbc.gridwidth = 3;
        comboGbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        comboGbc.anchor = java.awt.GridBagConstraints.WEST;
        comboGbc.weightx = 1.0;
        comboGbc.insets = new java.awt.Insets(5, 5, 5, 5);
        
        for (MetaData.Type type : MetaData.Type.values()) {
            add(new JLabel(type.displayName()), labelGbc);
            
            final JComboBox<MetaData> comboBox = new JComboBox<>(model.getMetaDataModel(type));
            comboBox.setRenderer(new MetaDataListCellRender());
            
            add(comboBox, comboGbc);
            
            metaDataComboBoxes.put(type, comboBox);
            ++labelGbc.gridy;
            ++comboGbc.gridy;
        }
        
        userField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                model.setUser(userField.getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                model.setUser(userField.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                model.setUser(userField.getText());
            }
        });
        
        tagField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                model.setTag(tagField.getText());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                model.setTag(tagField.getText());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                model.setTag(tagField.getText());
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        userField.setText(model.getUser());
        dataIsPrivateCheckBox.setSelected(model.isPrivate());
        appName.setText(model.getApplicationName());
        tagField.setText(model.getTag());
        
        for (Map.Entry<MetaData.Type, JComboBox<MetaData>> entry
                : metaDataComboBoxes.entrySet()) {
            entry.getValue().setModel(model.getMetaDataModel(entry.getKey()));
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

        setBorder(javax.swing.BorderFactory.createTitledBorder("General Configuaration"));
        setLayout(new java.awt.GridBagLayout());

        userLabel.setText("User");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(userLabel, gridBagConstraints);

        userField.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(userField, gridBagConstraints);

        dataIsPrivateCheckBox.setToolTipText("Data should not be exported");
        dataIsPrivateCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dataIsPrivateCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(dataIsPrivateCheckBox, gridBagConstraints);

        dataPrivateLabel.setText("Data is Private");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(dataPrivateLabel, gridBagConstraints);

        appName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(appName, gridBagConstraints);

        appNameLabel.setText("Application Name from the File");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(appNameLabel, gridBagConstraints);

        tagField.setColumns(18);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tagField, gridBagConstraints);

        tagLabel.setText("Tag");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tagLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JTextField appName = new javax.swing.JTextField();
    private final javax.swing.JLabel appNameLabel = new javax.swing.JLabel();
    private final javax.swing.JCheckBox dataIsPrivateCheckBox = new javax.swing.JCheckBox();
    private final javax.swing.JLabel dataPrivateLabel = new javax.swing.JLabel();
    private final javax.swing.JTextField tagField = new javax.swing.JTextField();
    private final javax.swing.JLabel tagLabel = new javax.swing.JLabel();
    private final javax.swing.JTextField userField = new javax.swing.JTextField();
    private final javax.swing.JLabel userLabel = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables
}
