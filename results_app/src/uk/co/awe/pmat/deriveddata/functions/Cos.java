package uk.co.awe.pmat.deriveddata.functions;

import java.util.List;
import uk.co.awe.pmat.deriveddata.AbstractFunction;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;

/**
 * Derived data function which returns the trigonometric cosine of a number.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Cos extends AbstractFunction {

	@Override
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		super.bind(new Class<?>[][] { new Class<?>[] { Double.class,
				Integer.class }, }, args);
	}

	@Override
	protected String apply(DataGrid dataGrid, List<String> argCols) {
		if (argCols.size() != 1) {
			throw new IllegalStateException(
					"Incorrent number of columns pass to Abs:" + argCols.size());
		}
		final String name = "cos(" + argCols.get(0) + ")";
		for (DataGrid.Row row : dataGrid.getRows()) {
			final Value<Double> val = Value.doubleValue(row.getyValue(argCols
					.get(0)));
			row.addyValue(name, val.updateValue(Math.cos(val.getValue())));
		}
		return name;
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Double.class };
	}

}