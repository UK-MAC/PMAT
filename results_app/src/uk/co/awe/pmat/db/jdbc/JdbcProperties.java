package uk.co.awe.pmat.db.jdbc;

import uk.co.awe.pmat.db.DatabaseProperties;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public final class JdbcProperties implements DatabaseProperties {

	public JdbcProperties() {
	}

	@Override
	public String getConnectionURL() {
		return "jdbc:mysql://%MYSQL_ADDRESS%";
	}

	@Override
	public String getDriverName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	public String getSchema() {
		return "ichnaea";
	}

	@Override
	public String getUserName() {
		return "%USERNAME%";
	}

	@Override
	public String getPassword() {
		return "%PASSWORD%";
	}

}
