package uk.co.awe.pmat.gui.harness;

import java.io.File;
import java.util.Arrays;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.models.ViewResultsModel;

/**
 *
 * @author AWE Plc
 */
public class ExportRunsHarness {

    public static void main(String[] args) throws DatabaseException {
        new ExportRunsHarness();
    }

    public ExportRunsHarness() throws DatabaseException {
        
        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.DEVELOPMENT);

        ViewResultsModel vrm = new ViewResultsModel();
        vrm.getResultsTableModel();

        try {
            vrm.exportRowsToFile(new File("export.xml"), Arrays.asList(new Long[]{ 0L, 1L, 2L }));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
