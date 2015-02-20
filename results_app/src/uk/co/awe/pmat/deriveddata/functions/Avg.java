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
 * Derived data function that returns the average of all y values for the series
 * group.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Avg implements Function {

	private static final Logger LOG = LoggerFactory.getLogger(Avg.class);

	private DerivedData boundValue;

	@Override
	public DerivedData[] getArgs() {
		return new DerivedData[] { boundValue };
	}

	@Override
	public void bind(DerivedData... args) throws InvalidArgumentsException {
		if (args.length != 1) {
			throw new InvalidArgumentsException(
					"Function Avg expects 1 argument," + " received "
							+ args.length);
		}

		Class<?>[] types = args[0].returnTypes();
		Class<?>[] expectedTypes = new Class<?>[] { Double.class, Integer.class };

		if (!ArrayUtils.arrayContains(expectedTypes, types)) {
			throw new InvalidArgumentsException("Function Avg expects argument"
					+ " number 1 to be of types "
					+ Arrays.toString(expectedTypes)
					+ ", recieved argument of type " + Arrays.toString(types));
		}
		boundValue = args[0];
	}

	@Override
	public Class<?>[] returnTypes() {
		return new Class<?>[] { Double.class };
	}

	@Override
	public Function newInstance() {
		Avg newFunction = null;
		try {
			newFunction = (Avg) clone();
			newFunction.boundValue = null;
		} catch (CloneNotSupportedException ex) {
			LOG.error("Error creating new instance of Function " + getClass(),
					ex);
		}
		return newFunction;
	}

	/**
	 * Helper class use to hold the sums.
	 */
	private static final class Sum {
		private double valSum = 0.;
		private double errSum = 0.;
		private int count = 0;
	}

	@Override
	public String evaluate(DataGrid dataGrid) throws DerivedDataException {
		final String col = boundValue.evaluate(dataGrid);
		final String name = "avg(" + col + ")";
		final Map<SeriesGroup, Map<Value<?>, Sum>> seriesSums = DefaultHashMap
				.mapOfDefaultMaps(new Creator<Sum>() {
					@Override
					public Sum create() {
						return new Sum();
					}
				});

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Map<Value<?>, Sum> sums = seriesSums
					.get(row.getSeriesGroup());
			final Sum sum = sums.get(row.getxValue());

			final Object value = row.getyValue(col).getValue();
			final Object error = row.getyValue(col).getError();
			if (value instanceof Number) {
				sum.valSum += ((Number) value).doubleValue();
				if (error != null) {
					sum.errSum += ((Number) error).doubleValue();
				}
				++sum.count;
			}
		}

		for (DataGrid.Row row : dataGrid.getRows()) {
			final Map<Value<?>, Sum> sums = seriesSums
					.get(row.getSeriesGroup());
			final Sum sum = sums.get(row.getxValue());
			final Double avg = sum.valSum / sum.count;
			final Double errAvg = sum.errSum / sum.count;
			row.addyValue(name, row.getyValue(col).updateValue(avg, errAvg));
		}

		return name;
	}

}
