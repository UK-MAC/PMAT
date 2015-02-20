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
 * A derived data function that returns the maximum y value for a series group.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Max implements Function {

	private static final Logger LOG = LoggerFactory.getLogger(Max.class);

	private DerivedData boundValue;

	@Override
	public DerivedData[] getArgs() {
		return new DerivedData[] { boundValue };
	}

	@Override
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		if (args.length != 1) {
			throw new InvalidArgumentsException(
					"Function Max expects 1 argument," + " received "
							+ args.length);
		}

		Class<?>[] types = args[0].returnTypes();
		Class<?>[] expectedTypes = new Class<?>[] { Double.class, Integer.class };

		if (!ArrayUtils.arrayContains(expectedTypes, types)) {
			throw new InvalidArgumentsException("Function Max expects argument"
					+ " number 1 to be of types "
					+ Arrays.toString(expectedTypes)
					+ ", recieved argument of type " + Arrays.toString(types));
		}
		boundValue = args[0];
	}

	/**
	 * Helper class use to hold the max values found.
	 */
	private static final class MaxVal {
		private double val = Double.MIN_VALUE;
		private double err = 0.;
	}

	@Override
	public String evaluate(DataGrid dataGrid) throws DerivedDataException {
		final String col = boundValue.evaluate(dataGrid);
		final String name = "max(" + col + ")";
		final Map<SeriesGroup, Map<Value<?>, MaxVal>> seriesMaxes = DefaultHashMap
				.mapOfDefaultMaps(new Creator<MaxVal>() {
					@Override
					public MaxVal create() {
						return new MaxVal();
					}
				});
		// = new HashMap<SeriesGroup, Map<Value<?>, MaxVal>>();

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Map<Value<?>, MaxVal> maxes = seriesMaxes.get(row
					.getSeriesGroup());
			final MaxVal max = maxes.get(row.getxValue());

			final Object value = row.getyValue(col).getValue();
			final Object error = row.getyValue(col).getError();
			if (value instanceof Number) {
				final Double val = ((Number) value).doubleValue();
				if (val > max.val) {
					max.val = val;
					if (error != null) {
						max.err = ((Number) error).doubleValue();
					}
				}
			}
		}

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Map<Value<?>, MaxVal> maxes = seriesMaxes.get(row
					.getSeriesGroup());
			final MaxVal max = maxes.get(row.getxValue());
			final Value<?> val = row.getyValue(col);
			row.addyValue(name, val.updateValue(max.val, max.err));
		}

		return name;
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Double.class };
	}

	@Override
	public Function newInstance() {
		Max newFunction = null;
		try {
			newFunction = (Max) clone();
			newFunction.boundValue = null;
		} catch (CloneNotSupportedException ex) {
			LOG.error("Error creating new instance of Function " + getClass(),
					ex);
		}
		return newFunction;
	}

}
