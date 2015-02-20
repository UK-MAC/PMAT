package uk.co.awe.pmat.gui.harness;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.addresults.AddResultsView;
import uk.co.awe.pmat.gui.models.AddResultsModel;

/**
 *
 * @author AWE Plc
 */
public class AddResultsHarness implements PropertyChangeListener {

    public AddResultsHarness() {
        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.DEVELOPMENT);

        JFrame main = new JFrame();

        AddResultsModel arm = new AddResultsModel(config);
                
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setContentPane(new AddResultsView(arm, config, this));
        main.setBounds(100, 100, 600, 800);
        main.setVisible(true);
    }

    public static void main (String[] args) {
        new AddResultsHarness();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.err.println(String.format("%s: %s -> %s", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
    }
}
