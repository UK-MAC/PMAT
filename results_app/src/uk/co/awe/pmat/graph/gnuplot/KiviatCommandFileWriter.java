package uk.co.awe.pmat.graph.gnuplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static uk.co.awe.pmat.graph.GraphConfig.*;
import uk.co.awe.pmat.graph.GraphConfig;
import uk.co.awe.pmat.graph.Plottable;
import uk.co.awe.pmat.graph.PlottableLine;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * The class responsible for writing GnuPlot command files to create Kiviat
 * graphs.
 * 
 * @author AWE Plc copyright 2013
 */
final class KiviatCommandFileWriter extends CommandFileWriter {

	private final GnuPlotter gnuPlotter;

	/**
	 * Create a new {@code KiviatCommandFileWriter}.
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
	KiviatCommandFileWriter(GnuPlotter gnuPlotter, GnuPlotAxis x1,
			GnuPlotAxis x2, GnuPlotAxis y1, GnuPlotAxis y2) {
		super(x1, x2, y1, y2);
		this.gnuPlotter = gnuPlotter;
	}

	/**
	 * Write a GnuPlot command file to produce a Kiviat (spider's web) plot.
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

		if (plottables.size() != 1) {
			throw new IllegalArgumentException();
		}

		final int numSpokes = gnuPlotter.getXtics().size();
		final float spokeLength = 1.2F;

		outputStream.write("set polar\n");
		outputStream.write("unset ytics\n");
		outputStream.write("unset border\n");
		outputStream.write("set size square\n");
		outputStream.write(String.format("set xtics axis 0,0.2,%3.1f\n",
				spokeLength));
		outputStream
				.write(String.format("set grid polar 2*pi/%d\n", numSpokes));
		outputStream.write("set xrange [-1.35:1.35]\n");
		outputStream.write("set yrange [-1.35:1.35]\n");
		outputStream.write("#set rrange [0:1]\n");

		outputStream.write("set key" + " "
				+ graphConfig.getKeyOption(KeyShow.class) + " "
				+ graphConfig.getKeyOption(KeyPlacement.class) + " "
				+ graphConfig.getKeyOption(KeyHorizontalPosition.class) + " "
				+ graphConfig.getKeyOption(KeyVerticalPosition.class) + " "
				+ graphConfig.getKeyOption(KeyOrientation.class) + " "
				+ graphConfig.getKeyOption(KeyBox.class) + "\n");

		int idx = 0;
		// Position the spoke labels around the spokes, spoke length + 0.1
		// from the centre.
		for (String spokeName : gnuPlotter.getXtics().keySet()) {
			final double offset = spokeName.length() * 0.02;
			final double cos2pi = Math.cos(idx * 2 * Math.PI / numSpokes);
			final double sin2pi = Math.sin(idx * 2 * Math.PI / numSpokes);
			final double xPos = (spokeLength + 0.1 + offset * cos2pi * cos2pi)
					* cos2pi;
			final double yPos = (spokeLength + 0.1 + offset * cos2pi * cos2pi)
					* sin2pi;
			outputStream.write(String.format(
					"set label \"%s\" at %3.2f, %3.2f center\n", spokeName,
					xPos, yPos));
			++idx;
		}

		final String xCol = String.format("($1*2*pi/%d)", numSpokes);

		outputStream.write("plot ");
		boolean firstPlot = true;
		for (Plottable plottable : plottables) {
			if (!firstPlot) {
				outputStream.write(", ");
			}
			outputStream.write(getGnuPlotCommandString(plottable, xCol,
					forExport, graphConfig));
			firstPlot = false;
		}
	}

	/**
	 * Create the GnuPlot "plot" line for the given {@code Plottable} data.
	 * 
	 * @param plotable
	 *            the data we are plotting.
	 * @param xCol
	 *            the string to use for the x column.
	 * @param forExport
	 *            {@code false} if this plot is being produced to be used
	 *            internally or {@code true} if it is to be saved.
	 * @param graphConfig
	 *            the graph configuration settings.
	 * @return the GnuPlot "plot" command string.
	 */
	private String getGnuPlotCommandString(Plottable plotable, String xCol,
			boolean forExport, GraphConfig graphConfig) {

		final File dataFile = gnuPlotter.getDataFile(plotable);
		if (dataFile == null) {
			throw new NullPointerException("Datafile must be created before "
					+ "getGnuPlotCommandString is called.");
		}

		final StringBuilder plotCommand = new StringBuilder();

		int seriesIdx = 2;
		for (PlottableLine line : plotable.getPlottableLines()) {
			plotCommand.append("\\\n");
			if (forExport) {
				plotCommand.append("\"").append(dataFile.getName()).append(
						"\" ");
			} else {
				plotCommand.append("\"").append(dataFile.getPath()).append(
						"\" ");
			}
			plotCommand.append("using ").append(xCol).append(":").append(
					seriesIdx).append(" ");
			plotCommand.append("title \"").append(
					StringUtils.capitaliseWords(graphConfig.getGraphLabel(line
							.getName()))).append("\" ");
			plotCommand.append(lineStyleCommand(line.getLineType()));
			if (seriesIdx != (plotable.getPlottableLines().size() + 1)) {
				plotCommand.append(", ");
			}
			++seriesIdx;
		}

		return plotCommand.toString();
	}

}
