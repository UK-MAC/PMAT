package uk.co.awe.pmat.graph;

/**
 * An exception which is thrown if a error occurs during the generation of the
 * graph.
 * 
 * @author AWE Plc copyright 2013
 */
public final class GraphDataException extends Exception {

	/**
	 * Create a new {@code GraphDataException}.
	 * 
	 * @param msg
	 *            the exception message, obtainable via {@link #getMessage()}.
	 */
	public GraphDataException(String msg) {
		super(msg);
	}

	/**
	 * Create a new {@code GraphDataException}.
	 * 
	 * @param msg
	 *            the exception message, obtainable via {@link #getMessage()}.
	 * @param cause
	 *            the {@code Throwable} that caused this exception.
	 */
	public GraphDataException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
