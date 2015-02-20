package uk.co.awe.pmat.datafiles;

import java.io.IOException;

/**
 * An exception which is thrown if an invalid file header is encountered.
 * 
 * @author AWE Plc copyright 2013
 */
public final class InvalidHeaderException extends IOException {

	/**
	 * Create a new {@code InvalidHeaderException} with the given message.
	 * 
	 * @param msg
	 *            the message, which can be retrieved via {@link #getMessage()}.
	 */
	public InvalidHeaderException(String msg) {
		super(msg);
	}
}