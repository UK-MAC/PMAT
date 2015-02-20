package uk.co.awe.pmat.db.jdbc;

import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.DatabaseSetup;

public class JdbcDatabaseSetup implements DatabaseSetup {

    @Override
    public void setup() {
        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.TEST);
        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(config));
    }

    @Override
    public void closeDown() {
        DatabaseManager.setDatabaseConnection(null);
    }

    @Override
    public void setupClass() {}
    
}
