package uk.co.awe.pmat.deriveddata;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.utils.ArrayUtils;

/**
 * A helper class which implements the derived data {@code Function} interface
 * and provides some shared functionality. This is used by the simple numeric
 * function class such as {@code Log} and {@code Abs}, as well as all the
 * operator class {@code +}, {@code -}, etc., which simply apply a function to
 * their arguments and return the result.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class AbstractFunction implements Function {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractFunction.class);

	private List<DerivedData> args;

	@Override
	public DerivedData[] getArgs() {
		return args.toArray(new DerivedData[args.size()]);
	}

	/**
	 * Checks the given arguments to make sure there are the correct number and
	 * they are of the correct type. For example a function may take 2 {@code
	 * Integer} arguments.
	 * 
	 * @param argTypes
	 *            an array of class arrays representing the acceptable. argument
	 *            types. The first dimension of the array is the number of
	 *            arguments expected.
	 * @param argArray
	 *            the arguments given to the function.
	 * @throws InvalidArgumentsException
	 *             if there is an incorrect number of arguments or they are not
	 *             of the expected types.
	 */
	protected void bind(Class<?>[][] argTypes, DerivedData[] argArray)
			throws InvalidArgumentsException {

		// Check the number of arguments
		if (argArray.length != argTypes.length) {
			throw new InvalidArgumentsException("Function "
					+ getClass().getSimpleName() + " expects "
					+ argTypes.length + " arguments, recieved "
					+ argArray.length);
		}
		// Check the types of the arguments
		for (int argIdx = 0; argIdx < argArray.length; ++argIdx) {
			Class<?>[] types = argArray[argIdx].returnTypes();
			Class<?>[] expectedTypes = argTypes[argIdx];
			if (!ArrayUtils.arrayContains(expectedTypes, types)) {
				throw new InvalidArgumentsException("Function "
						+ getClass().getSimpleName()
						+ " expects argument number " + (argIdx + 1)
						+ " to be of types "
						+ ArrayUtils.toString(expectedTypes)
						+ ", recieved argument of type "
						+ ArrayUtils.toString(types));
			}
		}
		args = java.util.Arrays.asList(argArray);
	}

	@Override
	public abstract void bind(DerivedData... argArray)
			throws InvalidArgumentsException;

	@Override
	public String evaluate(DataGrid dataGrid) throws DerivedDataException {
		if (args == null) {
			throw new IllegalStateException("Evaluate called on function "
					+ getClass().getSimpleName()
					+ " before any arguments have been bound");
		}

		List<String> argCols = new ArrayList<String>();
		for (DerivedData arg : args) {
			argCols.add(arg.evaluate(dataGrid));
		}

		return apply(dataGrid, argCols);
	}

	@Override
	public Function newInstance() {
		AbstractFunction newFunction = null;
		try {
			newFunction = (AbstractFunction) clone();
			newFunction.args = null;
		} catch (CloneNotSupportedException ex) {
			LOG.error("Error creating new instance of Function " + getClass(),
					ex);
		}
		return newFunction;
	}

	/**
	 * This method should handle the actual work of the function, using the
	 * arguments provided. At this point the arguments have already been parsed
	 * and are either simple numeric or string objects.
	 * 
	 * @param dataGrid
	 *            the data to apply the function to.
	 * @param argCols
	 *            the columns in the data grid corresponding to the function
	 *            arguments.
	 * @return the result of applying this function to the arguments.
	 */
	protected abstract String apply(DataGrid dataGrid, List<String> argCols);
}
