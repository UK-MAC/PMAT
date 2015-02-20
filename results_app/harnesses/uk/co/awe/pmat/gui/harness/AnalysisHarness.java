package uk.co.awe.pmat.gui.harness;

import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.models.analysis.AnalysisModel;
import uk.co.awe.pmat.Configuration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.UIManager;
import uk.co.awe.pmat.gui.analysis.AnalysisPanel;
import static javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * @author AWE Plc
 */
public class AnalysisHarness extends JFrame implements PropertyChangeListener {

    public AnalysisHarness() {
        super("NewAnalysisHarness");
        Configuration config = new Configuration(new JdbcProperties(), Configuration.Mode.DEVELOPMENT);
        this.setContentPane(new AnalysisPanel(new AnalysisModel(config, this), this));
    }
    
    public static void main(String[] args) {
        for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if (laf.getName().equals("Nimbus")) {
                try {
                    UIManager.setLookAndFeel(laf.getClassName());
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }

        JFrame frame = new AnalysisHarness();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(200, 200, 1000, 1000);
        frame.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.err.println(String.format("%s: %s -> %s", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
    }

}
