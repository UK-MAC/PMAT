package uk.co.awe.pmat.deriveddata;

import uk.co.awe.pmat.db.DataGrid;

/**
 * An interface used in the derived data parser to hold an object which can be
 * evaluated at time of use. This could be a constant number/string, a variable
 * to extract from the data or a function to apply to the data.
 * 
 * @author AWE Plc copyright 2013
 */
public interface DerivedData {

	/**
	 * Evaluate the {@code DerivedData} object on a data point. The data point
	 * may or may not be used in the evaluation. I.e. a constant number will
	 * return the same value for any given arguments.
	 * 
	 * @param dataGrid
	 *            the data from which this derived data field can be derived
	 *            from.
	 * @return an object that contains the result of the evaluation. This will
	 *         be either an {@code Integer}, {@code Double} or {@code String}.
	 * @throws DerivedDataException
	 *             if an error occurs trying to evaluate the {@code DerivedData}
	 *             .
	 */
	String evaluate(DataGrid dataGrid) throws DerivedDataException;

	/**
	 * Return the types that can be associated with the {@code DerivedData}.
	 * I.e. if this is a function that could only ever return {@code Integer}
	 * values then this should return an array containing just the class {@code
	 * Class<Integer>}. This is used to perform a limited degree of type
	 * checking at parse time, i.e. a function that only takes {@code Integer}
	 * arguments fail to parse if given a {@code DerivedData} that can only ever
	 * return {@code String} objects.
	 * 
	 * @return an array containing the class types.
	 */
	Class<?>[] returnTypes();
}
