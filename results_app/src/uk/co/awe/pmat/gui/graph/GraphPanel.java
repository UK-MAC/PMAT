package uk.co.awe.pmat.gui.graph;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.graph.GraphModel;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.GuiPanel;
import uk.co.awe.pmat.gui.events.EventHub;

/**
 * A simple container panel that is used to display the resultant plot created
 * by the {@code GraphModel}.
 * 
 * @author AWE Plc copyright 2013
 */
public final class GraphPanel extends GuiPanel {

	private static final Logger LOG = LoggerFactory.getLogger(GraphPanel.class);

	private static final int RESIZING_WAIT = 500; // miliseconds

	private final GraphModel graphModel;
	private final GraphConfigPanel axisConfigPanel;
	private final PropertyChangeListener workerListener;

	private Thread resizingThread;
	private boolean resizing = false;

	/**
	 * Create a new {@code GraphPanel}.
	 * 
	 * @param graphModel
	 *            the model driving this panel.
	 * @param workerListener
	 *            a listener to be kept informed of the state of any {@code
	 *            SwingWorker}s launched.
	 */
	public GraphPanel(GraphModel graphModel, EventHub eventHub,
			PropertyChangeListener workerListener) {
		this.graphModel = graphModel;
		this.workerListener = workerListener;

		initComponents();
		axisConfigPanel = new GraphConfigPanel(graphModel, eventHub);
		axisConfigBar.add(axisConfigPanel);

		graphModel.addChangeListener((ChangeListener) this);

		resizeImage();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		reloadImage();
	}

	/**
	 * Reload the image displayed on the panel.
	 */
	private void reloadImage() {
		if (graphModel.readyToPlot()) {
			imageLabel.setIcon(null);
			imageLabel.setText("Loading...");
			imageLabel.repaint();

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				Throwable caught = null;

				@Override
				protected Void doInBackground() throws Exception {
					try {
						graphModel.doPlot();
					} catch (Throwable ex) {
						LOG.error("Failed to run plotter", ex);
						caught = ex;
					}
					return null;
				}

				@Override
				protected void done() {
					imageLabel.setText(null);
					if (caught == null) {
						Image img = graphModel.getImage();
						if (img != null) {
							imageLabel.setSize(getSize());
							imageLabel.setIcon(new ImageIcon(img));
							imageLabel.repaint();
						}
					} else {
						imageLabel.setText(caught.getMessage());
					}
				}
			};
			worker.addPropertyChangeListener(workerListener);
			worker.execute();
		}
	}

	/**
	 * Resize the image, reloading the image with the new size if the scaling
	 * goes beyond a certain amount.
	 */
	private synchronized void resizeImage() {
		if (isResizing()) {
			// If the image is still being drawn, wait until it's finished and
			// then try again.
			if (resizingThread == null) {
				resizingThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(RESIZING_WAIT);
						} catch (InterruptedException ex) {
							LOG.debug("Resizing image thread interupted");
						}
						if (graphModel.setImageSize(imageLabel.getSize())) {
							reloadImage();
						}
						setResizing(false);
						resizingThread = null;
					}
				}, "GraphPanel resizing thread");
				resizingThread.start();
			}
		} else {
			setResizing(true);
			if (graphModel.setImageSize(imageLabel.getSize())) {
				reloadImage();
			}
			setResizing(false);
		}
	}

	/**
	 * Returns whether the panel is still resizing.
	 * 
	 * @return {@code true} if the panel is resizing, {@code false} otherwise.
	 */
	private synchronized boolean isResizing() {
		return resizing;
	}

	/**
	 * Set whether the panel is resizing.
	 * 
	 * @param value
	 *            {@code true} if the panel is resizing, {@code false}
	 *            otherwise.
	 */
	private synchronized void setResizing(boolean value) {
		resizing = value;
	}

	/**
	 * Save the currently displayed plot as a PNG file.
	 */
	private void saveGraphAsPng() {
		int chooserReturn = saveAsDialog.showSaveDialog(this);
		if (chooserReturn == JFileChooser.APPROVE_OPTION) {
			File path = saveAsDialog.getSelectedFile();
			try {
				graphModel.saveAsPng(path);
			} catch (IOException ex) {
				ExceptionDialog.showExceptionDialog(ex,
						"Error exporting as PNG.");
			}
		}
	}

/**
     * Save the currently displayed plot as {@code GnuPlot) files.
     *
     * @param forTeX {@code true} if the files should produce an image to be
     * imported into a {@code TeX} document, or {@code false} if the files
     * should produce a standard PNG output.
     */
	private void saveGraphAsGnuplotFiles(boolean forTeX) {
		int chooserReturn = saveAsDialog.showSaveDialog(this);
		if (chooserReturn == JFileChooser.APPROVE_OPTION) {
			File path = saveAsDialog.getSelectedFile();
			try {
				graphModel.saveAsPlotterFiles(path, forTeX);
			} catch (IOException ex) {
				ExceptionDialog.showExceptionDialog(ex,
						"Error exporting as PNG.");
			}
		}
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

		saveGraphPopupMenu = new javax.swing.JPopupMenu();
		saveAsGnuplotFilesMenuItem = new javax.swing.JMenuItem();
		saveAsPngMenuItem = new javax.swing.JMenuItem();
		saveAsTeXGnuplotFilesMenuItem = new javax.swing.JMenuItem();
		saveAsDialog = new javax.swing.JFileChooser();

		saveAsGnuplotFilesMenuItem.setText("Save as GnuPlot Files");
		saveAsGnuplotFilesMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveAsGnuplotFilesMenuItemActionPerformed(evt);
					}
				});
		saveGraphPopupMenu.add(saveAsGnuplotFilesMenuItem);

		saveAsPngMenuItem.setText("Save as PNG");
		saveAsPngMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveAsPngMenuItemActionPerformed(evt);
					}
				});
		saveGraphPopupMenu.add(saveAsPngMenuItem);

		saveAsTeXGnuplotFilesMenuItem.setText("Save as TeX GnuPlot files");
		saveAsTeXGnuplotFilesMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveAsTeXGnuplotFilesMenuItemActionPerformed(evt);
					}
				});
		saveGraphPopupMenu.add(saveAsTeXGnuplotFilesMenuItem);

		setLayout(new java.awt.BorderLayout());

		axisConfigBar.setOrientation(1);
		axisConfigBar.setRollover(true);
		add(axisConfigBar, java.awt.BorderLayout.LINE_END);

		imagePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5),
				javax.swing.BorderFactory.createTitledBorder("")));
		imagePanel.setLayout(new java.awt.BorderLayout());

		imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		imageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				imageLabelMouseClicked(evt);
			}
		});
		imageLabel.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				imageLabelComponentResized(evt);
			}
		});
		imagePanel.add(imageLabel, java.awt.BorderLayout.CENTER);

		add(imagePanel, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Function called when the image label is resized.
	 * 
	 * @param evt
	 *            the resize event.
	 */
	private void imageLabelComponentResized(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_imageLabelComponentResized
		resizeImage();
	}// GEN-LAST:event_imageLabelComponentResized

	/**
	 * Function called when the "Save As Gnuplot Files" menu item is selected.
	 * 
	 * @param evt
	 *            the selection event.
	 */
	private void saveAsGnuplotFilesMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveAsGnuplotFilesMenuItemActionPerformed
		saveGraphAsGnuplotFiles(false);
	}// GEN-LAST:event_saveAsGnuplotFilesMenuItemActionPerformed

	/**
	 * Function called when the "Save As PNG" menu item is selected.
	 * 
	 * @param evt
	 *            the selection event.
	 */
	private void saveAsPngMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveAsPngMenuItemActionPerformed
		saveGraphAsPng();
	}// GEN-LAST:event_saveAsPngMenuItemActionPerformed

	/**
	 * Function called when the "Save As TeX Files" menu item is selected.
	 * 
	 * @param evt
	 *            the selection event.
	 */
	private void saveAsTeXGnuplotFilesMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveAsTeXGnuplotFilesMenuItemActionPerformed
		saveGraphAsGnuplotFiles(true);
	}// GEN-LAST:event_saveAsTeXGnuplotFilesMenuItemActionPerformed

	/**
	 * Function called when the graph image is clicked.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void imageLabelMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_imageLabelMouseClicked
		if (evt.getButton() == MouseEvent.BUTTON3) {
			saveGraphPopupMenu.show(this, evt.getX(), evt.getY());
		}
	}// GEN-LAST:event_imageLabelMouseClicked

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JToolBar axisConfigBar = new javax.swing.JToolBar();
	private final javax.swing.JLabel imageLabel = new javax.swing.JLabel();
	private final javax.swing.JPanel imagePanel = new javax.swing.JPanel();
	private javax.swing.JFileChooser saveAsDialog;
	private javax.swing.JMenuItem saveAsGnuplotFilesMenuItem;
	private javax.swing.JMenuItem saveAsPngMenuItem;
	private javax.swing.JMenuItem saveAsTeXGnuplotFilesMenuItem;
	private javax.swing.JPopupMenu saveGraphPopupMenu;
	// End of variables declaration//GEN-END:variables

}
