package uk.co.awe.pmat;

/**
 * An exception which indicates a recoverable erroneous application state, i.e.
 * if insufficient state has been created by the user to perform the action the
 * user has requested.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ApplicationException extends Exception {

	/**
	 * Create a new {@code ApplicationException}.
	 * 
	 * @param message
	 *            the exception message.
	 */
	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

}
