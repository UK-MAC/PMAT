package uk.co.awe.pmat.gui.harness;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.configpanels.MetaDataView;
import uk.co.awe.pmat.gui.models.MetaDataDisplayModel;

/**
 *
 * @author AWE Plc
 */
public class ConfigHarness implements PropertyChangeListener {

    public ConfigHarness() {
        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.DEVELOPMENT);

        JFrame main = new JFrame();

        MetaDataDisplayModel cdm = new MetaDataDisplayModel();

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setContentPane(new MetaDataView(cdm));
        main.setBounds(100, 100, 1000, 1000);
        main.setVisible(true);
    }

    public static void main (String[] args) {
        new ConfigHarness();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.err.println(String.format("%s: %s -> %s", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
    }
}