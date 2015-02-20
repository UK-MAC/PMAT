package uk.co.awe.pmat.gui.harness;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import uk.co.awe.pmat.ApplicationException;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.models.ImportModel;

/**
 *
 * @author AWE Plc
 */
public class ImportRunsHarness implements PropertyChangeListener {

    public static void main(String[] args) {
        new ImportRunsHarness();
    }

    public ImportRunsHarness() {
        
        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.DEVELOPMENT);

        ImportModel im = new ImportModel(this);

        try {
            im.loadFromFile();
            im.saveIntoDB();
        } catch (ApplicationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.err.println(evt);
    }
    
}
