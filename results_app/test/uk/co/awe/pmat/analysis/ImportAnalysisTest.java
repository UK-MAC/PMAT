package uk.co.awe.pmat.analysis;

import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.gui.models.analysis.ImportAnalysesModel;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class ImportAnalysisTest {

    public ImportAnalysisTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // <editor-fold defaultstate="collapsed" desc="test_load_analysis_file">
    @Test
    public void test_load_analysis_file() throws IOException, URISyntaxException {

        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST)));
        ImportAnalysesModel iam = new ImportAnalysesModel();

        URL url = ClassLoader.getSystemResource("AnalysisExportFile.paf");
        assertNotNull(url);

        iam.loadAnalysesFromFile(new File(url.toURI()));
        iam.updateTable();

        TableModel tm = iam.getTableModel();

        assertThat(tm.getRowCount(), equalTo(1));       
    }// </editor-fold>

}