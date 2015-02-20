package uk.co.awe.pmat.deriveddata.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.deriveddata.Function;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;

/**
 * Derived data function that returns the first value if it is not null,
 * otherwise it returns the second value.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Or implements Function {

	private static final Logger LOG = LoggerFactory.getLogger(Or.class);

	private Variable first;
	private Variable second;

	@Override
	public DerivedData[] getArgs() {
		return new DerivedData[] { first, second };
	}

	@Override
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		if (args.length != 2) {
			throw new InvalidArgumentsException(
					"Function Or expects 2 arguments," + " received "
							+ args.length);
		}

		if (!(args[0] instanceof Variable)) {
			throw new InvalidArgumentsException("The first argument in Or "
					+ "must be a Variable");
		}
		if (!(args[1] instanceof Variable)) {
			throw new InvalidArgumentsException("The second argument in Or "
					+ "must be a Variable");
		}

		first = (Variable) args[0];
		second = (Variable) args[1];
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Double.class };
	}

	@Override
	public Function newInstance() {
		Or newFunction = null;
		try {
			newFunction = (Or) clone();
			newFunction.first = null;
			newFunction.second = null;
		} catch (CloneNotSupportedException ex) {
			LOG.error("Error creating new instance of Function " + getClass(),
					ex);
		}
		return newFunction;
	}

	@Override
	public String evaluate(DataGrid dataGrid) throws DerivedDataException {
		final String firstName = first.evaluate(dataGrid);
		final String secondName = second.evaluate(dataGrid);
		final String name = String.format("or(%s, %s)", firstName, secondName);

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Value<?> firstVal = row.getyValue(firstName);
			final Value<?> secondVal = row.getyValue(secondName);

			final Value<?> retVal = (firstVal == null || firstVal.getValue() == null) ? secondVal
					: firstVal;
			row.addyValue(name, retVal);
		}

		return name;
	}

}
