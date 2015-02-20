package uk.co.awe.pmat.gui.harness;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.ViewResultsView;
import uk.co.awe.pmat.gui.models.ViewResultsModel;

/**
 *
 * @author AWE Plc
 */
public class ViewResultsHarness implements PropertyChangeListener {

    public static void main(String[] args) throws DatabaseException {
        new ViewResultsHarness();
    }

    public ViewResultsHarness() throws DatabaseException {

        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.DEVELOPMENT);

        JFrame main = new JFrame();

        ViewResultsModel vrm = new ViewResultsModel();

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setContentPane(new ViewResultsView(vrm, this));
        main.setBounds(100, 100, 1000, 1000);
        main.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
