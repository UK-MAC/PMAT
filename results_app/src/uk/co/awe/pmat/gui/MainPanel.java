package uk.co.awe.pmat.gui;

import java.beans.PropertyVetoException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.gui.addresults.AddResultsView;
import uk.co.awe.pmat.gui.analysis.AnalysisPanel;
import uk.co.awe.pmat.gui.analysis.ImportAnalysesView;
import uk.co.awe.pmat.gui.configpanels.MetaDataView;
import uk.co.awe.pmat.gui.models.AddResultsModel;
import uk.co.awe.pmat.gui.models.ImportModel;
import uk.co.awe.pmat.gui.models.MetaDataDisplayModel;
import uk.co.awe.pmat.gui.models.ViewResultsModel;
import uk.co.awe.pmat.gui.models.analysis.AnalysisModel;
import uk.co.awe.pmat.gui.models.analysis.ImportAnalysesModel;

/**
 * The main application panel. This where the graphs will be displayed, along
 * with the panels needed to select that data, axes, labels, etc.
 * 
 * @author AWE Plc copyright 2013
 */
final class MainPanel extends JPanel implements PropertyChangeListener {

	private static final Logger LOG = LoggerFactory.getLogger(MainPanel.class);

	private final StatusBar statusBar = new StatusBar(this);
	private final Set<SwingWorker> workers = Collections
			.newSetFromMap(new IdentityHashMap<SwingWorker, Boolean>());
	private final JInternalFrame innerFrame;
	private final Configuration config;

	private AnalysisPanel analysis;

	private JPanel addResultsView;
	private JPanel viewResultsView;
	private JPanel configDataView;
	private JPanel importView;
	private JPanel importAnalysesView;

	/**
	 * Create a new {@code MainPanel}.
	 * 
	 * @param dbMapping
	 *            the mapping used to communicate with the database.
	 * @param config
	 *            the application configuration.
	 */
	MainPanel(Configuration config) {
		initComponents();
		this.config = config;
		innerFrame = new JInternalFrame("", true, true, true, false);
		innerFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

		loadAnalysisView(null);
	}

	/**
	 * Create and display a new {@code AnalysisPanel} for the given model.
	 * 
	 * @param model
	 *            the analysis to display.
	 */
	void loadAnalysisView(AnalysisModel model) {
		if (model == null) {
			model = new AnalysisModel(config, this);
		}
		analysis = new AnalysisPanel(model, this);

		Dimension size = centerPanel.getSize();
		if (size.getWidth() > 0 && size.getHeight() > 0) {
			analysis.setSize(size);
		} else {
			analysis.setSize(600, 600);
		}

		centerPanel.removeAll();
		centerPanel.add(analysis, JLayeredPane.DEFAULT_LAYER);

		statusPanel.removeAll();
		statusPanel.add(statusBar, BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				analysis.setSize(centerPanel.getSize());
			}
		});
	}

	/**
	 * Set the current view to show a panel for the given {@code View}.
	 * 
	 * @param view
	 *            the view to display.
	 */
	void setView(View view) {
		analysis.setSize(centerPanel.getSize());
		centerPanel.removeAll();
		centerPanel.add(analysis, JLayeredPane.DEFAULT_LAYER);
		switch (view) {
		case ADD_RESULTS:
			innerFrame.setContentPane(getAddResultsView());
			innerFrame.setTitle(view.getDisplayName());
			break;
		case EXPORT_RESULTS:
			innerFrame.setContentPane(getViewResultsView());
			innerFrame.setTitle(view.getDisplayName());
			break;
		case IMPORT_ANALYSES:
			innerFrame.setContentPane(getImportAnalysesView());
			innerFrame.setTitle(view.getDisplayName());
			break;
		case IMPORT_RESULTS:
			innerFrame.setContentPane(getImportView());
			innerFrame.setTitle(view.getDisplayName());
			break;
		case SYSTEM_CONFIG:
			innerFrame.setContentPane(getConfigDataView());
			innerFrame.setTitle(view.getDisplayName());
			break;
		default:
			throw new IllegalArgumentException("Uknown view: " + view);
		}
		innerFrame.setVisible(true);
		innerFrame.setSize(centerPanel.getSize());
		centerPanel.add(innerFrame, JLayeredPane.DRAG_LAYER);
		try {
			innerFrame.setMaximum(true);
			innerFrame.requestFocus();
		} catch (PropertyVetoException ex) {
			LOG.warn("Failed to set Maximised state: " + ex.getMessage());
		}
	}

	/**
	 * Get the add results view, creating a new one if necessary.
	 * 
	 * @return The add results view
	 */
	private JPanel getAddResultsView() {
		if (addResultsView == null) {
			AddResultsModel model = new AddResultsModel(config);
			addResultsView = new AddResultsView(model, config, this);
		}
		return addResultsView;
	}

	/**
	 * Get the view results view, creating a new one each time so that it
	 * displays the latest results.
	 * 
	 * @return The view results view
	 */
	private JPanel getViewResultsView() {
		// create a new form every time
		ViewResultsModel model = new ViewResultsModel();
		viewResultsView = new ViewResultsView(model, this);
		return viewResultsView;
	}

	/**
	 * Get the configuration view, creating a new one if necessary.
	 * 
	 * @return The configuration view
	 */
	private JPanel getConfigDataView() {
		if (configDataView == null) {
			MetaDataDisplayModel model = new MetaDataDisplayModel();
			configDataView = new MetaDataView(model);
		}
		return configDataView;
	}

	/**
	 * Get the import view, creating a new one if necessary.
	 * 
	 * @return The import view
	 */
	private JPanel getImportView() {
		if (importView == null) {
			ImportModel model = new ImportModel(this);
			importView = new ImportView(model, config);
		}
		return importView;
	}

	/**
	 * Get the import analyses view, creating a new one if necessary.
	 * 
	 * @return The import analyses view
	 */
	private JPanel getImportAnalysesView() {
		if (importAnalysesView == null) {
			ImportAnalysesModel model = new ImportAnalysesModel();
			importAnalysesView = new ImportAnalysesView(model, config, this);
		}
		return importAnalysesView;
	}

	void cancelWorkers() {
		for (SwingWorker worker : workers) {
			worker.cancel(false);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "progress":
			statusBar.setProgress((Integer) evt.getNewValue());
			break;
		case "state":
			final SwingWorker worker = (SwingWorker) evt.getSource();
			if (SwingWorker.StateValue.STARTED.equals(evt.getNewValue())) {
				workers.add(worker);
				statusBar.setAsBusy();
			} else if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
				workers.remove(worker);
				statusBar.setAsNotBusy();
			}
			break;
		default:
			break;
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

		setLayout(new java.awt.BorderLayout());

		statusPanel.setLayout(new java.awt.BorderLayout());
		add(statusPanel, java.awt.BorderLayout.SOUTH);
		add(centerPanel, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JLayeredPane centerPanel = new javax.swing.JLayeredPane();
	private final javax.swing.JPanel statusPanel = new javax.swing.JPanel();
	// End of variables declaration//GEN-END:variables

}
