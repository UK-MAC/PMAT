package uk.co.awe.pmat.gui;

import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.Configuration;
import javax.swing.JTextField;
import java.awt.Window;
import javax.swing.JDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseManager;
import static uk.co.awe.pmat.Configuration.*;

/**
 * This sets up the configuration of the database.
 * 
 * @author AWE Plc copyright 2013
 */
final class ConnectionSettingsDialog extends JDialog {

	private static final Logger LOG = LoggerFactory
			.getLogger(ConnectionSettingsDialog.class);

	private static final String ADVANCED_BORDER_TITLE = "Advanced Options";
	private static final String DEFAULT_BORDER_TITLE = "Default Options";
	private static final String SHOW_ADVANCED_LABEL = "Advanced Options";
	private static final String HIDE_ADVANCED_LABEL = "Hide Advanced";

	private final Action showAdvancedAction = new AbstractAction(
			SHOW_ADVANCED_LABEL) {
		@Override
		public void actionPerformed(ActionEvent e) {
			showAdvanced = !showAdvanced;
			update();
		}
	};

	private final Action hideAdvancedAction = new AbstractAction(
			HIDE_ADVANCED_LABEL) {
		@Override
		public void actionPerformed(ActionEvent e) {
			showAdvanced = !showAdvanced;
			update();
		}
	};

	private final Action nameInDBOptionSelectionAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			final Configuration.NameInDBOption option = nameInDBOptionsModel
					.getSelectedElement();
			switch (option) {
			case FROM_ENVIRONMENT:
				nameInDBSpecified.setText(System.getProperty("user.name"));
				nameInDBSpecified.setEnabled(false);
				break;
			case SAME_AS_USERNAME:
				nameInDBSpecified.setText(workingConfig
						.getProperty(Key.DB_USERNAME));
				nameInDBSpecified.setEnabled(false);
				break;
			case USER_SPECIFIED:
				nameInDBSpecified.setText(workingConfig
						.getProperty(Key.NAME_IN_DATABASE_SPECIFIED));
				nameInDBSpecified.setEnabled(true);
				break;
			default:
				throw new IllegalStateException(
						"Unknown PropertyNameInDBOption: " + option);
			}
			workingConfig.setProperty(Key.NAME_IN_DATABASE_OPTION, option
					.name());
		}
	};

	private final Action usernameOptionSelectionAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			final Configuration.UsernameOption option = usernameOptionsModel
					.getSelectedElement();
			switch (option) {
			case ASK_ON_CONNECT:
				usernameSpecified.setText(null);
				usernameSpecified.setEnabled(false);
				break;
			case FROM_ENVIRONMENT:
				usernameSpecified.setText(System.getProperty("user.name"));
				usernameSpecified.setEnabled(false);
				break;
			case FROM_DATABASE:
				usernameSpecified.setText(null);
				usernameSpecified.setEnabled(false);
				break;
			case USER_SPECIFIED:
				usernameSpecified.setText(workingConfig
						.getProperty(Key.DB_USERNAME_SPECIFIED));
				usernameSpecified.setEnabled(true);
				break;
			default:
				throw new IllegalStateException(
						"Unknown PropertyUsernameOption: " + option);
			}
			workingConfig.setProperty(Key.DB_USERNAME_OPTION, option.name());
		}
	};

	private final Action passwordOptionSelectionAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			final Configuration.PasswordOption option = passwordOptionsModel
					.getSelectedElement();
			switch (option) {
			case ASK_ON_CONNECT:
				passwordSpecified.setText(null);
				passwordSpecified.setEnabled(false);
				break;
			case FROM_DATABASE:
				passwordSpecified.setText(null);
				passwordSpecified.setEnabled(false);
				break;
			case NONE:
				passwordSpecified.setText(null);
				passwordSpecified.setEnabled(false);
				break;
			case USER_SPECIFIED:
				passwordSpecified.setText(workingConfig
						.getProperty(Key.DB_PASSWORD_SPECIFIED));
				passwordSpecified.setEnabled(true);
				break;
			default:
				throw new IllegalStateException(
						"Unknown PropertyUsernameOption: " + option);
			}
			workingConfig.setProperty(Key.DB_PASSWORD_OPTION, option.name());
		}
	};

	/**
	 * A document listener that monitors a text field and updates a specific
	 * configuration property whenever the text changes.
	 */
	private final class ConfigDocumentListener implements DocumentListener {
		private final Key key;
		private final JTextField textField;

		/**
		 * Create a new {@code ConfigDocumentListener}.
		 * 
		 * @param key
		 *            the configuration key to change.
		 * @param textField
		 *            the text field to monitor.
		 */
		ConfigDocumentListener(Key key, JTextField textField) {
			this.key = key;
			this.textField = textField;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			workingConfig.setProperty(key, textField.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			workingConfig.setProperty(key, textField.getText());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			workingConfig.setProperty(key, textField.getText());
		}
	}

	private final DefaultComboBoxModel<DataOrigin> locationOptionsModel;
	private final DefaultComboBoxModel<Configuration.NameInDBOption> nameInDBOptionsModel;
	private final DefaultComboBoxModel<Configuration.UsernameOption> usernameOptionsModel;
	private final DefaultComboBoxModel<Configuration.PasswordOption> passwordOptionsModel;

	private final Configuration config;
	private final Configuration workingConfig;

	private boolean showAdvanced;

	/**
	 * Show a connection settings dialog.
	 * 
	 * @param window
	 *            the parent window of this dialog.
	 * @param config
	 *            the application configuration.
	 * @param dbMapping
	 *            the mapping used to communicate with the database.
	 */
	public static void showConnectionSettingsDialog(Window window,
			Configuration config) {

		ConnectionSettingsDialog connectionSettings = new ConnectionSettingsDialog(
				window, config);
		connectionSettings.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		connectionSettings.pack();

		final int parentCentreX = window.getX() + window.getWidth() / 2;
		final int perentCentreY = window.getY() + window.getHeight() / 2;

		final int x = parentCentreX - connectionSettings.getWidth() / 2;
		final int y = perentCentreY - connectionSettings.getHeight() / 2;

		connectionSettings.setLocation(x, y);
		connectionSettings.setVisible(true);
	}

	/**
	 * Create a new {@code ConnectionSettingsDialog}
	 * 
	 * @param window
	 *            the parent window of this dialog.
	 * @param config
	 *            the application configuration.
	 * @param dbMapping
	 *            the mapping used to communicate with the database.
	 */
	private ConnectionSettingsDialog(Window window,
            Configuration config) {

        super(window, "Connection Settings", JDialog.DEFAULT_MODALITY_TYPE);
        initComponents();
        this.config = config;
        this.workingConfig = new Configuration(config);

        if (config.getMode() == Configuration.Mode.DEVELOPMENT) {
            showAdvanced = true;
            advancedOptionsButton.setAction(hideAdvancedAction);
        } else {
            showAdvanced = false;
            advancedOptionsButton.setAction(showAdvancedAction);
        }

        locationOptionsModel = new DefaultComboBoxModel<>(DataOrigin.values());
        nameInDBOptionsModel = new DefaultComboBoxModel<>("Select name in database type", NameInDBOption.values());
        usernameOptionsModel = new DefaultComboBoxModel<>("Select username type", UsernameOption.values());
        passwordOptionsModel = new DefaultComboBoxModel<>("Select password type", PasswordOption.values());

        nameInDBOptionsModel.setSelectionAction(nameInDBOptionSelectionAction);
        usernameOptionsModel.setSelectionAction(usernameOptionSelectionAction);
        passwordOptionsModel.setSelectionAction(passwordOptionSelectionAction);

        // Connect the text boxes to the working configuration: whenever the
        // text is changed, the working config is updated to reflect this.
        nameInDBSpecified.getDocument().addDocumentListener(
                new ConfigDocumentListener(Key.NAME_IN_DATABASE_SPECIFIED, nameInDBSpecified));
        usernameSpecified.getDocument().addDocumentListener(
                new ConfigDocumentListener(Key.DB_USERNAME_SPECIFIED, usernameSpecified));
        passwordSpecified.getDocument().addDocumentListener(
                new ConfigDocumentListener(Key.DB_PASSWORD_SPECIFIED, passwordSpecified));
        schema.getDocument().addDocumentListener(
                new ConfigDocumentListener(Key.DB_SCHEMA, schema));
        driver.getDocument().addDocumentListener(
                new ConfigDocumentListener(Key.DB_DRIVER, driver));
        connectionUrl.getDocument().addDocumentListener(
                new ConfigDocumentListener(Key.DB_CONNECTION_URL, connectionUrl));

        saveButton.setAction(new AbstractAction("Save & Close") {
            @Override public void actionPerformed(ActionEvent e) {
                saveSettings();
                ConnectionSettingsDialog.this.setVisible(false);
            }
        });
        connectButton.setAction(new AbstractAction("Try Connection") {
            @Override public void actionPerformed(ActionEvent e) {
                tryConnection();
            }
        });

        update();
    }

	/**
	 * Update the fields, showing or hiding the advanced fields as necessary.
	 */
	private void update() {
		updateBasicFields();
		if (showAdvanced) {
			updateAdvancedFields();
			advancedOptionsButton.setAction(hideAdvancedAction);
		} else {
			advancedOptionsButton.setAction(showAdvancedAction);
		}
		advancedOptions.setVisible(showAdvanced);
		pack();
	}

	/**
	 * Update the basic fields, populating the combo boxes.
	 */
	private void updateBasicFields() {
		locationOptions.setModel(locationOptionsModel);
		nameInDBOptions.setModel(nameInDBOptionsModel);

		final String location = workingConfig.getProperty(Key.DATA_ORIGIN);
		try {
			DataOrigin option = DataOrigin.valueOf(location);
			locationOptionsModel.setSelectedElement(option);
		} catch (IllegalArgumentException ex) {
			LOG.error("Bad location option: " + location, ex);
		}

		final String nameInDB = workingConfig
				.getProperty(Key.NAME_IN_DATABASE_OPTION);
		try {
			NameInDBOption option = NameInDBOption.valueOf(nameInDB);
			nameInDBOptionsModel.setSelectedElement(option);
		} catch (IllegalArgumentException ex) {
			LOG.error("Bad nameInDB option: " + nameInDB, ex);
		}
	}

	/**
	 * Update the advanced fields, populating the combo boxes and setting the
	 * text fields.
	 */
	private void updateAdvancedFields() {
		usernameOptions.setModel(usernameOptionsModel);
		passwordOptions.setModel(passwordOptionsModel);

		schema.setText(workingConfig.getProperty(Key.DB_SCHEMA));
		driver.setText(workingConfig.getProperty(Key.DB_DRIVER));
		connectionUrl.setText(workingConfig.getProperty(Key.DB_CONNECTION_URL));

		final String username = workingConfig
				.getProperty(Key.DB_USERNAME_OPTION);
		try {
			UsernameOption option = UsernameOption.valueOf(username);
			usernameOptionsModel.setSelectedElement(option);
		} catch (IllegalArgumentException ex) {
			LOG.error("Bad username option: " + username, ex);
		}

		final String password = workingConfig
				.getProperty(Key.DB_PASSWORD_OPTION);
		try {
			PasswordOption option = PasswordOption.valueOf(password);
			passwordOptionsModel.setSelectedElement(option);
		} catch (IllegalArgumentException ex) {
			LOG.error("Bad password option: " + password, ex);
		}
	}

	/**
	 * Save the configuration settings.
	 */
	private void saveSettings() {
		config.updateFrom(workingConfig);
		config.saveConfigFile();
	}

	/**
	 * Attempt a simple query to the database to make sure the connection
	 * settings are correct, and inform the user of the result.
	 */
	private void tryConnection() {
		if (DatabaseManager.getConnection().tryConnection(workingConfig)) {
			JOptionPane.showMessageDialog(this, "Connection successful",
					"Trying Database Connection", JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Connection failed",
					"Trying Database Connection", JOptionPane.WARNING_MESSAGE);
		}
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

		getContentPane().setLayout(new java.awt.GridBagLayout());

		advancedOptions.setBorder(javax.swing.BorderFactory
				.createTitledBorder(ADVANCED_BORDER_TITLE));
		advancedOptions.setLayout(new java.awt.GridBagLayout());

		passwordSpecified.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(passwordSpecified, gridBagConstraints);

		passwordLabel.setText("Password");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(passwordLabel, gridBagConstraints);

		usernameSpecified.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(usernameSpecified, gridBagConstraints);

		databaseDriverLabel.setText("Database Driver");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(databaseDriverLabel, gridBagConstraints);

		schemaLabel.setText("Database Schema");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(schemaLabel, gridBagConstraints);

		schema.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(schema, gridBagConstraints);

		passwordWarning.setForeground(new java.awt.Color(255, 51, 51));
		passwordWarning
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		passwordWarning
				.setText("<html>\nThe password will be stored in PLAIN text in your config file\n</html>");
		passwordWarning.setPreferredSize(new java.awt.Dimension(200, 40));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(passwordWarning, gridBagConstraints);

		usernameLabel.setText("User name");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(usernameLabel, gridBagConstraints);

		connectionLabel.setText("Connection Url");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(connectionLabel, gridBagConstraints);

		connectionUrl.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(connectionUrl, gridBagConstraints);

		driver.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(driver, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(usernameOptions, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		advancedOptions.add(passwordOptions, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		getContentPane().add(advancedOptions, gridBagConstraints);

		defaultOptions.setBorder(javax.swing.BorderFactory
				.createTitledBorder(DEFAULT_BORDER_TITLE));
		defaultOptions.setLayout(new java.awt.GridBagLayout());

		loactionLabel.setText("Location");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		defaultOptions.add(loactionLabel, gridBagConstraints);

		nameInDBLabel.setText("Name recorded in database");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		defaultOptions.add(nameInDBLabel, gridBagConstraints);

		nameInDBSpecified.setColumns(20);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		defaultOptions.add(nameInDBSpecified, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		defaultOptions.add(locationOptions, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		defaultOptions.add(nameInDBOptions, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		getContentPane().add(defaultOptions, gridBagConstraints);

		buttonPanel.setLayout(new java.awt.GridBagLayout());

		connectButton.setText("Try Connection");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(connectButton, gridBagConstraints);

		saveButton.setText("Save & Close");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(saveButton, gridBagConstraints);

		advancedOptionsButton.setText("Advanced Options");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		buttonPanel.add(advancedOptionsButton, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		buttonPanel.add(spacer, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		getContentPane().add(buttonPanel, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JPanel advancedOptions = new javax.swing.JPanel();
	private final javax.swing.JButton advancedOptionsButton = new javax.swing.JButton();
	private final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	private final javax.swing.JButton connectButton = new javax.swing.JButton();
	private final javax.swing.JLabel connectionLabel = new javax.swing.JLabel();
	private final javax.swing.JTextField connectionUrl = new javax.swing.JTextField();
	private final javax.swing.JLabel databaseDriverLabel = new javax.swing.JLabel();
	private final javax.swing.JPanel defaultOptions = new javax.swing.JPanel();
	private final javax.swing.JTextField driver = new javax.swing.JTextField();
	private final javax.swing.JLabel loactionLabel = new javax.swing.JLabel();
	private final javax.swing.JComboBox<DataOrigin> locationOptions = new javax.swing.JComboBox<DataOrigin>();
	private final javax.swing.JLabel nameInDBLabel = new javax.swing.JLabel();
	private final javax.swing.JComboBox<Configuration.NameInDBOption> nameInDBOptions = new javax.swing.JComboBox<Configuration.NameInDBOption>();
	private final javax.swing.JTextField nameInDBSpecified = new javax.swing.JTextField();
	private final javax.swing.JLabel passwordLabel = new javax.swing.JLabel();
	private final javax.swing.JComboBox<Configuration.PasswordOption> passwordOptions = new javax.swing.JComboBox<Configuration.PasswordOption>();
	private final javax.swing.JPasswordField passwordSpecified = new javax.swing.JPasswordField();
	private final javax.swing.JLabel passwordWarning = new javax.swing.JLabel();
	private final javax.swing.JButton saveButton = new javax.swing.JButton();
	private final javax.swing.JTextField schema = new javax.swing.JTextField();
	private final javax.swing.JLabel schemaLabel = new javax.swing.JLabel();
	private final javax.swing.JPanel spacer = new javax.swing.JPanel();
	private final javax.swing.JLabel usernameLabel = new javax.swing.JLabel();
	private final javax.swing.JComboBox<Configuration.UsernameOption> usernameOptions = new javax.swing.JComboBox<Configuration.UsernameOption>();
	private final javax.swing.JTextField usernameSpecified = new javax.swing.JTextField();
	// End of variables declaration//GEN-END:variables

}
