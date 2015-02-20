package uk.co.awe.pmat;

import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.gui.MainFrame;
import uk.co.awe.pmat.utils.ArrayUtils;

/**
 * Main application class. Used to launch the command line or GUI versions of
 * the application.
 * 
 * @author AWE Plc copyright 2013
 */
public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	private Configuration configuration;

	/**
	 * Main application entry point.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	@SuppressWarnings( { "UseOfSystemOutOrSystemErr" })
	public static void main(String[] args) {
		Main app = new Main();
		try {
			app.run(args);
		} catch (ApplicationException ex) {
			System.err.println(ex.getMessage());
			LOG.error("Application error", ex);
		}
	}

	/**
	 * Run the application.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	void run(String[] args) throws ApplicationException {
		final Configuration.Mode mode;
		if (ArrayUtils.arrayContains(args, "-d")) {
			mode = Configuration.Mode.DEVELOPMENT;
		} else {
			mode = Configuration.Mode.NORMAL;
		}

		configuration = new Configuration(new JdbcProperties(), mode);
		DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(
				configuration));

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame.run(configuration);
			}
		});
	}

}
