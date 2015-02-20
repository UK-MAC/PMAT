package uk.co.awe.pmat.gui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.co.awe.pmat.gui.analysis.AnalysesDisplayDialog;
import javax.swing.UnsupportedLookAndFeelException;
import uk.co.awe.pmat.gui.models.analysis.AnalysisModel;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.db.DatabaseException;
import javax.swing.SwingWorker;
import java.util.concurrent.Callable;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static javax.swing.UIManager.LookAndFeelInfo;

/**
 * The PMAT desktop application.
 * 
 * @author AWE Plc copyright 2013
 */
public final class MainFrame extends JFrame {

	private static MainFrame mainFrame;

	private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);

	private static final int DEFAULT_SIZE_X = Constants.Application.DEFAULT_SIZE_X;
	private static final int DEFAULT_SIZE_Y = Constants.Application.DEFAULT_SIZE_Y;
	private static final int DEFAULT_POS_X = Constants.Application.DEFAULT_POS_X;
	private static final int DEFAULT_POS_Y = Constants.Application.DEFAULT_POS_Y;

	private static final JPanel loadingPanel;

	static {
		loadingPanel = new JPanel(new BorderLayout());
		loadingPanel.add(new JLabel("Loading ...", JLabel.CENTER),
				BorderLayout.CENTER);
	}

	private final Configuration config;

	private MainPanel mainPanel;

	/**
	 * Create and run the GUI.
	 * 
	 * @param configuration
	 *            the application configuration.
	 */
	public static void run(final Configuration configuration) {
		setLookAndFeel();
		mainFrame = new MainFrame(configuration);
		mainFrame.setContentPane(loadingPanel);
		mainFrame.setVisible(true);

		Thread
				.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						LOG.error("Unhandled exception", e);
					}
				});

		/*
		 * Launch a thread to initialise the application. Make sure this is
		 * launched after all Swing setup has been done by putting it on the end
		 * of the event queue.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						mainFrame.initMainPanel();
					}
				}, "Application Initialisation").start();
			}
		});

	}

	private void initMainPanel() {
		mainPanel = new MainPanel(config);
		// Once we are initialised update the GUI on the AWT event thread.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setContentPane(mainPanel);
				pack();
				setBounds();
			}
		});
	}

	/**
	 * Create a new {@code MainFrame} application panel.
	 * 
	 * @param configuration
	 *            the application configuration.
	 */
	private MainFrame(Configuration configuration) {
		initComponents();

		this.config = configuration;

		setTitle(Constants.Application.CAPTION);
		setBounds();
		setJMenuBar(new MenuBar(this));
	}

	/**
	 * Exit the application.
	 */
	void exit() {
		setVisible(false);
		dispose();
	}

	/**
	 * Display an "About" box.
	 */
	void showAboutBox() {
		new AboutBox(this).setVisible(true);
	}

	/**
	 * Display the application settings dialogue.
	 */
	void showSettings() {
		new SettingsDialog(this, true, config).setVisible(true);
	}

	/**
	 * Display the analyses dialogue.
	 */
	void showAnalyses() {
		try {
			final Callable<AnalysisModel> loadAnalysisTask = AnalysesDisplayDialog
					.showAnalysesDisplayDialog(mainPanel, config, mainPanel);
			if (loadAnalysisTask != null) {
				SwingWorker<Void, Void> analysisLoader = new SwingWorker<Void, Void>() {
					private AnalysisModel analysisModel;

					@Override
					protected Void doInBackground() throws Exception {
						try {
							analysisModel = loadAnalysisTask.call();
						} catch (DatabaseException ex) {
							ExceptionDialog.showDatabaseExceptionDialog(ex);
						} catch (Exception ex) {
							ExceptionDialog.showExceptionDialog(ex,
									"Error displaying analyses");
						}
						return null;
					}

					@Override
					protected void done() {
						mainPanel.loadAnalysisView(analysisModel);
					}
				};
				analysisLoader.addPropertyChangeListener(mainPanel);
				analysisLoader.execute();
			}
		} catch (DatabaseException ex) {
			ExceptionDialog
					.showExceptionDialog(ex, "Error displaying analyses");
		}
	}

	/**
	 * Reset the currently displayed analyses.
	 */
	void resetAnalysis() {
		mainPanel.loadAnalysisView(null);
	}

	/**
	 * Load the application user guide in an external web browser.
	 */
	void showUserGuide() {
		LOG.info("Opening user guide");
		String browser = config.getProperty(Configuration.Key.WEB_BROWSER);
		String userguide = Constants.Application.USER_GUIDE_URL;
		ProcessBuilder procBuilder = new ProcessBuilder(browser, userguide);
		try {
			procBuilder.start();
		} catch (IOException ex) {
			ExceptionDialog.showExceptionDialog(ex,
					"Cannot open userguide using browser " + browser + ".");
		}
	}

	/**
	 * Show the database connection settings dialogue.
	 */
	void showConnectionSettings() {
		ConnectionSettingsDialog.showConnectionSettingsDialog(this, config);
	}

	/**
	 * Set the main view panel to the given {@link View}.
	 * 
	 * @param view
	 *            the view to show.
	 */
	void setView(View view) {
		if (mainPanel != null) {
			mainPanel.setView(view);
		}
	}

	/**
	 * Set the frame's size and position.
	 */
	private void setBounds() {
		String maximised = config.getProperty(Configuration.Key.APP_MAXIMISED);

		if (Boolean.TRUE.toString().equals(maximised)) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			String x = config.getProperty(Configuration.Key.APP_POS_X);
			String y = config.getProperty(Configuration.Key.APP_POS_Y);
			try {
				setLocation(Integer.parseInt(x), Integer.parseInt(y));
			} catch (NumberFormatException ex) {
				setSize(DEFAULT_POS_X, DEFAULT_POS_Y);
			}

			x = config.getProperty(Configuration.Key.APP_WIDTH);
			y = config.getProperty(Configuration.Key.APP_HEIGHT);
			try {
				setSize(Integer.parseInt(x), Integer.parseInt(y));
			} catch (NumberFormatException ex) {
				setSize(DEFAULT_SIZE_X, DEFAULT_SIZE_Y);
			}
		}
	}

	/**
	 * Update the applications "Look and Feel".
	 */
	private static void setLookAndFeel() {
        final String laf = Constants.Application.LOOK_AND_FEEL;
        boolean found = false;
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (laf.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    found = true;
                    break;
                }
            }
            if (!found) {
                LOG.debug("Look and feel " + laf + " not found.");
            }
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            LOG.error("Look and Feel " + laf + " found but failed to load.", ex);
        }
    }

	/**
	 * Tidy up and save properties at application exit.
	 */
	private void atExit() {
		Boolean isMaxisised = (getExtendedState() == JFrame.MAXIMIZED_BOTH);
		config.setProperty(Configuration.Key.APP_MAXIMISED, isMaxisised
				.toString());

		Dimension size = getSize();
		config.setProperty(Configuration.Key.APP_WIDTH, Integer
				.toString(size.width));
		config.setProperty(Configuration.Key.APP_HEIGHT, Integer
				.toString(size.height));

		Point location = getLocation();
		config.setProperty(Configuration.Key.APP_POS_X, Integer
				.toString(location.x));
		config.setProperty(Configuration.Key.APP_POS_Y, Integer
				.toString(location.y));
		config.saveConfigFile();
	}

	/**
	 * Return the applications root frame.
	 * 
	 * @return the root frame of the application.
	 */
	public static JFrame getRootFrame() {
		return mainFrame;
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

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
		atExit();
	}// GEN-LAST:event_formWindowClosing

	// Variables declaration - do not modify//GEN-BEGIN:variables
	// End of variables declaration//GEN-END:variables

}
