package uk.co.awe.pmat.graph.gnuplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static uk.co.awe.pmat.graph.GraphConfig.*;
import uk.co.awe.pmat.graph.GraphConfig;
import uk.co.awe.pmat.graph.Plottable;
import uk.co.awe.pmat.graph.PlottableLine;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * The class responsible for writing GnuPlot command files to create Bar graphs.
 * 
 * @author AWE Plc copyright 2013
 */
final class BarChartCommandFileWriter extends CommandFileWriter {

	private final GnuPlotter gnuPlotter;

	/**
	 * Create a new {@code BarChartCommandFileWriter}.
	 * 
	 * @param gnuPlotter
	 *            the plotter being used.
	 * @param x1
	 *            the GnuPlot x1 axis state.
	 * @param x2
	 *            the GnuPlot x2 axis state.
	 * @param y1
	 *            the GnuPlot y1 axis state.
	 * @param y2
	 *            the GnuPlot y2 axis state.
	 */
	BarChartCommandFileWriter(GnuPlotter gnuPlotter, GnuPlotAxis x1,
			GnuPlotAxis x2, GnuPlotAxis y1, GnuPlotAxis y2) {
		super(x1, x2, y1, y2);
		this.gnuPlotter = gnuPlotter;
	}

	/**
	 * Write a GnuPlot command file to produce a bar chart plot.
	 * 
	 * @param graphConfig
	 *            the graph configuration settings.
	 * @param plottables
	 *            the data we are plotting.
	 * @param outputStream
	 *            the output stream we are writing the command file to.
	 * @param forExport
	 *            {@code false} if this plot is being produced to be used
	 *            internally or {@code true} if it is to be saved.
	 * @throws IOException
	 *             if a problem occurs during writing of the command file.
	 */
	@Override
	void writeCommandFile(GraphConfig graphConfig,
			List<? extends Plottable> plottables, BufferedWriter outputStream,
			boolean forExport) throws IOException {

		updateGnuPlotAxes(graphConfig);
		updateGnuPlotAxesLabels(plottables, graphConfig);

		writeAxesCommands(outputStream);

		if (gnuPlotter.getXtics().size() > 0) {
			outputStream.write("set xtics (");
			String delim = "";
			for (Map.Entry<String, Integer> entry : gnuPlotter.getXtics()
					.entrySet()) {
				outputStream.write(delim + "\"" + entry.getKey() + "\"");
				outputStream.write(" " + entry.getValue());
				delim = ", ";
			}
			outputStream.write(")\n");
		}

		outputStream.write("set style data histogram\n");
		outputStream.write("set style histogram cluster gap 1\n");
		outputStream.write("set style fill solid 1 border -1\n");
		outputStream.write("set boxwidth 0.95\n");
		outputStream.write("set grid\n");
		outputStream.write("set key" + " "
				+ graphConfig.getKeyOption(KeyShow.class) + " "
				+ graphConfig.getKeyOption(KeyPlacement.class) + " "
				+ graphConfig.getKeyOption(KeyHorizontalPosition.class) + " "
				+ graphConfig.getKeyOption(KeyVerticalPosition.class) + " "
				+ graphConfig.getKeyOption(KeyOrientation.class) + " "
				+ graphConfig.getKeyOption(KeyBox.class) + "\n");
		outputStream.write("set pointsize 1\n");
		outputStream.write("plot ");

		boolean firstPlot = true;
		for (Plottable plottable : plottables) {
			if (!firstPlot) {
				outputStream.write(", ");
			}
			outputStream.write(getGnuPlotCommandString(plottable, forExport,
					graphConfig));
			firstPlot = false;
		}
	}

	/**
	 * Create the GnuPlot "plot" line for the given {@code Plottable} data.
	 * 
	 * @param plotable
	 *            the data we are plotting.
	 * @param forExport
	 *            {@code false} if this plot is being produced to be used
	 *            internally or {@code true} if it is to be saved.
	 * @param graphConfig
	 *            the graph configuration settings.
	 * @return the GnuPlot "plot" command string.
	 */
	private String getGnuPlotCommandString(Plottable plotable,
			boolean forExport, GraphConfig graphConfig) {

		final File dataFile = gnuPlotter.getDataFile(plotable);
		if (dataFile == null) {
			throw new NullPointerException("Datafile must be created before "
					+ "getGnuPlotCommandString is called.");
		}

		final StringBuilder plotCommand = new StringBuilder();

		final String fileName = forExport ? "\"" + dataFile.getName() + "\""
				: "\"" + dataFile.getPath() + "\"";

		int seriesIdx = 2;
		String delim = "";

		for (PlottableLine line : plotable.getPlottableLines()) {
			plotCommand.append(delim).append("\\\n");
			plotCommand.append(fileName);
			plotCommand.append("using ").append(seriesIdx).append(":xtic(1) ");
			plotCommand.append("title \"").append(
					StringUtils.capitaliseWords(graphConfig.getGraphLabel(line
							.getName()))).append("\" ");
			plotCommand.append("fill pattern ").append(seriesIdx - 1);
			plotCommand
					.append(lineColourCommand(line.getLineType().getColour()));
			delim = ", ";
			++seriesIdx;
		}

		return plotCommand.toString();
	}

}
