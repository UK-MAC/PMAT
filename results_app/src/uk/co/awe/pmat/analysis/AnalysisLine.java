package uk.co.awe.pmat.analysis;

import uk.co.awe.pmat.LineStyle;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.graph.PlottableLine;

/**
 * An implementation of the {@code PlottableLine} interface to allow for the
 * plotting of analysis lines.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AnalysisLine implements PlottableLine {

	private final LineType lineType;
	private final String name;

	/**
	 * Create a new {@code AnalysisLine} with given name and line type.
	 * 
	 * @param name
	 *            The name to display against this line on the plot
	 * @param lineType
	 *            The line type with which to plot this line
	 */
	public AnalysisLine(String name, LineType lineType) {
		this.lineType = lineType.copy();
		this.lineType.setStyle(LineStyle.LINE);
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public LineType getLineType() {
		return this.lineType;
	}

}
