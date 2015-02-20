package uk.co.awe.pmat.deriveddata;

/**
 * An interface for a function which can be used in the derived data parser. The
 * interface extends {@code DerivedData} so can be evaluated in the parser, but
 * also allows for the binding of arguments which can be used when it is
 * evaluated.
 * 
 * @author AWE Plc copyright 2013
 */
public interface Function extends DerivedData, Cloneable {

	/**
	 * Bind the arguments to the function. This allows the arguments to be
	 * stored or processed in some way to allow the function to be correctly
	 * evaluated when the {@code evaluate} method is called.
	 * 
	 * @param args
	 *            The arguments to bind to the function
	 * @throws InvalidArgumentsException
	 *             if the wrong number or type of arguments are provided
	 * @see DerivedData
	 */
	void bind(DerivedData... args) throws InvalidArgumentsException;

	/**
	 * Return a new instance of this {@code Function} class.
	 * 
	 * @return A new Function instance
	 */
	Function newInstance();

	/**
	 * Return the arguments that have been bound against this {@code Function}.
	 * 
	 * @return the bound arguments.
	 */
	DerivedData[] getArgs();

}
