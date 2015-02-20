package uk.co.awe.pmat.db;

/**
 * Exception meaning that a user has been asked for their name and password and
 * they have chosen to cancel the request.
 * 
 * @author AWE Plc copyright 2013
 */
public final class DatabaseException extends Exception {

	/**
	 * Create a new {@code DatabaseException}.
	 * 
	 * @param message
	 *            the exception message, obtainable via {@link #getMessage()}.
	 */
	public DatabaseException(String message) {
		super(message);
	}

	/**
	 * Create a new {@code DatabaseException}.
	 * 
	 * @param message
	 *            the exception message, obtainable via {@link #getMessage()}.
	 * @param cause
	 *            the nested cause of this {@code DatabaseException}.
	 */
	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new {@code DatabaseException}.
	 * 
	 * @param cause
	 *            the nested cause of this {@code DatabaseException}.
	 */
	public DatabaseException(Throwable cause) {
		super(cause);
	}

}
