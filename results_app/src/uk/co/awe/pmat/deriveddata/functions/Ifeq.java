package uk.co.awe.pmat.deriveddata.functions;

import java.util.List;
import uk.co.awe.pmat.deriveddata.AbstractFunction;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;

/**
 * Derived data function which compares two values and returns 1 if they are
 * equal and 0 otherwise.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Ifeq extends AbstractFunction {

	@Override
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		super.bind(
				new Class[][] {
						new Class<?>[] { String.class, Double.class,
								Integer.class },
						new Class<?>[] { String.class, Double.class,
								Integer.class }, }, args);
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Integer.class };
	}

	@Override
	protected String apply(DataGrid dataGrid, List<String> argCols) {
		if (argCols.size() != 2) {
			throw new IllegalStateException(
					"Incorrent number of columns pass to Ifeq:"
							+ argCols.size());
		}
		final String name = "ifeq(" + argCols.get(0) + ", " + argCols.get(1)
				+ ")";
		for (DataGrid.Row row : dataGrid.getRows()) {
			final Value<?> lhs = row.getyValue(argCols.get(0));
			final Value<?> rhs = row.getyValue(argCols.get(1));
			final double res;
			if (lhs.getValue() instanceof Number
					&& rhs.getValue() instanceof Number) {
				final Double lhsDbl = ((Number) lhs.getValue()).doubleValue();
				final Double rhsDbl = ((Number) rhs.getValue()).doubleValue();
				res = lhsDbl.equals(rhsDbl) ? 1. : 0.;
			} else {
				res = lhs.getValue().equals(rhs.getValue()) ? 1. : 0.;
			}
			row.addyValue(name, lhs.updateValue(res, 0.0));
		}
		return name;
	}

}
