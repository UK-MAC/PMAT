package uk.co.awe.pmat.deriveddata;

/**
 * An exception to be thrown if a derived data function is passed an incorrect
 * number or types of arguments.
 * 
 * @author AWE Plc copyright 2013
 */
public class InvalidArgumentsException extends Exception {
	private static final long serialVersionUID = 5221207998170427651L;

	/**
	 * Create a new {@code InvalideArgumentsException} with the given message.
	 * 
	 * @param msg
	 *            the exception message, retrieved by {@link #getMessage()}.
	 */
	public InvalidArgumentsException(String msg) {
		super(msg);
	}
}
