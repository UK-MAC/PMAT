package uk.co.awe.pmat.graph.gnuplot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.graph.GraphConfig;
import uk.co.awe.pmat.graph.Plottable;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * A base class for GnuPlot command file writers. These are the classes that
 * will be used to write the specific command files needed to create different
 * types of GnuPlot graphs, i.e. polar, bar, scatter, etc.
 * 
 * @author AWE Plc copyright 2013
 */
abstract class CommandFileWriter {

	private final GnuPlotAxis x1;
	private final GnuPlotAxis x2;
	private final GnuPlotAxis y1;
	private final GnuPlotAxis y2;

	/**
	 * Create a new {@code CommandFileWriter}.
	 * 
	 * @param x1
	 *            the GnuPlot x1 axis state.
	 * @param x2
	 *            the GnuPlot x2 axis state.
	 * @param y1
	 *            the GnuPlot y1 axis state.
	 * @param y2
	 *            the GnuPlot y2 axis state.
	 */
	CommandFileWriter(GnuPlotAxis x1, GnuPlotAxis x2, GnuPlotAxis y1,
			GnuPlotAxis y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	/**
	 * Write the command file need to drive {@code GnuPlot}.
	 * 
	 * @param graphConfig
	 *            the graph configuration.
	 * @param plottables
	 *            the plottable data.
	 * @param outputStream
	 *            the output stream to write the commands to.
	 * @param forExport
	 *            {@code true} if we are creating a command file to write
	 *            exported images, {@code false} if we are creating temporary
	 *            application only images.
	 * @throws IOException
	 *             if an error occurs writing the commands.
	 */
	abstract void writeCommandFile(GraphConfig graphConfig,
			List<? extends Plottable> plottables, BufferedWriter outputStream,
			boolean forExport) throws IOException;

	/**
	 * Update the GnuPlot axes to be consistent with the state stored in the
	 * graph config model.
	 * 
	 * @param graphConfig
	 *            the graph config model.
	 */
	protected void updateGnuPlotAxes(GraphConfig graphConfig) {
		x1.setLogScale(graphConfig.getAxisLog(AxisName.X1) ? 2 : 0);
		x1.setMinValue(graphConfig.getAxisMinimum(AxisName.X1));
		x1.setMaxValue(graphConfig.getAxisMaximum(AxisName.X1));

		y1.setLogScale(graphConfig.getAxisLog(AxisName.Y1) ? 2 : 0);
		y1.setMinValue(graphConfig.getAxisMinimum(AxisName.Y1));
		y1.setMaxValue(graphConfig.getAxisMaximum(AxisName.Y1));
	}

	/**
	 * Update the GnuPlot axes labels to be consistent with the labels stored in
	 * the graph config model.
	 * 
	 * @param plottables
	 *            the plottable data being displayed.
	 * @param graphConfig
	 *            the graph config model.
	 */
	protected void updateGnuPlotAxesLabels(List<? extends Plottable> plottables,
            GraphConfig graphConfig) {

        // TODO: Need to add functionality for x2 and y2 labels as required.

        Set<String> xLabelNames = new LinkedHashSet<>();
        Set<String> yLabelNames = new LinkedHashSet<>();

        for (Plottable plottable : plottables) {
            xLabelNames.add(graphConfig.getGraphLabel(plottable.getXAxis().displayName()));
            yLabelNames.add(graphConfig.getGraphLabel(plottable.getYAxis().displayName()));
        }

        String x1LogLabel = x1.getLogScale() == 0 ? "" : "Log Of ";
        String y1LogLabel = y1.getLogScale() == 0 ? "" : "Log Of ";

        x1.setLabel(x1LogLabel + StringUtils.joinStrings(xLabelNames, ", "));
        y1.setLabel(y1LogLabel + StringUtils.joinStrings(yLabelNames, ", "));
    }

	/**
	 * Write the GnuPlot commands to display the axes correctly.
	 * 
	 * @param outputStream
	 *            the output stream of the command file we are writing.
	 * @throws IOException
	 *             if a problem occurs during writing of the command file.
	 */
	protected void writeAxesCommands(BufferedWriter outputStream)
			throws IOException {
		outputStream.write(x1.labelCommandString());
		outputStream.write(x2.labelCommandString());
		outputStream.write(y1.labelCommandString());
		outputStream.write(y2.labelCommandString());

		outputStream.write(x1.logscaleCommandString());
		outputStream.write(x2.logscaleCommandString());
		outputStream.write(y1.logscaleCommandString());
		outputStream.write(y2.logscaleCommandString());

		outputStream.write(x1.rangeCommandString());
		outputStream.write(x2.rangeCommandString());
		outputStream.write(y1.rangeCommandString());
		outputStream.write(y2.rangeCommandString());
	}

	/**
	 * Turn the line style into a gnu plot command string, beginning at the
	 * "with" command, e.g. "with points lw 1 lc rgb '#000000'".
	 * 
	 * @param lineType
	 *            the style of the line.
	 * @return the command string.
	 */
	protected String lineStyleCommand(LineType lineType) {

		String line = "with ";

		// LINE STYLE
		switch (lineType.getStyle()) {
		case BARS:
			line += "boxes";
			break;
		case DOTS:
			line += "dots";
			break;
		case IMPULSES:
			line += "impulses";
			break;
		case LINE:
			line += "lines";
			break;
		case LINE_AND_POINTS:
			line += "linespoints";
			break;
		case POINTS:
			line += "points";
			break;
		case STEPS:
			line += "steps";
			break;
		default:
			throw new IllegalArgumentException("Unknown line type " + lineType);
		}

		// WIDTH
		line += " lw " + lineType.getWidth();

		// COLOUR
		// Get the color as string, this includes alpha so get rid of this, an
		// added benefit is that 000000 is still shown.
		line += lineColourCommand(lineType.getColour());

		return line;
	}

	/**
	 * Create the GnuPlot commands needed to set the colour of the line.
	 * 
	 * @param colour
	 *            the line colour to use.
	 * @return the GnuPlot line colour command.
	 */
	protected String lineColourCommand(GraphColour colour) {
		int col = colour.getColor().getRGB();
		String colString = Integer.toHexString(col).toUpperCase();
		colString = colString.substring(2);
		return " lc rgb \'#" + colString + "\'";
	}
}
