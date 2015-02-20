package uk.co.awe.pmat.gui;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseException;

/**
 * A dialog which is used to display exceptions to the user. These can either be
 * recoverable exceptions, or fatal ones where the user will have a chance to
 * view/send the log as part of a "bug" report.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ExceptionDialog extends JDialog {

	private static final Logger LOG = LoggerFactory
			.getLogger(ExceptionDialog.class);

	private static final String SHOW_LOG_TEXT = "Show Log";
	private static final String HIDE_LOG_TEXT = "Hide Log";
	private static final int MAX_WIDTH = 60;

	/**
	 * Action to perform when the "Show Log" button is pressed.
	 */
	private final Action showLogAction = new AbstractAction(SHOW_LOG_TEXT) {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (logDisplay.getText().isEmpty()) {
				loadLog();
			}
			showLog(true);
		}
	};

	/**
	 * Action to perform when the "Hide Log" button is pressed.
	 */
	private final Action hideLogAction = new AbstractAction(HIDE_LOG_TEXT) {
		@Override
		public void actionPerformed(ActionEvent e) {
			showLog(false);
		}
	};

	/**
	 * Action to perform when the "Exit" button is pressed.
	 */
	private final Action exitAction = new AbstractAction("Exit") {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	};

	/**
	 * Create an new {@code ExceptionDialog}.
	 * 
	 * @param parent
	 *            the window which owns this dialog, and will be used to locate
	 *            the dialog on the screen.
	 * @param modal
	 *            {@code true} if this should be a modal dialog, {@code false}
	 *            otherwise.
	 * @param throwable
	 *            the exception that we are displaying.
	 * @param message
	 *            the message to display.
	 * @param title
	 *            the title to display in the dialog banner.
	 * @param hasLogButton
	 *            {@code true} if a "Show Log" button should be displayed,
	 *            {@code false} otherwise.
	 */
	private ExceptionDialog(Window parent, boolean modal, Throwable throwable,
			String message, String title, boolean hasLogButton) {

		super(parent, modal ? Dialog.DEFAULT_MODALITY_TYPE
				: Dialog.ModalityType.MODELESS);
		initComponents();

		exitButton.setAction(exitAction);

		setTitle(title != null ? title : throwable.getClass().getSimpleName());

		String stackTrace = getStackTrace(throwable);

		LOG.debug(throwable.getClass().getSimpleName()
				+ " displayed in ExceptionDialog: " + throwable.getMessage());
		LOG.debug("Stack Trace:");
		LOG.debug(stackTrace);

		String msg = "<html>";
		msg += message != null ? wrapLine(message) + "<br/><br/>" : "";
		msg += exceptionMessage(throwable);
		msg += "</html>";
		this.message.setText(msg);

		if (parent != null) {
			setLocation(parent.getX() + parent.getWidth() / 2 - getWidth() / 2,
					parent.getY() + parent.getHeight() / 2 - getHeight() / 2);
		}

		if (!hasLogButton) {
			showLogButton.setVisible(false);
		}
		showLog(false);
	}

	/**
	 * Wrap a line, adding line breaks in the next white space which occurs
	 * after {@code MAX_WIDTH} number of characters.
	 * 
	 * @param line
	 *            the line to wrap.
	 * @return the wrapped line.
	 */
	private static String wrapLine(String line) {
		final StringBuilder wrappedLine = new StringBuilder();

		// Wrap the line at first space after MAX_WIDTH.
		String delim = "";
		while (line.length() > MAX_WIDTH) {
			final int spaceIdx = line.indexOf(' ', MAX_WIDTH);
			if (spaceIdx == -1) {
				break;
			}
			wrappedLine.append(delim).append(line.substring(0, spaceIdx));
			line = line.substring(spaceIdx).replaceFirst(" ", "");
			delim = "<br/>";
		}
		if (!line.isEmpty()) {
			wrappedLine.append(delim).append(line);
		}

		return wrappedLine.toString();
	}

	/**
	 * Extract and format the message stored in an exception.
	 * 
	 * @param exception
	 *            the exception for whose message we are extracting.
	 * @return the formatted exception message.
	 */
	private static String exceptionMessage(Throwable exception) {
		final String name = exception.getClass().getSimpleName();
		final String message = exception.getMessage();

		String line = name;
		if (message == null) {
			return line;
		} else {
			return wrapLine(line + ": " + message);
		}
	}

	/**
	 * Extract a stack trace from an exception.
	 * 
	 * @param throwable
	 *            the exception from which we are extracting the trace.
	 * @return the stack trace.
	 */
	private static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * Load the application log from the log file and display it in the log
	 * display text area.
	 */
	private void loadLog() {
		String logFileName = ResourceBundle.getBundle("log4j").getString(
				"log4j.appender.rollingFile.File");

		final StringBuilder logText = new StringBuilder();
		final File logFile = new File(logFileName);
		if (!logFile.exists()) {
			logText.append("No log file found.\n");
		} else {
			BufferedReader buffIn = null;
			try {
				buffIn = new BufferedReader(new FileReader(logFile));
				String line = buffIn.readLine();
				while (line != null) {
					logText.append(line).append("\n");
					line = buffIn.readLine();
				}
			} catch (IOException ex) {
				logText.append(ex.getMessage()).append("\n");
			} finally {
				try {
					if (buffIn != null) {
						buffIn.close();
					}
				} catch (IOException ex) {
					logText.append(ex.getMessage()).append("\n");
				}
			}
		}

		logDisplay.setText(logText.toString());
	}

	/**
	 * Display the application log in a text area on the dialog.
	 * 
	 * @param showFlag
	 *            {@code true} if the log should be shown, {@code false}
	 *            otherwise.
	 */
	private void showLog(boolean showFlag) {
		if (showFlag) {
			showLogButton.setText(HIDE_LOG_TEXT);
			showLogButton.setAction(hideLogAction);
			logScroll.setVisible(true);
		} else {
			showLogButton.setText(SHOW_LOG_TEXT);
			showLogButton.setAction(showLogAction);
			logScroll.setVisible(false);
		}
		pack();
	}

	/**
	 * Display a dialog box to inform the user of an exception occurring in the
	 * application.
	 * 
	 * @param throwable
	 *            The exception thrown
	 * @param message
	 *            A message to display with the exception
	 * @param title
	 *            The title of the dialog
	 */
	public static void showExceptionDialog(Throwable throwable, String message,
			String title) {

		ExceptionDialog dialog = new ExceptionDialog(MainFrame.getRootFrame(),
				true, throwable, message, title, false);
		dialog.setVisible(true);
	}

	/**
	 * Display a dialog box to inform the user of an exception occurring in the
	 * application.
	 * 
	 * @param throwable
	 *            The exception thrown
	 * @param message
	 *            A message to display with the exception
	 */
	public static void showExceptionDialog(Throwable throwable, String message) {
		showExceptionDialog(throwable, message, null);
	}

	/**
	 * Show a dialog box to inform the user of a {@link DatabaseException} which
	 * has been thrown. This is a version of
	 * {@link #showExceptionDialog(Throwable, String, String)} specialised for
	 * this exception as it is used a lot in the code.
	 * 
	 * @param exception
	 *            the {@link DatabaseException} thrown.
	 */
	public static void showDatabaseExceptionDialog(DatabaseException exception) {
		Throwable cause = exception;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		showExceptionDialog(cause,
				"An error occured while trying to query the database",
				"Database Error");
	}

	/**
	 * Show a dialog box to inform the user of an exception which has been
	 * thrown but not caught until it reaches the top level. These exceptions
	 * should be serious errors and so the application log can be viewed to see
	 * what has gone wrong.
	 * 
	 * @param parent
	 *            The {@code Swing} component that will be the parent of this
	 *            dialog
	 * @param throwable
	 *            The exception thrown
	 */
	public static void showUncaughtExceptionDialog(Window parent,
			Throwable throwable) {

		ExceptionDialog dialog = new ExceptionDialog(parent, true, throwable,
				"A serious error has occured. Please report this "
						+ "error along with the log below.", null, true);
		dialog.setVisible(true);
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
		java.awt.GridBagConstraints gridBagConstraints;

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		icon.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/error.png"))); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(icon, gridBagConstraints);

		message.setText("Message");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(message, gridBagConstraints);

		logScroll.setMinimumSize(new java.awt.Dimension(400, 400));
		logScroll.setPreferredSize(new java.awt.Dimension(400, 400));

		logDisplay.setColumns(20);
		logDisplay.setRows(5);
		logScroll.setViewportView(logDisplay);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		getContentPane().add(logScroll, gridBagConstraints);

		showLogButton.setText("Show Log");
		buttonPanel.add(showLogButton);

		exitButton.setText("Exit");
		buttonPanel.add(exitButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(buttonPanel, gridBagConstraints);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	private final javax.swing.JButton exitButton = new javax.swing.JButton();
	private final javax.swing.JLabel icon = new javax.swing.JLabel();
	private final javax.swing.JTextArea logDisplay = new javax.swing.JTextArea();
	private final javax.swing.JScrollPane logScroll = new javax.swing.JScrollPane();
	private final javax.swing.JLabel message = new javax.swing.JLabel();
	private final javax.swing.JButton showLogButton = new javax.swing.JButton();
	// End of variables declaration//GEN-END:variables

}
