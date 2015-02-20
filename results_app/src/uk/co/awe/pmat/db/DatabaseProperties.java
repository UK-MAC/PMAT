package uk.co.awe.pmat.db;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public interface DatabaseProperties {
	/**
	 * Return the URL used to connect to the database.
	 * 
	 * @return the database URL.
	 */
	String getConnectionURL();

	/**
	 * The name of the driver used to connect to the database.
	 * 
	 * @return the driver name.
	 */
	String getDriverName();

	/**
	 * The name of the database schema being used.
	 * 
	 * @return the schema name.
	 */
	String getSchema();

	/**
	 * The username used to connect to the database.
	 * 
	 * @return the database username.
	 */
	String getUserName();

	/**
	 * The password used to connect to the database.
	 * 
	 * @return the database password.
	 */
	String getPassword();
}
