package uk.co.awe.pmat.db.xml;

/**
 * An Exception that can be thrown during creation of objects during processing
 * of XML.
 * 
 * @author AWE Plc copyright 2013
 */
public final class XMLSerialisationException extends RuntimeException {

	/**
	 * Create a new {@code XMLSerialisationException}.
	 * 
	 * @param cls
	 *            the class of the object that was being deserialised when this
	 *            exception was thrown.
	 * @param msg
	 *            the exception message.
	 */
	public XMLSerialisationException(Class<?> cls, String msg) {
		super("ImportException thrown at " + cls.getSimpleName() + ": " + msg);
	}

	/**
	 * Create a new {@code XMLSerialisationException}.
	 * 
	 * @param cls
	 *            the class of the object that was being deserialised when this
	 *            exception was thrown.
	 * @param msg
	 *            the exception message.
	 * @param ex
	 *            the exception that caused this {@code
	 *            XMLSerialisationException} to be thrown.
	 */
	public XMLSerialisationException(Class<?> cls, String msg, Throwable ex) {
		super("ImportException thrown at " + cls.getSimpleName() + ": " + msg,
				ex);
	}

	/**
	 * Create a new {@code XMLSerialisationException}.
	 * 
	 * @param msg
	 *            the exception message.
	 */
	public XMLSerialisationException(String msg) {
		super(msg);
	}
}
