package uk.co.awe.pmat;

import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import uk.co.awe.pmat.utils.ArrayUtils;

/**
 * An public static final class used to hold all the application static
 * constants used.
 * 
 * @author AWE Plc copyright 2013
 */
@SuppressWarnings("ClassMayBeInterface")
public final class Constants {

	/**
	 * {@code Constants} cannot be instantiated.
	 */
	private Constants() {
	}

	/**
	 * Application level constants, i.e. how the application looks or which
	 * version it is.
	 */
	public static final class Application {
		/** The application version. */
		public static final String VERSION = "2.1.1";

		/** The title bar caption. */
		public static final String CAPTION = "PMAT " + VERSION;

		/** The URL of the online user guide. */
		public static final String USER_GUIDE_URL = "UserGuide_URL";

		/** The default application x size. */
		public static final int DEFAULT_SIZE_X = 800;

		/** The default application y size. */
		public static final int DEFAULT_SIZE_Y = 1000;

		/** The default application x position. */
		public static final int DEFAULT_POS_X = 200;

		/** The default application y position. */
		public static final int DEFAULT_POS_Y = 200;

		/** The application look and feel name. */
		public static final String LOOK_AND_FEEL = "Nimbus";

		/** The application short description. */
		public static final String SHORT_DESCRIPTION = "Performance Modelling Analysis Tool";

		/** The application description. */
		public static final String DESCRIPTION = " A tool for displaying and analysing performance modelling results.";

		/** The application authors. */
		public static final String AUTHORS = "<html>AWE Plc<br/>"
				+ "Tessella Plc<br/>"
				+ "University of Warwick<br/></html>";

		/** The title of the about box. */
		public static final String ABOUT_BOX_TITLE = "About";

		/** The analysis dialog title. */
		public static final String ANALYSIS_DIALOG_TITLE = "Analyses Stored in Database";

		public static final String RESULT_STORE_DIRECTORY = "PMAT/pmtm_store";
	}

	/**
	 * Configuration constants. These are used in the {@code Configuration}
	 * class.
	 */
	public static final class Config {
		/** The extension used on PMAT configuration file. */
		public static final String EXTENSTION = ".cfg.xml";

		/** The default configuration filename. */
		public static final String DEFAULT_FILE = "default";

		/** The directory for the user configuration, in user home directory. */
		public static final String DIRECTORY = ".PMAT";

		/** The filename for the user configuration file. */
		public static final String NORMAL_FILE = "user";

		/** The filename for the development mode configuration file. */
		public static final String DEVELOPMENT_FILE = "dev";

		/** The filename for the test mode configuration file. */
		public static final String TEST_FILE = "test";
	}

	/**
	 * Constants used in the export of PMAT results.
	 */
	public static final class Export {
		/** The PMAT results export file extension. */
		public static final String EXTENSION = ".prf";

		/** The results export file file-chooser description. */
		public static final String DESCRIPTION = "PMAT Results File";
	}

	/**
	 * Constants used in the analysis and export/import of analyses.
	 */
	public static final class Analysis {
		/** The analysis export file file-chooser description. */
		public static final String DESCRIPTION = "PMAT Analysis Export";

		/** The directory to load the the analysis methods from. */
		public static final String METHOD_FOLDER = "../analyses/dist";

		/** The default filename for analysis export files. */
		public static final String DEFAULT_FILENAME = "PMAT_analysis";

		/** The analysis export file extension. */
		public static final String EXTENSION = ".paf";
	}

	/**
	 * Constants used in the GUI.
	 */
	public static final class Gui {
		/** The status bar cancel text. */
		public static final String STATUS_BAR_CANCEL_TEXT = null;

		/** The status bar cancel icon. */
		public static final Icon STATUS_BAR_CANCEL_ICON = new ImageIcon(
				ClassLoader.getSystemResource("cross16.png"));
	}

	/**
	 * Constants used in the application menu.
	 */
	public static final class Menu {
		/** The file menu label. */
		public static final String FILE_MENU = "&File";

		/** The database menu label. */
		public static final String DATABASE_MENU = "&Database";

		/** The analysis menu label. */
		public static final String ANALYSIS_MENU = "&Analysis";

		/** The show help menu label. */
		public static final String HELP_MENU = "&Help";

		/** The close menu item label. */
		public static final String CLOSE_ITEM = "Close";

		/** The show about box menu item label. */
		public static final String ABOUT_ITEM = "&About";

		/** The show user guide menu item label. */
		public static final String USER_GUIDE = "&User Guide";

		/** The exit menu item label. */
		public static final String EXIT_ITEM = "&Exit";

		/** The settings menu item label. */
		public static final String SETTINGS_ITEM = "&Settings";

		/** The edit configuration menu item label. */
		public static final String EDIT_CONFIG_ITEM = "&Edit Configuration Data";

		/** The view results menu item label. */
		public static final String VIEW_EXPORT_ITEM = "&View/Export Results";

		/** The add results menu item label. */
		public static final String ADD_RESULTS_ITEM = "&Add Results";

		/** The import results menu item label. */
		public static final String IMPORT_RESULTS_ITEM = "&Import Results";

		/** The connection settings menu item label. */
		public static final String CONN_SETTINGS_ITEM = "&Connection Settings";

		/** The reset analyses menu item label. */
		public static final String RESET_ANALYSIS_ITEM = "&Reset Analysis";

		/** The import analyses menu item label. */
		public static final String IMPORT_ANALYSES_ITEM = "&Import Analyses";

		/** The view analyses menu item label. */
		public static final String VIEW_ANALYSES_ITEM = "&View/Export Analyses";
	}

	/**
	 * Constants used in the plotting tools.
	 */
	public static final class Plot {
		/** The image resize increments. */
		public static final int IMAGE_SIZE_INCREMENTS = 20;

		/** The minimum image size. */
		public static final int IMAGE_MIN_SIZE = 50;

		/** The time to wait before cancelling the plotter. */
		public static final long TIMEOUT = 5000; // ms

		/** The timeout sample interval. */
		public static final long TIMEOUT_STEP = 100; // ms

		/** File extension for PNG files. */
		public static final String PNG_EXT = ".png";

		/** Default filename for plot files. */
		public static final String DEFAULT_FILENAME = "PMAT_plot_";

		/** File extension for plot data files. */
		public static final String DATA_EXT = ".dat";

		/** File extension for plot TeX files. */
		public static final String TEX_EXT = ".tex";

		/**
		 * Constants used in the export of plotting data.
		 */
		public static final class Data {
			/** Default filename for data files. */
			public static final String DEFAULT_FILENAME = "PMAT_data";

			/** File extension for CSV files. */
			public static final String CSV_EXT = ".csv";
		}

		/**
		 * Constants used in the styling of plot lines.
		 */
		public static final class Line {
			/** Default line style. */
			public static final LineStyle DEFAULT_STYLE = LineStyle.POINTS;

			/** Default line colour. */
			public static final GraphColour DEFAULT_COLOUR = GraphColour.GREEN;

			/** Default line width. */
			public static final int DEFAULT_WIDTH = 1;
		}

		/**
		 * Constants used to drive the {@code gnuplot} plotter.
		 */
		public static final class Gnuplot {
			/** GnuPlot command line command. */
			public static final String COMMAND = "gnuplot";

			/** GnuPlot command file extension. */
			public static final String COMMAND_EXT = ".gnu";

			/** TeX file image size. */
			public static final String TEX_GRAPH_SIZE = "15cm, 10cm";

			/** GnuPlot test command. */
			public static final List<String> TEST_COMMAND = ArrayUtils
					.asUnmodifiableList("gnuplot", "-V");
		}

		/**
		 * Constants used to drive the {@code matplotlib} plotter.
		 */
		public static final class Matplotlib {
			/** MatplotLib command line command. */
			public static final String COMMAND = "python";

			/** MatplotLib command file extension. */
			public static final String COMMAND_EXT = ".plt";

			/** MatplotLib test command. */
			public static final List<String> TEST_COMMAND = ArrayUtils
					.asUnmodifiableList("python", "-c", "import matplotlib");
		}
	}
}
