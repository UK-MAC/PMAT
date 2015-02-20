package uk.co.awe.pmat.gui.harness;

import javax.swing.JFrame;

/**
 *
 * @author AWE Plc
 */
public class DerivedDataHarness {

    public static void main(String[] args) {
        new DerivedDataHarness();
    }

    public DerivedDataHarness() {
//        Map<String, DerivedData> variableMap = new HashMap<String, DerivedData>();
//        Map<String, Function> functionMap = new HashMap<String, Function>();

        //DerivedDataPanel derivedDataPanel = new DerivedDataPanel(variableMap, functionMap);
        JFrame frame = new JFrame();
        //frame.setContentPane(derivedDataPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(200, 200, 800, 800);
        frame.setVisible(true);
    }
    
}
