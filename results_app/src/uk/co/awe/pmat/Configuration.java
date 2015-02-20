package uk.co.awe.pmat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseProperties;

/**
 * A class to contain and manage all the application configuration data. This
 * includes database configuration information which is handled in conjunction
 * with a {@code DatabaseProperties} class.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Configuration {

	/**
	 * Different modes the application can be running in. Each mode will have
	 * it's own configuration file.
	 */
	public enum Mode {
		/** Normal Mode. */
		NORMAL,
		/** Development Mode. */
		DEVELOPMENT,
		/** Testing Mode. */
		TEST
	};

	/**
	 * The different properties that {@code Configuration} class is responsible
	 * for.
	 */
	public enum Key {
		/** Database user name value. */
		DB_USERNAME(null),
		/** Database user name option. */
		DB_USERNAME_OPTION("database.username"),
		/** Database user name if specified. */
		DB_USERNAME_SPECIFIED("database.username.specified"),
		/** Database password value. */
		DB_PASSWORD(null),
		/** Database password option. */
		DB_PASSWORD_OPTION("database.password"),
		/** Database password if specified. */
		DB_PASSWORD_SPECIFIED("database.password.specified"),
		/** */
		DB_DRIVER("database.driver"),
		/** */
		DB_CONNECTION_URL("database.connection.url"),
		/** */
		DB_SCHEMA("database.schema"),
		/** */
		DATA_ORIGIN("database.dataorigin"),
		/** */
		NAME_IN_DATABASE(null),
		/** */
		NAME_IN_DATABASE_OPTION("database.storedname"),
		/** */
		NAME_IN_DATABASE_SPECIFIED("database.storedname.specified"),
		/** */
		RESULTS_FILE_PATH("results.filepath"),
		/** */
		IMPORT_FILE_PATH("import.filepath"),
		/** */
		ANALYSIS_FILE_PATH("analysis.filepath"),
		/** */
		SAVE_CSV_FILE_PATH("savecsv.filepath"),
		/** */
		APP_POS_X("application.location.x"),
		/** */
		APP_POS_Y("application.location.y"),
		/** */
		APP_MAXIMISED("application.maximised"),
		/** */
		APP_WIDTH("application.width"),
		/** */
		APP_HEIGHT("application.height"),
		/** */
		FILE_EDITOR("application.fileEditor"),
		/** */
		WEB_BROWSER("application.webBrowser");

		/**
		 * The key use in retrieving the value from the configuration files.
		 */
		private final String resourceKey;

		/**
		 * Construct the {@code Enum} with the given resource key.
		 * 
		 * @param resourceKey
		 *            the resource key for this {@code Key}.
		 */
		private Key(String resourceKey) {
			this.resourceKey = resourceKey;
		}
	}

	/**
	 * An {@code Enum} representation of where the data in the database is from.
	 */
	public enum DataOrigin {
		/** Data comes from Warwick. */
		WARWICK(1, "Warwick"),
		/** Data comes from AWE. */
		AWE(16, "AWE"),
		/** Data is test data. */
		TEST(15, "Test");

		private final int offSet;
		private final String displayName;

		/**
		 * Construct the {@code DataOrigin} with the given offset and display
		 * name.
		 * 
		 * @param offSet
		 *            the primary key offset for this {@code DataOrigin}.
		 * @param displayName
		 *            the display name for this {@code DataOrigin}.
		 */
		private DataOrigin(int offSet, String displayName) {
			this.offSet = offSet;
			this.displayName = displayName;
		}

		/**
		 * Returns the {@code ID} offset of the primary key in the database
		 * table for this origin. This is so that data from different origins
		 * will never have clashing {@code ID}s.
		 * 
		 * @return the offset.
		 */
		public int getOffSet() {
			return offSet;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}

	/**
	 * An {@code Enum} representation of the different ways the password can be
	 * specified.
	 */
	public enum PasswordOption {
		/** No password required. */
		NONE("No password"),
		/** Ask for password on connection. */
		ASK_ON_CONNECT("Ask at connection time"),
		/** */
		FROM_DATABASE("From database config"),
		/** */
		USER_SPECIFIED("User specified");

		private String displayName;

		/**
		 * Construct the {@code PasswordOption} with the given display name.
		 * 
		 * @param displayName
		 *            the name to display, in drop down boxes, etc.
		 */
		private PasswordOption(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}

	/**
	 * An {@code Enum} representation of the different ways the username that
	 * will be stored in the database can be specified.
	 */
	public enum NameInDBOption {
		/** */
		FROM_ENVIRONMENT("From Environment"),
		/** */
		SAME_AS_USERNAME("Same as Username"),
		/** */
		USER_SPECIFIED("User specified");

		private String displayName;

		/**
		 * Construct the {@code NameInDBOption} with the given display name.
		 * 
		 * @param displayName
		 *            the name to display, in drop down boxes, etc.
		 */
		private NameInDBOption(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}

	/**
	 * An {@code Enum} representation of the different ways the username used to
	 * connect to the database can be specified.
	 */
	public enum UsernameOption {
		/** */
		FROM_ENVIRONMENT("From Environment"),
		/** */
		ASK_ON_CONNECT("Ask at connection time"),
		/** */
		FROM_DATABASE("From database config"),
		/** */
		USER_SPECIFIED("User specified");

		private String displayName;

		/**
		 * Construct the {@code UsernameOption} with the given display name.
		 * 
		 * @param displayName
		 *            the name to display, in drop down boxes, etc.
		 */
		private UsernameOption(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(Configuration.class);

	private final DatabaseProperties dbProperties;
	private final Mode mode;
	private final Properties properties = new Properties();

	/**
	 * Construct a {@code Configuration} instance with the given database
	 * properties and mode.
	 * 
	 * @param dbProperties
	 *            The properties used when a {@code FROM_DATABASE} option is
	 *            given.
	 * @param mode
	 *            the mode the configuration is for; each mode has it's own
	 *            configuration file.
	 */
	public Configuration(DatabaseProperties dbProperties, Mode mode) {
		this.dbProperties = dbProperties;
		this.mode = mode;
		loadConfigFile();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 *            the {@code Configuration} to copy.
	 */
	public Configuration(Configuration other) {
		dbProperties = other.dbProperties;
		mode = other.mode;
		properties.putAll(other.properties);
	}

	/**
	 * Update this {@code Configuration} using all the properties stored in the
	 * given {@code Configuration}.
	 * 
	 * @param other
	 *            the configuration to update from.
	 */
	public void updateFrom(Configuration other) {
		properties.clear();
		properties.putAll(other.properties);
	}

	/**
	 * Return the mode that this {@code Configuration} is set to.
	 * 
	 * @return the mode.
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Save this {@code Configuration} to it's configuration file.
	 */
	public void saveConfigFile() {
		final File dir = new File(System.getProperty("user.home"),
				Constants.Config.DIRECTORY);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				LOG.error("Error creating configuration directory " + dir);
				throw new RuntimeException(
						"Error creating configuration directory " + dir);
			}
		}

		final String fileName = configFileName();
		final File configFile = new File(System.getProperty("user.home"),
				fileName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(configFile);
			try {
				properties.storeToXML(out, "PMAT Configuration File");
			} catch (IOException ex) {
				LOG.error("Error saving configuration file " + configFile, ex);
			}
		} catch (FileNotFoundException ex) {
			LOG.error("Error opening configuration file " + configFile, ex);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				LOG.error("Error closing configuration file " + configFile, ex);
			}
		}
	}

	/**
	 * Set the given property to the given value.
	 * 
	 * @param name
	 *            the name of the property to set.
	 * @param value
	 *            the value to set the property to.
	 */
	public void setProperty(Key name, String value) {
		if (name.resourceKey == null) {
			throw new IllegalArgumentException("Cannot set property " + name);
		}
		properties.setProperty(name.resourceKey, value);
	}

	/**
	 * Returns a string of the property passed in or null for no value set.
	 * 
	 * For database properties it will query the {@code DatabaseProperties} if
	 * the {@code FROM_DATABASE} option is set for the respective {@code
	 * _OPTION} property.
	 * 
	 * @param name
	 *            the property to get.
	 * @return value of the property.
	 */
	public String getProperty(Key name) {
		switch (name) {
		case DB_CONNECTION_URL:
			return dbProperties.getConnectionURL();
		case NAME_IN_DATABASE:
			return getNameInDatabase();
		case DB_USERNAME:
			return getUserName();
		case DB_PASSWORD:
			return getPassword();
		case DB_DRIVER:
			return dbProperties.getDriverName();
		case DB_SCHEMA:
			switch (mode) {
			case DEVELOPMENT:
				return dbProperties.getSchema() + "_dev";
			case NORMAL:
				return dbProperties.getSchema();
			case TEST:
				return dbProperties.getSchema() + "_test";
			default:
				throw new IllegalStateException("Unknown mode " + mode);
			}
		default:
			return properties.getProperty(name.resourceKey);
		}
	}

	/**
	 * Return the name that should be stored in the database, using the {@code
	 * NAME_IN_DATABASE_OPTION} to determine from where to retrieve this.
	 * 
	 * @return the name to store in the database.
	 */
	private String getNameInDatabase() {
		final NameInDBOption option = NameInDBOption
				.valueOf(getProperty(Key.NAME_IN_DATABASE_OPTION));

		switch (option) {
		case FROM_ENVIRONMENT:
			return System.getProperty("user.name", "");
		case SAME_AS_USERNAME:
			return getUserName();
		case USER_SPECIFIED:
			return getProperty(Key.NAME_IN_DATABASE_SPECIFIED);
		default:
			throw new IllegalStateException("Unknown name in DB option "
					+ option);
		}
	}

	/**
	 * Return the username used to connect to the database, using the {@code
	 * DB_USERNAME_OPTION} to determine from where to retrieve this.
	 * 
	 * @return the database username.
	 */
	private String getUserName() {
		final UsernameOption option = UsernameOption
				.valueOf(getProperty(Key.DB_USERNAME_OPTION));

		switch (option) {
		case ASK_ON_CONNECT:
			String username = null;
			while (username == null || username.isEmpty()) {
				username = JOptionPane.showInputDialog(null,
						"Please provide a database login name",
						"Database Username", JOptionPane.QUESTION_MESSAGE);
			}
			return username;
		case FROM_ENVIRONMENT:
			return System.getProperty("user.name");
		case FROM_DATABASE:
			return dbProperties.getUserName();
		case USER_SPECIFIED:
			return getProperty(Key.DB_USERNAME_SPECIFIED);
		default:
			throw new IllegalStateException("Unknown user name option "
					+ option);
		}
	}

	/**
	 * Return the password used to connect to the database, using the {@code
	 * DB_PASSWORD_OPTION} to determine from where to retrieve this.
	 * 
	 * @return the database password.
	 */
	private String getPassword() {
		final PasswordOption option = PasswordOption
				.valueOf(getProperty(Key.DB_PASSWORD_OPTION));

		switch (option) {
		case ASK_ON_CONNECT:
			String password = null;
			while (password == null || password.isEmpty()) {
				password = JOptionPane.showInputDialog(null,
						"Please provide a database password",
						"Database Password", JOptionPane.QUESTION_MESSAGE);
			}
			return password;
		case FROM_DATABASE:
			return dbProperties.getPassword();
		case USER_SPECIFIED:
			return getProperty(Key.DB_PASSWORD_SPECIFIED);
		case NONE:
			return null;
		default:
			throw new IllegalStateException("Unknown user name option "
					+ option);
		}
	}

	/**
	 * Return the name of the configuration file to load/save, which depends on
	 * which mode this {@code Configuration} is in.
	 * 
	 * @return the configuration file name.
	 */
	private String configFileName() {
		String fileName = Constants.Config.DIRECTORY + File.separator;
		switch (mode) {
		case NORMAL:
			fileName += Constants.Config.NORMAL_FILE
					+ Constants.Config.EXTENSTION;
			break;
		case DEVELOPMENT:
			fileName += Constants.Config.DEVELOPMENT_FILE
					+ Constants.Config.EXTENSTION;
			break;
		case TEST:
			fileName += Constants.Config.TEST_FILE
					+ Constants.Config.EXTENSTION;
			break;
		default:
			throw new IllegalStateException("Unknown configuration mode");
		}
		return fileName;
	}

	/**
	 * Load this {@code Configuration} from it's configuration file or, if that
	 * doesn't exist, from the default configuration file.
	 */
	private void loadConfigFile() {
		String fileName = configFileName();
		final File configFile = new File(System.getProperty("user.home"),
				fileName);
		final InputStream in;
		if (configFile.exists() && configFile.canRead()) {
			try {
				in = new FileInputStream(configFile);
			} catch (FileNotFoundException ex) {
				LOG.error("Error opening configuration file " + fileName, ex);
				throw new RuntimeException("Error opening configuration file "
						+ fileName, ex);
			}
		} else {
			LOG.warn("No user configuration found, using default");
			final String modeFile = (mode == Mode.TEST ? Constants.Config.TEST_FILE
					: Constants.Config.DEFAULT_FILE);
			fileName = modeFile + Constants.Config.EXTENSTION;
			in = ClassLoader.getSystemResourceAsStream(fileName);
			if (in == null) {
				LOG.error("Error opening default configuration");
				throw new RuntimeException(
						"Error opening default configuration");
			}
		}
		try {
			properties.loadFromXML(in);
		} catch (IOException ex) {
			LOG.error("Error loading configuration file " + fileName, ex);
			throw new RuntimeException("Error loading configuration file "
					+ fileName, ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				LOG.error("Error closing configuration file " + fileName, ex);
			}
		}
	}
}
