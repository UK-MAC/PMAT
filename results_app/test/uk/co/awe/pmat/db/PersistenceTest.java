package uk.co.awe.pmat.db;

import org.junit.AfterClass;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class PersistenceTest {

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

    // <editor-fold defaultstate="collapsed" desc="test_save_compiler">
    @Test
    public void test_save_compiler() throws DatabaseException, SQLException {
              
        long numRows = TD.getRowCount(TestDatabase.Table.Compiler);
        
        String name = "Test Name";
        String vendor = "Test Vendor";
        int versionMajor = 1;
        int versionMinor = 2;
        int versionBuild = 3;
        
        Compiler compiler = new Compiler(name, vendor, versionMajor, versionMinor, versionBuild);
        
        DatabaseManager.getConnection().save(compiler);

        long newNumRows = TD.getRowCount(TestDatabase.Table.Compiler);
        
        assertThat(newNumRows, equalTo(numRows + 1));
        
        String query = String.format("SELECT Vendor FROM Compiler WHERE Name = '%s'", name);
        assertThat(vendor, equalTo(TD.executeQuery(query, String.class)));
        
        query = String.format("SELECT VersionMajor FROM Compiler WHERE Name = '%s'", name);
        assertThat(versionMajor, equalTo(TD.executeQuery(query, Integer.class)));
        
        query = String.format("SELECT VersionMinor FROM Compiler WHERE Name = '%s'", name);
        assertThat(versionMinor, equalTo(TD.executeQuery(query, Integer.class)));
        
        query = String.format("SELECT VersionBuild FROM Compiler WHERE Name = '%s'", name);
        assertThat(versionBuild, equalTo(TD.executeQuery(query, Integer.class)));

    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="test_save_machine">
    @Test
    public void test_save_machine() throws DatabaseException, SQLException {
              
        long numRows = TD.getRowCount(TestDatabase.Table.Machine);
        
        String name = "Test Name";
        String vendor = "Test Vendor";
        
        Machine machine = new Machine(name, vendor);
        
        DatabaseManager.getConnection().save(machine);

        long newNumRows = TD.getRowCount(TestDatabase.Table.Machine);
        
        assertThat(newNumRows, equalTo(numRows + 1));

    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="test_save_MPI">
    @Test
    public void test_save_MPI() throws DatabaseException, SQLException {
              
        long numRows = TD.getRowCount(TestDatabase.Table.MPI);
        
        String name = "Test Name";
        String vendor = "Test Vendor";
        int versionMajor = 1;
        int versionMinor = 2;
        int versionBuild = 3;
        
        Mpi mpi = new Mpi(name, vendor, versionMajor, versionMinor, versionBuild);
        
        DatabaseManager.getConnection().save(mpi);

        long newNumRows = TD.getRowCount(TestDatabase.Table.MPI);
        
        assertThat(newNumRows, equalTo(numRows + 1));

    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="test_save_operating_system">
    @Test
    public void test_save_operating_system() throws DatabaseException, SQLException {
              
        long numRows = TD.getRowCount(TestDatabase.Table.OperatingSystem);
        
        String name = "Test Name";
        String vendor = "Test Vendor";
        int versionMajor = 1;
        int versionMinor = 2;
        int versionBuild = 3;
        int versionBuildMinor = 4;
        String kernel = "linux-x86-12345";
        
        OperatingSystem os = new OperatingSystem(name, vendor, versionMajor, versionMinor, versionBuild, versionBuildMinor, kernel);
        
        DatabaseManager.getConnection().save(os);

        long newNumRows = TD.getRowCount(TestDatabase.Table.OperatingSystem);
        
        assertThat(newNumRows, equalTo(numRows + 1));

    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="test_save_processor">
    @Test
    public void test_save_processor() throws DatabaseException, SQLException {
              
        long numRows = TD.getRowCount(TestDatabase.Table.Processor);
        
        String name = "Test Name";
        String vendor = "Test Vendor";
        String architecture = "x86_64";
        int cores = 8;
        int threadsPerCore = 2;
        int clockSpeed = 2500;
        
        Processor proc = new Processor(name, vendor, architecture, cores, threadsPerCore, clockSpeed);
        
        DatabaseManager.getConnection().save(proc);

        long newNumRows = TD.getRowCount(TestDatabase.Table.Processor);
        
        assertThat(newNumRows, equalTo(numRows + 1));

    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="test_save_application">
    @Test
    public void test_save_application() throws DatabaseException, SQLException {
              
        long numRows = TD.getRowCount(TestDatabase.Table.Application);
        
        String name = "Test Name";
        int versionMajor = 1;
        int versionMinor = 2;
        int versionBuild = 3;
        String versionCode = "2D";
        String description = "Test Application";
        boolean isPrivate = false;
        
        Application app = new Application(name, versionMajor, versionMinor, versionBuild, versionCode, description, isPrivate);
        
        DatabaseManager.getConnection().save(app);

        long newNumRows = TD.getRowCount(TestDatabase.Table.Application);
        
        assertThat(newNumRows, equalTo(numRows + 1));

    }// </editor-fold>

}