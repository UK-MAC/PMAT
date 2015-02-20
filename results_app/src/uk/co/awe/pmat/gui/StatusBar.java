package uk.co.awe.pmat.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import uk.co.awe.pmat.Constants;

/**
 * A progress bar to show the state of any work happening in the background.
 * 
 * @author AWE Plc copyright 2013
 */
final class StatusBar extends JPanel {

	/**
	 * Creates new form StatusBar.
	 */
	StatusBar(final MainPanel mainPanel) {
		initComponents();
		cancelButton.setAction(new AbstractAction(
				Constants.Gui.STATUS_BAR_CANCEL_TEXT,
				Constants.Gui.STATUS_BAR_CANCEL_ICON) {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.cancelWorkers();
			}
		});
		cancelButton.setPreferredSize(new Dimension(
				Constants.Gui.STATUS_BAR_CANCEL_ICON.getIconWidth() + 10,
				Constants.Gui.STATUS_BAR_CANCEL_ICON.getIconHeight() + 10));
		cancelButton.setEnabled(false);
	}

	/**
	 * Set the progress bar going to indicate that work is going on in the
	 * background.
	 */
	void setAsBusy() {
		progressBar.setIndeterminate(true);
		cancelButton.setEnabled(true);
	}

	/**
	 * Stop the progress bar to indicate that all work in the background has
	 * finished.
	 */
	void setAsNotBusy() {
		progressBar.setIndeterminate(false);
		progressBar.setValue(0);
		cancelButton.setEnabled(false);
	}

	void setProgress(Integer progress) {
		progressBar.setIndeterminate(false);
		progressBar.setValue(progress);
	}

	/**
	 * This method is called from within the constructor to initialise the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		cancelButton = new javax.swing.JButton();

		setLayout(new java.awt.BorderLayout());
		add(progressBar, java.awt.BorderLayout.CENTER);

		cancelButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
		add(cancelButton, java.awt.BorderLayout.EAST);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private final javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar();
	// End of variables declaration//GEN-END:variables

}
