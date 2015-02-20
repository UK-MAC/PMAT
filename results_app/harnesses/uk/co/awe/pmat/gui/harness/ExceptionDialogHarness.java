package uk.co.awe.pmat.gui.harness;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import uk.co.awe.pmat.gui.ExceptionDialog;

/**
 *
 * @author AWE Plc
 */
public class ExceptionDialogHarness {

    public static void main(String[] args) {
     
        final JFrame main = new JFrame();
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setVisible(false);
                main.dispose();
            }
        });
        main.add(exitButton);
        main.setBounds(100, 100, 400, 400);
        main.setVisible(true);
        
        Exception ex = new Exception(new Exception(new Exception(new Exception(new Exception(new Exception(new Exception(new Exception())))))));
        ex.fillInStackTrace();

        ExceptionDialog.showUncaughtExceptionDialog(main, ex);

        ex = new Exception("Some very long message that would cause the exception dialog to go right across the screen but should be wrapped.");
        ExceptionDialog.showExceptionDialog(ex, "Why?!");
    }
    
}
