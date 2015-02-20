package uk.co.awe.pmat.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import uk.co.awe.pmat.Constants;

/**
 * The menu bar for the application.
 * 
 * @author AWE Plc copyright 2013
 */
final class MenuBar extends JMenuBar {

	private final MainFrame mainFrame;
	private final JMenu fileMenu;
	private final JMenu databaseMenu;
	private final JMenu analysisMenu;
	private final JMenu helpMenu;

	private final Action exitAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.exit();
		}
	};

	private final Action aboutAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.showAboutBox();
		}
	};

	private final Action userGuideAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.showUserGuide();
		}
	};

	private final Action settingsAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.showSettings();
		}
	};

	private final Action viewAnalysesAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.showAnalyses();
		}
	};

	private final Action resetAnalysisAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.resetAnalysis();
		}
	};

	private final Action connectionSettingsAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.showConnectionSettings();
		}
	};

	/**
	 * A helper class which handles the event triggered by selecting a view.
	 */
	private class SetViewAction extends AbstractAction {
		private final View view;

		/**
		 * Create a new {@code SetViewAction}.
		 * 
		 * @param view
		 *            the view that this action will set as the current view.
		 */
		SetViewAction(View view) {
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.setView(view);
		}
	}

	/**
	 * Create a new {@code Menu}.
	 * 
	 * @param mainFrame
	 *            the parent frame of this menu.
	 */
	MenuBar(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		fileMenu = buildMenu(Constants.Menu.FILE_MENU);
		databaseMenu = buildMenu(Constants.Menu.DATABASE_MENU);
		analysisMenu = buildMenu(Constants.Menu.ANALYSIS_MENU);
		helpMenu = buildMenu(Constants.Menu.HELP_MENU);

		fileMenu
				.add(buildMenuItem(Constants.Menu.SETTINGS_ITEM, settingsAction));
		fileMenu.add(buildMenuItem(Constants.Menu.EXIT_ITEM, exitAction));

		databaseMenu.add(buildMenuItem(Constants.Menu.EDIT_CONFIG_ITEM,
				new SetViewAction(View.SYSTEM_CONFIG)));
		databaseMenu.add(buildMenuItem(Constants.Menu.VIEW_EXPORT_ITEM,
				new SetViewAction(View.EXPORT_RESULTS)));
		databaseMenu.add(buildMenuItem(Constants.Menu.ADD_RESULTS_ITEM,
				new SetViewAction(View.ADD_RESULTS)));
		databaseMenu.add(buildMenuItem(Constants.Menu.IMPORT_RESULTS_ITEM,
				new SetViewAction(View.IMPORT_RESULTS)));
		databaseMenu.add(buildMenuItem(Constants.Menu.CONN_SETTINGS_ITEM,
				connectionSettingsAction));

		analysisMenu.add(buildMenuItem(Constants.Menu.RESET_ANALYSIS_ITEM,
				resetAnalysisAction));
		analysisMenu.add(buildMenuItem(Constants.Menu.IMPORT_ANALYSES_ITEM,
				new SetViewAction(View.IMPORT_ANALYSES)));
		analysisMenu.add(buildMenuItem(Constants.Menu.VIEW_ANALYSES_ITEM,
				viewAnalysesAction));

		helpMenu.add(buildMenuItem(Constants.Menu.ABOUT_ITEM, aboutAction));
		helpMenu.add(buildMenuItem(Constants.Menu.USER_GUIDE, userGuideAction));

		add(fileMenu);
		add(databaseMenu);
		add(analysisMenu);
		add(helpMenu);
	}

	/**
	 * Create a menu item with the given name and action.
	 * 
	 * @param name
	 *            the menu item name.
	 * @param action
	 *            the menu item action.
	 * @return the menu item.
	 */
	private JMenuItem buildMenuItem(final String name, Action action) {
		final char mnemonicChar;
		if (name.contains("&")) {
			mnemonicChar = name.charAt(name.indexOf("&") + 1);
		} else {
			mnemonicChar = (char) -1;
		}
		final JMenuItem menuItem;
		if (action != null) {
			action.putValue(Action.NAME, name.replace("&", ""));
			menuItem = new JMenuItem(action);
		} else {
			menuItem = new JMenuItem(name.replace("&", ""));
		}
		if (mnemonicChar != -1) {
			menuItem.setMnemonic(mnemonicChar);
		}
		return menuItem;
	}

	/**
	 * Create a menu with the given name.
	 * 
	 * @param name
	 *            the menu name.
	 * @return the menu.
	 */
	private JMenu buildMenu(final String name) {
		final char mnemonicChar;
		if (name.contains("&")) {
			mnemonicChar = name.charAt(name.indexOf("&") + 1);
		} else {
			mnemonicChar = (char) -1;
		}
		final JMenu menu = new JMenu(name.replace("&", ""));
		if (mnemonicChar != -1) {
			menu.setMnemonic(mnemonicChar);
		}
		return menu;
	}

}
