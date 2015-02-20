package uk.co.awe.pmat.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.graph.Plottable;
import uk.co.awe.pmat.graph.PlottableLine;
import uk.co.awe.pmat.utils.DefaultHashMap.Creator;
import uk.co.awe.pmat.utils.DefaultHashMap;

/**
 * A class to obtain the data returned from an analysis method in a way that can
 * be plotted.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AnalysisData implements Plottable {

	private static final Logger LOG = LoggerFactory
			.getLogger(AnalysisData.class);

	private final AnalysisMethod analysisMethod;
	private final Axis xAxisName;
	private final Axis yAxisName;
	private final List<AnalysisLine> analysisLines = new ArrayList<AnalysisLine>();

	/**
	 * Create a new {@code AnalysisData}.
	 * 
	 * @param analysisMethod
	 *            the method which will be used to obtain the data.
	 * @param xAxisName
	 *            the name of the x-axis.
	 * @param yAxisName
	 *            the name of the y-axis.
	 * @param plottableLines
	 *            the lines used as the based of the analysis lines, i.e. the
	 *            name of the analysis line will contain the name of the
	 *            plottable line it was based on.
	 */
	public AnalysisData(AnalysisMethod analysisMethod, Axis xAxisName,
			Axis yAxisName, Collection<? extends PlottableLine> plottableLines) {

		this.analysisMethod = analysisMethod;
		this.xAxisName = xAxisName;
		this.yAxisName = yAxisName;
		createAnalysisLines(plottableLines);
	}

	/**
	 * Create the analysis lines from the list of plottable lines.
	 * 
	 * @param plottableLines
	 *            the plottable lines from which to derive the analysis lines.
	 */
	private void createAnalysisLines(
			Collection<? extends PlottableLine> plottableLines) {
		for (PlottableLine line : plottableLines) {
			analysisLines.add(new AnalysisLine(line.getName() + " "
					+ analysisMethod.analysisMethodName(), line.getLineType()));
		}
	}

	@Override
	public Object[][] getTableData() {

		final Collection<Integer> analysisIDs = analysisMethod
				.returnAnalysisIDs();

		final Map<Double, Double[]> values = new DefaultHashMap<Double, Double[]>(
				new Creator<Double[]>() {
					@Override
					public Double[] create() {
						return new Double[analysisIDs.size()];
					}
				});
		// = new HashMap<Double, Double[]>();
		int seriesIdx = 0;

		for (Integer id : analysisIDs) {
			List<DataPoint> analysis = analysisMethod.returnAnalysis(id);
			for (DataPoint point : analysis) {
				values.get(point.getXValue())[seriesIdx] = point.getYValue();
			}
			++seriesIdx;
		}

		List<Double> sortedXValues = new ArrayList<Double>(values.keySet());
		Collections.sort(sortedXValues);

		Object[][] tableData = new Object[values.size()][analysisIDs.size() + 1];

		for (int xIdx = 0; xIdx < sortedXValues.size(); ++xIdx) {

			final Double xValue = sortedXValues.get(xIdx);
			tableData[xIdx][0] = xValue;

			Double[] yValues = values.get(xValue);
			System.arraycopy(yValues, 0, tableData[xIdx], 1, yValues.length);

			LOG.debug("Adding row " + Arrays.toString(tableData[xIdx]));
		}

		return tableData;
	}

	@Override
	public Axis getXAxis() {
		return xAxisName;
	}

	@Override
	public Axis getYAxis() {
		return yAxisName;
	}

	@Override
	public Collection<? extends PlottableLine> getPlottableLines() {
		return Collections.unmodifiableCollection(analysisLines);
	}

}
