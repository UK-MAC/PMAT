package uk.co.awe.pmat.graph.gnuplot;

import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * A class which encapsulates the {@code GnuPlot} commands necessary to define
 * the required axis type.
 * 
 * @author AWE Plc copyright 2013
 */
final class GnuPlotAxis {

	private final AxisName name;
	private int logScale;
	private String label;
	private Double minValue;
	private Double maxValue;

	/**
	 * Create a GnuPlot axis with the given name.
	 * 
	 * @param name
	 *            The {@code GnuPlot} axis name that this {@code GnuPlotAxis}
	 *            represents.
	 */
	GnuPlotAxis(AxisName name) {
		this.name = name;
		logScale = 0;
		label = null;
		minValue = null;
		maxValue = null;
	}

	/**
	 * Set the label that will be plotted against this axis.
	 * 
	 * @param label
	 *            The label to plot
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Specify the log scale value for this axis.
	 * 
	 * @return 0 if this axis is not log scale, or the log scale value which is
	 *         set.
	 */
	public Integer getLogScale() {
		return logScale;
	}

	/**
	 * Set the log scale for this axis.
	 * 
	 * @param logScale
	 *            The log scale to set. Must be either 0 (for no log scale) or
	 *            at least 2.
	 */
	public void setLogScale(int logScale) {
		if (logScale == 1) {
			throw new IllegalArgumentException("Invalid argument to "
					+ "setLogScale, cannot set log scale to 1");
		}
		this.logScale = logScale;
	}

	/**
	 * Get the current minimum value set for this axis. If no minimum value has
	 * been set then {@code null} is returned.
	 * 
	 * @return The minimum value set for this axis if one exists
	 */
	public Double getMinValue() {
		return minValue;
	}

	/**
	 * Get the current maximum value set for this axis. If no maximum value has
	 * been set then {@code null} is returned.
	 * 
	 * @return The maximum value set for this axis if one exists
	 */
	public Double getMaxValue() {
		return maxValue;
	}

	/**
	 * Set the minimum value for this axis. This specifies the lower bound of
	 * the range that will be used by {@code GnuPlot}.
	 * 
	 * @param minValue
	 *            The minimum value
	 */
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	/**
	 * Set the maximum value for this axis. This specifies the upper bound of
	 * the range that will be used by {@code GnuPlot}.
	 * 
	 * @param maxValue
	 *            The maximum value
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Generate the {@code GnuPlot} command to set the log scale.
	 * 
	 * @return The log scale command string
	 */
	public String logscaleCommandString() {
		if (logScale > 0) {
			return "set logscale " + name + " " + logScale + "\n";
		} else {
			return "unset logscale " + name + "\n";
		}
	}

	/**
	 * Generate the {@code GnuPlot} command to set the axis label.
	 * 
	 * @return The label command string
	 */
	public String labelCommandString() {
		if (label != null) {
			return "set " + name + "label \""
					+ StringUtils.capitaliseWords(label) + "\"\n";
		} else {
			return "";
		}
	}

	/**
	 * Generate the {@code GnuPlot} command to set the plot range.
	 * 
	 * @return The range command string
	 */
	public String rangeCommandString() {
		if (minValue == null && maxValue == null) {
			return "set autoscale " + name + "fixmax\n";
		} else {
			return "set " + name + "range ["
					+ (minValue != null ? minValue : "") + ":"
					+ (maxValue != null ? maxValue : "") + "]\n";
		}
	}

	/**
	 * Generate the {@code GnuPlot} command to set the axis tic style.
	 * 
	 * @return The axis tic command string
	 */
	public String ticsCommandString() {
		// TODO: Create this command string as needed.
		// "set ytics nomirror\n"
		// "set y2tics border\n"
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
