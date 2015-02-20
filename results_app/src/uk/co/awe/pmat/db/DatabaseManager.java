package uk.co.awe.pmat.db;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public class DatabaseManager {

	private static DatabaseConnection databaseConnection;

	/**
	 * Set the database connection that will be used in the application.
	 * 
	 * @param databaseConnection
	 *            the database connection.
	 */
	public static void setDatabaseConnection(
			DatabaseConnection databaseConnection) {
		DatabaseManager.databaseConnection = databaseConnection;
	}

	/**
	 * Return the database connection. If no connection has been set up then
	 * this will throw a {@link IllegalStateException}.
	 * 
	 * @return the database connection.
	 */
	public static DatabaseConnection getConnection() {
		if (databaseConnection == null) {
			throw new IllegalStateException(
					"Database connection not initialised");
		}
		return databaseConnection;
	}

}
