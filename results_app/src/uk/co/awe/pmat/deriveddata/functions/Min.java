package uk.co.awe.pmat.deriveddata.functions;

import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.deriveddata.Function;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.series.SeriesGroup;
import uk.co.awe.pmat.utils.ArrayUtils;
import uk.co.awe.pmat.utils.DefaultHashMap;
import uk.co.awe.pmat.utils.DefaultHashMap.Creator;

/**
 * A derived data function that returns the minimum y value for a series group.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Min implements Function {

	private static final Logger LOG = LoggerFactory.getLogger(Min.class);

	private DerivedData boundValue;

	@Override
	public DerivedData[] getArgs() {
		return new DerivedData[] { boundValue };
	}

	@Override
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		if (args.length != 1) {
			throw new InvalidArgumentsException(
					"Function Min expects 1 argument," + " received "
							+ args.length);
		}

		Class<?>[] types = args[0].returnTypes();
		Class<?>[] expectedTypes = new Class<?>[] { Double.class, Integer.class };

		if (!ArrayUtils.arrayContains(expectedTypes, types)) {
			throw new InvalidArgumentsException("Function Min expects argument"
					+ " number 1 to be of types "
					+ Arrays.toString(expectedTypes)
					+ ", recieved argument of type " + Arrays.toString(types));
		}
		boundValue = args[0];
	}

	/**
	 * Helper class use to hold the min values found.
	 */
	private static final class MinVal {
		private double val = Double.MAX_VALUE;
		private double err = 0.;
	}

	@Override
	public String evaluate(DataGrid dataGrid) throws DerivedDataException {
		final String col = boundValue.evaluate(dataGrid);
		final String name = "min(" + col + ")";
		final Map<SeriesGroup, Map<Value<?>, MinVal>> seriesMaxes = DefaultHashMap
				.mapOfDefaultMaps(new Creator<MinVal>() {
					@Override
					public MinVal create() {
						return new MinVal();
					}
				});

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Map<Value<?>, MinVal> mins = seriesMaxes.get(row
					.getSeriesGroup());
			final MinVal min = mins.get(row.getxValue());

			final Object value = row.getyValue(col).getValue();
			final Object error = row.getyValue(col).getError();
			if (value instanceof Number) {
				final Double val = ((Number) value).doubleValue();
				if (val < min.val) {
					min.val = val;
					if (error != null) {
						min.err = ((Number) error).doubleValue();
					}
				}
			}
		}

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Map<Value<?>, MinVal> mins = seriesMaxes.get(row
					.getSeriesGroup());
			final MinVal min = mins.get(row.getxValue());
			final Value<?> val = row.getyValue(col);
			row.addyValue(name, val.updateValue(min.val, min.err));
		}

		return name;
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Double.class };
	}

	@Override
	public Function newInstance() {
		Min newFunction = null;
		try {
			newFunction = (Min) clone();
			newFunction.boundValue = null;
		} catch (CloneNotSupportedException ex) {
			LOG.error("Error creating new instance of Function " + getClass(),
					ex);
		}
		return newFunction;
	}

}
