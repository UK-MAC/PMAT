package uk.co.awe.pmat.graph;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.utils.DefaultHashMap.Creator;
import uk.co.awe.pmat.utils.DefaultHashMap;

/**
 * An abstract base class to implement some common functionality of the
 * {@code Plotter} interface.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class AbstractPlotter implements Plotter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPlotter.class);

    private final Map<String, Integer> xtics
            = new DefaultHashMap<>(new LinkedHashMap<String, Integer>(), new Creator<Integer>() {
        private int xTicIdx = 0;
        @Override
        public Integer create() {
            return xTicIdx++;
        }
        @Override
        public void reset() {
            xTicIdx = 0;
        }
    });

    private final Map<Plottable, File> graphDataFiles = new HashMap<>();
    private final Map<String, Double> normaliseMap = new HashMap<>();

    private int width  = Constants.Plot.IMAGE_MIN_SIZE;
    private int height = Constants.Plot.IMAGE_MIN_SIZE;
    private PlotType plotType;

    /**
     * Return the "tic" marks to display along the x-axis.
     *
     * @return the x-axis tic marks.
     */
    public Map<String, Integer> getXtics() {
        return xtics;
    }

    /**
     * Returns the data file that was written with the given {@code Plottable}
     * data.
     *
     * @param plottable the plottable data for whose data file we should return.
     * @return the data file.
     */
    public File getDataFile(Plottable plottable) {
        return graphDataFiles.get(plottable);
    }

    /**
     * Returns the stored width that should be used to create the plot.
     *
     * @return the plot width.
     */
    protected int getWidth() {
        return width;
    }

    /**
     * Returns the stored height that should be used to create the plot.
     *
     * @return the plot height.
     */
    protected int getHeight() {
        return height;
    }

    @Override
    public boolean setPlotType(PlotType pltType) {
        boolean changed = (plotType != pltType);
        plotType = pltType;
        return changed;
    }

    /**
     * Returns the {@link PlotType} that should be plotted.
     *
     * @return the plot type.
     */
    protected PlotType getPlotType() {
        return plotType;
    }

    /**
     * Launch a process with the given command to run the plotter.
     *
     * @param command the plotter command.
     * @param commandFile the command file to be parsed to the plotter.
     * @throws IOException if an error occurs running the plotter.
     */
    protected void runPlotter(String command, File commandFile) throws IOException {
        if (commandFile == null) {
            LOG.error(String.format("Attempt to run %s with a "
                    + "null command file.", this.getClass().getSimpleName()));
            throw new NullPointerException(String.format("Attempt to run %s with a "
                    + "null command file.", this.getClass().getSimpleName()));
        }

        LOG.debug("Running command file " + commandFile.getAbsolutePath());

        final ProcessBuilder procBuilder = new ProcessBuilder(command,
                commandFile.getAbsolutePath());
        procBuilder.environment().put("GNUPLOT_DEFAULT_GDFONT", "/usr/share/fonts/truetype/verdana.ttf");
        procBuilder.directory(commandFile.getParentFile());

        final Process plot = procBuilder.start();
        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new PlotTimerTask(plot), 0, Constants.Plot.TIMEOUT_STEP);
            plot.waitFor();
            timer.cancel();
        } catch (InterruptedException ex) {
            LOG.warn("Plotter interrupted", ex);
        }
    }

    /**
     * Test to make sure the plotter is usable.
     *
     * @param command the command which is used to test the plotter.
     * @return {@code true} if the plotter is usable, {@code false} otherwise.
     */
    protected boolean testPlotter(List<String> command) {
        LOG.debug("Testing plotter " + getClass().getSimpleName());
        
        final ProcessBuilder procBuilder = new ProcessBuilder(command);
        try {
            final Process proc = procBuilder.start();
            final int exitVal = proc.waitFor();
            return (exitVal == 0);
        } catch (IOException | InterruptedException ex) {
            LOG.debug("Plotter " + getClass().getSimpleName() + " failed to run", ex);
            return false;
        }
    }

    /**
     * A wrapper for {@link #writeCommandFile(List, GraphConfig, boolean,
     * boolean, File)} used when creating a command file for internal usage
     * only, i.e. for display in the application rather than saving.
     *
     * @param plottables the data we are plotting.
     * @param graphConfig the graph configuration settings.
     * @throws IOException if a problem occurs during opening/writing of the
     * command file
     */
    @Override
    public void writeCommandFile(
            List<? extends Plottable> plottables,
            GraphConfig graphConfig) throws IOException {
        writeCommandFile(plottables, graphConfig, false, false, null);
    }

    /**
     * Write a collection of {@code Plottable} data sets to a given data
     * file.
     *
     * @param plottables The plottable data sets to write to the data file
     * @param dataFile The file to write the plot data into
     * @throws IOException if a problem occurs opening/writing the file
     * @see Plottable
     */
    @Override
    public void writeDataFile(List<? extends Plottable> plottables, File dataFile)
            throws IOException {
        if (dataFile == null) {
            throw new NullPointerException();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Plottable plottable : plottables) {
                LOG.debug("Writing data file " + dataFile);
                writePlotData(plottable, writer);
                graphDataFiles.put(plottable, dataFile);
            }
        }
    }

    /**
     * A wrapper for {@link #writePlotData(Plottable, Writer)} that generates
     * temporary files to write the data too.
     *
     * @param plottables The plottable data sets to write to the data file
     * @throws IOException if a problem occurs opening/writing the file
     * @see #writePlotData(Plottable, Writer)
     */
    @Override
    public void writeDataFile(List<? extends Plottable> plottables)
            throws IOException {

        BufferedWriter outputStream = null;
        try {
            final File tempFile = File.createTempFile(
                    Constants.Plot.DEFAULT_FILENAME, Constants.Plot.DATA_EXT);
            outputStream = new BufferedWriter(new FileWriter(tempFile));
            for (Plottable plottable : plottables) {
                LOG.debug("Writing data file " + tempFile);
                writePlotData(plottable, outputStream);
                graphDataFiles.put(plottable, tempFile);
            }
        } finally {
            if (outputStream != null) { outputStream.close(); }
        }
    }

    /**
     * Write the data in the {@code Plottable} to the given {@code Writer}.
     *
     * @param plottable the {@code Plottable} to write.
     * @param writer the {@code Writer}.
     * @throws IOException if an error occurs writing the data.
     */
    void writePlotData(Plottable plottable, Writer writer) throws IOException {
        xtics.clear();

        Object[][] graphTable = plottable.getTableData();
        if (graphTable.length == 0) {
            throw new IOException("No graph data to plot");
        }
        
        Object[] lastRow = null;

        for (Object[] row : graphTable) {
            if (Arrays.equals(row, lastRow)) {
                continue;
            }
            if (plotType == PlotType.KIVIAT) {
                outputRow(row, writer);
            } else {
                outputRow(row, writer);
            }
            lastRow = row;
        }

        if (plotType == PlotType.KIVIAT) {
            // For Kiviat plots output the first x value against so the
            // line joins up with itself.
            final String firstX = graphTable[0][0].toString();
            for (Object[] row : graphTable) {
                if (!row[0].toString().equals(firstX)) {
                    break;
                }
                outputRow(row, writer);
            }
        }
    }

    /**
     * Output a row of plottable data to the given output stream. This is done
     * in a tab separated format with "-" characters representing {@code null}s.
     *
     * @param row the row data to output.
     * @param normaliseRow the row to normalise the data against.
     * @param writer the output stream to write the data to.
     * @throws IOException if an error occurs writing the data to the output
     * stream.
     */
    private void outputRow(Object[] row, Writer writer)
            throws IOException {

        int valueIdx = 0;

        for (Object value : row) {
            if (valueIdx == 0) {
               outputXValue(value, writer);
            } else {
                writer.write("\t"); // Write delim
                
                if (plotType == PlotType.KIVIAT) {
                    if (valueIdx == 1) {
                        writer.write("1.0");
                    } else {
                        if (value == null) {
                            writer.write("-"); // Missing data value.
                        } else if (value instanceof Number) {
                            final Double val = ((Number) value).doubleValue();
                            final Double firstVal = row[1] == null
                                    ? null
                                    : ((Number) row[1]).doubleValue();
                            if (plotType == PlotType.KIVIAT) {
                                if (valueIdx == 1) {
                                    writer.write("1.0");
                                } else {
                                    if (firstVal == null) {
                                        writer.write("-");
                                    } else {
                                        writer.write(Double.toString(val / firstVal));
                                    }
                                }
                            } else {
                                writer.write(val.toString());
                            }
                        } else {
                            throw new IllegalStateException("Invalid data point found"
                                    + " in table data: " + value);
                        }
                    }
                } else {
                    if (value == null) {
                        writer.write("-"); // Missing data value.
                    } else if (value instanceof Number) {
                        final Double val = ((Number) value).doubleValue();
                        writer.write(val.toString());
                    } else {
                        throw new IllegalStateException("Invalid data point found"
                                + " in table data: " + value);
                    }
                }
            }
            ++valueIdx;
        }
        writer.write("\n");
    }

    /**
     * Write the given X value to the given writer.
     *
     * @param value the x value to write.
     * @param writer the writer.
     * @throws IllegalStateException if the given value is {@code null}.
     * @throws IOException if an error occurs writing the value.
     */
    private void outputXValue(Object value, Writer writer)
            throws IllegalStateException, IOException {
        // X Axis points
        if (value == null) {
            throw new IllegalStateException("First column values"
                    + " cannot be null in table data");
        } else {
            final String valStr = value.toString();
            if (value instanceof Number && plotType != PlotType.KIVIAT) {
                writer.write(valStr);
            } else {
                writer.write(xtics.get(valStr).toString());
            }
        }
    }

    /**
     * Set the size of the outputted PNG produced by the next execution of
     * GnuPlot. If the image size is too small or the image size has not changed
     * by a significant enough amount the image size is not set. The return
     * value of the function tells whether the setting of the image size was
     * successful or not.
     *
     * @param size The image size
     * @return {@code true} if the image size has be changed, {@code false}
     * otherwise.
     */
    @Override
    public boolean setImageSize(Dimension size) {
        int newSizeX = size.width;
        int newSizeY = size.height;
        newSizeX = Math.max(round(newSizeX, Constants.Plot.IMAGE_SIZE_INCREMENTS),
                Constants.Plot.IMAGE_MIN_SIZE);
        newSizeY = Math.max(round(newSizeY, Constants.Plot.IMAGE_SIZE_INCREMENTS),
                Constants.Plot.IMAGE_MIN_SIZE);

        if ((Math.abs(newSizeX - width) > Constants.Plot.IMAGE_SIZE_INCREMENTS)
                    || (Math.abs(newSizeY - height) > Constants.Plot.IMAGE_SIZE_INCREMENTS)) {
            width = newSizeX;
            height = newSizeY;
            return true;
        }
        return false;
    }

    /**
     * Round the first argument (a) to the nearest multiple (n) of the second
     * argument (b) such that n*b &lt; a.
     *
     *
     * @param a the first argument.
     * @param b the second argument.
     * @return a, rounded.
     */
    private int round(int a, int b) {
        return (a / b) * b;
    }

    /**
     * A {@code TimerTask} which is used to monitor the plotter, flushing and
     * logging its error stream and destroying it if it is taking too long.
     */
    private static class PlotTimerTask extends TimerTask {

        private final long startTime = System.currentTimeMillis();
        private final Process plot;

        /**
         * Create a new {@code PlotTimerTask},
         *
         * @param plot the plot process to monitor.
         */
        PlotTimerTask(Process plot) {
            this.plot = plot;
        }

        @Override
        public void run() {
            final long time = System.currentTimeMillis();
            if (time - startTime > Constants.Plot.TIMEOUT) {
                LOG.warn("Plotter timed out and had to be destroyed");
                plot.destroy();
            }
            StringWriter errorOut = null;
            try {
                errorOut = new StringWriter();
                while (true) {
                    int chr = plot.getErrorStream().read();
                    if (chr == -1) {
                        break;
                    }
                    errorOut.write(chr);
                }
            } catch (IOException ex) {
                LOG.error("Error in Plotter timer", ex);
            } finally {
                if (errorOut != null) {
                    try {
                        String error = errorOut.toString();
                        if (error.length() != 0) {
                            LOG.debug("Plotter Error: " + errorOut.toString());
                        }
                        errorOut.close();
                    } catch (IOException ex) {
                        LOG.warn("Failed to close Plotter error output", ex);
                    }
                }
            }
        }

    }
}