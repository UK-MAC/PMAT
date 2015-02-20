package uk.co.awe.pmat.graph.gnuplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.graph.AbstractPlotter;
import uk.co.awe.pmat.graph.GraphConfig;
import uk.co.awe.pmat.graph.Plottable;

/**
 * A class to handle the interaction with {@code GnuPlot} including creating the
 * command and data files and launching the sub process.
 * 
 * @author AWE Plc copyright 2013
 */
public final class GnuPlotter extends AbstractPlotter {

	private static final Logger LOG = LoggerFactory.getLogger(GnuPlotter.class);

	private static final String PNG_EXTENSION = Constants.Plot.PNG_EXT;
	private static final String TEX_EXTENSION = Constants.Plot.TEX_EXT;

	private final BarChartCommandFileWriter barChartCommandFileWriter;
	private final KiviatCommandFileWriter kiviatCommandFileWriter;
	private final ScatterCommandFileWriter scatterCommandFileWriter;

	private File currentCommandFile;
	private File currentPlotFile;

	/**
	 * Create a new {@code GnuPlotter} instance.
	 */
	public GnuPlotter() {
		final GnuPlotAxis x1 = new GnuPlotAxis(AxisName.X1);
		final GnuPlotAxis x2 = new GnuPlotAxis(AxisName.X2);
		final GnuPlotAxis y1 = new GnuPlotAxis(AxisName.Y1);
		final GnuPlotAxis y2 = new GnuPlotAxis(AxisName.Y2);
		barChartCommandFileWriter = new BarChartCommandFileWriter(this, x1, x2,
				y1, y2);
		kiviatCommandFileWriter = new KiviatCommandFileWriter(this, x1, x2, y1,
				y2);
		scatterCommandFileWriter = new ScatterCommandFileWriter(this, x1, x2,
				y1, y2);
	}

	@Override
	public File getPlotFile() {
		return currentPlotFile;
	}

	@Override
	public void runPlotter() throws IOException {
		super.runPlotter(Constants.Plot.Gnuplot.COMMAND, currentCommandFile);
	}

	@Override
	public boolean testPlotter() {
		return testPlotter(Constants.Plot.Gnuplot.TEST_COMMAND);
	}

	@Override
	public String toString() {
		return "GnuPlot";
	}

	/**
	 * Write the GnuPlot command file which is used to provide all the
	 * instructions GnuPlot needs to find the data files and produce the plots.
	 * This method must be called <i>after</i> all the data files have been
	 * written as the file names are passed onto GnuPlot.
	 * 
	 * @param plottables
	 *            the data we are plotting.
	 * @param graphConfig
	 *            the graph configuration settings.
	 * @param forExport
	 *            {@code false} if this plot is being produced to be used
	 *            internally or {@code true} if it is to be saved.
	 * @param forTeX
	 *            {@code true} if we are creating a GnuPlot for export to LaTeX
	 *            or {@code false} otherwise.
	 * @param commandFile
	 *            the file we are writing the commands into.
	 * @throws IOException
	 *             if a problem occurs during opening/writing of the command
	 *             file.
	 */
	@Override
    public void writeCommandFile(
            List<? extends Plottable> plottables,
            GraphConfig graphConfig,
            boolean forExport,
            boolean forTeX,
            File commandFile) throws IOException {

        if (commandFile != null) {
            String fileName = commandFile.getAbsolutePath();
            fileName = fileName.substring(0, fileName.length() - Constants.Plot.Gnuplot.COMMAND_EXT.length());
            currentPlotFile = new File(fileName + (forTeX ? TEX_EXTENSION : PNG_EXTENSION));
            currentCommandFile = commandFile;
        } else {
            currentCommandFile = File.createTempFile(Constants.Plot.DEFAULT_FILENAME, Constants.Plot.Gnuplot.COMMAND_EXT);
            currentCommandFile.deleteOnExit();
            currentPlotFile = File.createTempFile(Constants.Plot.DEFAULT_FILENAME, forTeX ? TEX_EXTENSION : PNG_EXTENSION);
            currentPlotFile.deleteOnExit();
        }

        LOG.debug("Creating plot file " + currentPlotFile);
        LOG.debug("Creating command file " + currentCommandFile);
        try (BufferedWriter outputStream = new BufferedWriter(new FileWriter(currentCommandFile))) {
            if (forTeX) {
                outputStream.write("set terminal epslatex color solid lw 2 rounded size "
                        + Constants.Plot.Gnuplot.TEX_GRAPH_SIZE + " font \"phv\" 8 \n");
            } else {
                outputStream.write("set terminal png truecolor rounded medium size "
                        + getWidth() + ", " + getHeight() + "\n");
            }

            if (forExport) {
                outputStream.write("set output \"" + currentPlotFile.getName() + "\"\n");
            } else {
                outputStream.write("set output \"" + currentPlotFile.getPath() + "\"\n");
            }

            switch (getPlotType()) {
                case NORMAL:
                    scatterCommandFileWriter.writeCommandFile(graphConfig, plottables, outputStream, forExport);
                    break;
                case BAR:
                    barChartCommandFileWriter.writeCommandFile(graphConfig, plottables, outputStream, forExport);
                    break;
                case KIVIAT:
                    kiviatCommandFileWriter.writeCommandFile(graphConfig, plottables, outputStream, forExport);
                    break;
                default:
                    throw new IllegalStateException("Unknown plot type " + getPlotType());
            }
        }
    }

}
