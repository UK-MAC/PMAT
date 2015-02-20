package uk.co.awe.pmat.db;

import org.junit.AfterClass;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import java.util.Collection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.ApplicationException;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class RunImportTest {
    
    private static DatabaseConnection DB_CONNECTION;
    private static TestDatabase TD;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DB_CONNECTION = new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST));
        DatabaseManager.setDatabaseConnection(DB_CONNECTION);
        TD = new TestDatabase();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DB_CONNECTION = null;
        TD = null;
    }
    
    @Before
    public void setUp() throws Exception {
        TD.loadData();
    }
    
    @After
    public void tearDown() throws Exception {
        TD.clearData();
        DB_CONNECTION.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="test_import_run_without_subruns">
    @Test
    public void test_import_run_without_subruns() throws DatabaseException, SQLException, URISyntaxException, IOException, ApplicationException {
              
        int numRows = TD.getRowCount(TestDatabase.Table.Run);
        
        File importFile = new File(ClassLoader.getSystemClassLoader().getResource("ExportFileWithoutSubruns.prf").toURI());
        assertNotNull(importFile);
        
        final List<Run> runs = new ArrayList<>();
        
        new XMLSerialiser().deserialiseRuns(importFile, true, new XMLSerialiser.LoadedAction() {
            @Override
            public void doAction(XMLSerialisable node) {
                runs.add((Run) node);
            }
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        
        assertThat(runs.size(), equalTo(1));
        for (Run run : runs) {
            DatabaseManager.getConnection().save(run);
        }
        
        List<Run> dbRuns = DatabaseManager.getConnection().getRuns();
        assertThat(dbRuns.size(), equalTo(numRows + 1));
        
        int newNumRows = TD.getRowCount(TestDatabase.Table.Run);
        assertThat(newNumRows, equalTo(numRows + 1));

        Collection<RunData> dbRunData = DatabaseManager.getConnection().getDataSets(dbRuns.get(numRows));
        assertThat(dbRunData.size(), equalTo(0));
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="test_import_run_with_subruns">
    @Test
    public void test_import_run_with_subruns() throws DatabaseException, SQLException, URISyntaxException, IOException {
              
        int numRows = TD.getRowCount(TestDatabase.Table.Run);
        
        File importFile = new File(ClassLoader.getSystemClassLoader().getResource("ExportFileWithSubruns.prf").toURI());
        assertNotNull(importFile);
        
        final List<Run> runs = new ArrayList<>();
        
        new XMLSerialiser().deserialiseRuns(importFile, true, new XMLSerialiser.LoadedAction() {
            @Override
            public void doAction(XMLSerialisable node) {
                runs.add((Run) node);
            }
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        
        assertThat(runs.size(), equalTo(1));
        for (Run run : runs) {
            DatabaseManager.getConnection().save(run);
        }
        
        List<Run> dbRuns = DatabaseManager.getConnection().getRuns();
        assertThat(dbRuns.size(), equalTo(numRows + 1));
        
        int newNumRows = TD.getRowCount(TestDatabase.Table.Run);
        assertThat(newNumRows, equalTo(numRows + 1));
        
        Collection<RunData> dbRunData = DatabaseManager.getConnection().getDataSets(dbRuns.get(numRows));
        assertThat(dbRunData.size(), equalTo(1));
        
        RunData runData = dbRunData.iterator().next();
        assertThat(runData.getParameters().size(), equalTo(57));
        assertThat(runData.getResults().size(), equalTo(173));
    }// </editor-fold>
}
