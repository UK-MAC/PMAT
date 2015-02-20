package uk.co.awe.pmat.deriveddata.functions;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.deriveddata.Function;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Constant;
import uk.co.awe.pmat.deriveddata.ParserValues.Property;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;

/**
 * Derived data function which returns the first value of a {@code Variable}
 * which is found where a given {@code Property} equals a given {@code Constant}
 * value. The {@code Variable} is given by the first argument, the {@code
 * Property} by the second and the {@code Constant} by the third.
 * 
 * @author AWE Plc copyright 2013
 * @see uk.co.awe.pmat.analysis.deriveddata.ParserValues.Variable
 * @see uk.co.awe.pmat.analysis.deriveddata.ParserValues.Property
 * @see uk.co.awe.pmat.analysis.deriveddata.ParserValues.Constant
 */
public final class Find implements Function {

	private static final Logger LOG = LoggerFactory.getLogger(Find.class);

	private DerivedData select;
	private DerivedData where;
	private Constant<?> value;

	@Override
	public DerivedData[] getArgs() {
		return new DerivedData[] { select, where, value };
	}

	@Override
	@SuppressWarnings("unchecked")
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		if (args.length != 3) {
			throw new InvalidArgumentsException("Find expects 3 arguments,"
					+ " received " + args.length);
		}
		if (args[0] instanceof Variable || (args[0] instanceof Function)) {
			select = args[0];
		} else {
			throw new InvalidArgumentsException("The first argument in Find "
					+ "must be a Variable or Function");
		}
		if ((args[1] instanceof Variable) || (args[1] instanceof Property)) {
			where = args[1];
		} else {
			throw new InvalidArgumentsException("The second argument in Find "
					+ "must be a Variable or Property");
		}
		if (args[2] instanceof Constant) {
			value = (Constant) args[2];
		} else {
			throw new InvalidArgumentsException("The third argument in Find "
					+ "must be a Constant");
		}
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Double.class };
	}

	@Override
	public Function newInstance() {
		Find newFunction = null;
		try {
			newFunction = (Find) clone();
			newFunction.where = null;
			newFunction.select = null;
			newFunction.value = null;
		} catch (CloneNotSupportedException ex) {
			LOG.error("Error creating new instance of Function " + getClass(),
					ex);
		}
		return newFunction;
	}

	@Override
    public String evaluate(DataGrid dataGrid) throws DerivedDataException {
        final String var = select.evaluate(dataGrid);
        final String prop = where.evaluate(dataGrid);
        final String val = value.evaluate(dataGrid);
        final String name = String.format("find(%s, %s, %s)", var, prop, val);
        final Map<Value<?>, Value<?>> xVals = new HashMap<>();

        for (DataGrid.Row row : dataGrid.getRows()) {
            final Value<?> varVal = row.getyValue(var);
            final Value<?> propVal = row.getyValue(prop);
            final Value<?> xVal = row.getxValue();

            // Only add the first value found for each x value.
            if (xVals.containsKey(xVal)) { continue; }

            if (propVal != null && propVal.getValue().equals(val)) {
                xVals.put(xVal, varVal);
            }
        }

        for (DataGrid.Row row : dataGrid.getRows()) {
            row.addyValue(name, xVals.get(row.getxValue()));
        }

        return name;
    }
}