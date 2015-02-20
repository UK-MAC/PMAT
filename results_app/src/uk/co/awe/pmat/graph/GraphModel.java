package uk.co.awe.pmat.graph;

import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.ComboBoxModel;
import java.util.Collections;
import java.util.Collection;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.analysis.AnalysisData;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.AnalysisMethodData;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.graph.gnuplot.GnuPlotter;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.events.EventHub.EventType;
import uk.co.awe.pmat.gui.events.EventListener;
import uk.co.awe.pmat.gui.models.analysis.AnalysisModel;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.FileUtils;

/**
 * The model underpinning the GraphPanel. This model is mainly used for the
 * coordination of the {@code GraphData} which contains the data in a form
 * ready to be plotted, and the actual plotting of the data, via implementations
 * of the {@code Plotter} interface.
 *
 * @author AWE Plc copyright 2013
 */
public final class GraphModel implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(GraphModel.class);

    private static final String DEFAULT_FILENAME  = Constants.Plot.DEFAULT_FILENAME;
    private static final String GNUPLOT_EXTENSION = Constants.Plot.Gnuplot.COMMAND_EXT;
    private static final String DATA_EXTENSION    = Constants.Plot.DATA_EXT;
    private static final String PNG_EXTENSION     = Constants.Plot.PNG_EXT;

    private final AnalysisModel analysisModel;
    private final Plotter plotter;

    private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
    private final List<GraphData> graphDataList = new ArrayList<>();
    private final List<AnalysisData> analysisDataList = new ArrayList<>();
    private final GraphConfig graphConfig = new GraphConfig();
    private final DefaultComboBoxModel<PlotType> typeSelectionModel;

    private volatile Image plot;

    /**
     * Create a new {@code GraphModel}.
     *
     * @param analysisModel the model driving the analysis.
     */
    public GraphModel(AnalysisModel analysisModel, EventHub eventHub) {
        this.analysisModel = analysisModel;
        this.plotter = new GnuPlotter();

        typeSelectionModel = new DefaultComboBoxModel<>(PlotType.values());
        typeSelectionModel.setSelectionAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean changed = plotter
                        .setPlotType(typeSelectionModel.getSelectedElement());
                if (changed) {
                    eventSupport.fireChangeEvent();
                }
            }
        });
        
        plotter.setPlotType(typeSelectionModel.getSelectedElement());
    }

    /**
     * Returns the graph configuration model for this graph.
     *
     * @return the graph config model.
     */
    public GraphConfig getGraphConfig() {
        return graphConfig;
    }

    /**
     * Get the image stored image created by the last {@code doPlot}.
     *
     * @return The image
     */
    public Image getImage() {
        return plot;
    }

    /**
     * Run the selected {@code Plotter} for the current data set and store the
     * image ready to be returned by {@code getImage}.
     *
     * @throws IOException if there was a problem creating the files, or with
     * the execution of the {@code Plotter}.
     */
    public synchronized void doPlot() throws IOException {
        if (!readyToPlot()) {
            return;
        }
        final List<Plottable> plotData = new ArrayList<>();
        plotData.addAll(graphDataList);
        plotData.addAll(analysisDataList);

        plotter.writeDataFile(plotData);
        plotter.writeCommandFile(plotData, graphConfig);
        plotter.runPlotter();

        File plotFile = plotter.getPlotFile();
        plot = ImageIO.read(plotFile);
    }

    /**
     * Register a change listener with this model to receive events whenever the
     * model changes.
     *
     * @param listener the listener to register.
     */
    public void addChangeListener(ChangeListener listener) {
        eventSupport.addChangeListener(listener);
    }

    @Override
    public void receiveEvent(EventType eventType) {
        graphConfig.updateGraphLabels(getPlotLabels());
        
        eventSupport.fireChangeEvent();
    }

    /**
     * Create a new {@code GraphData} object and register it with this
     * {@code GraphModel}.
     *
     * @return the new {@code GraphData} object.
     */
    public synchronized GraphData newGraphData() {
        GraphData newGraphData = new GraphData(analysisModel);
        graphDataList.add(newGraphData);
        return newGraphData;
    }

    /**
     * Returns all the graph data currently stored in the graph model.
     *
     * @return the list of graph data.
     */
    public synchronized List<GraphData> getGraphData() {
        return Collections.unmodifiableList(graphDataList);
    }

    /**
     * Remove a graph data from those stored in the model.
     *
     * @param graphData the graph data to remove.
     */
    public synchronized void removeGraphData(GraphData graphData) {
        boolean wasPresent = graphDataList.remove(graphData);
        if (!wasPresent) {
            throw new IllegalArgumentException("Attempt to remove graphData not"
                    + " stored in model: " + graphData);
        }
    }

    /**
     * Save the plot as files which can be used to reproduce the plot outside of
     * PMAT.
     *
     * @param baseFileOrDir the file or directory used to create the files we
     * are writing.
     * @param forTeX {@code true} if the plotter files should produce
     * {@code TeX} compatible output, {@code false} otherwise.
     * @throws IOException if an error occurs writing the files.
     */
    public void saveAsPlotterFiles(File baseFileOrDir, boolean forTeX) throws IOException {
        final File commandFile = FileUtils.getSaveAsFile(baseFileOrDir, DEFAULT_FILENAME, GNUPLOT_EXTENSION);
        final File dataFile = FileUtils.getSaveAsFile(baseFileOrDir, DEFAULT_FILENAME, DATA_EXTENSION);

        plotter.writeDataFile(graphDataList, dataFile);
        plotter.writeCommandFile(graphDataList, graphConfig, true, forTeX, commandFile);
    }

    /**
     * Save the plot as a PNG file.
     *
     * @param baseFileOrDir the file or directory used to create the file we are
     * writing.
     * @throws IOException if an error occurs writing the file.
     */
    public void saveAsPng(File baseFileOrDir) throws IOException {
        final File outputFile = FileUtils.getSaveAsFile(baseFileOrDir, DEFAULT_FILENAME, PNG_EXTENSION);
        if (outputFile.exists()) {
            int result = JOptionPane.showConfirmDialog(null,
                    "File " + outputFile + " already exists. Do you want to overwrite?",
                    "Overwrite?",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.OK_OPTION) { return; }
        }

        // Create a BufferedImage and copy the icon image into.
        BufferedImage buffImg = new BufferedImage(
                plot.getWidth(null),
                plot.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImg.getGraphics();
        g.drawImage(plot, 0, 0, null);

        // Save the BufferedImage into the file.
        ImageIO.write(buffImg, "png", outputFile);
    }

    /**
     * Set the size of the image that will be returned by the {@code doPlot}
     * method.
     *
     * @param size the desired image size.
     * @return {@code true} if the change in size resulted in the image being
     * recreated, i.e. if it needs to be reloaded before being displayed.
     */
    public boolean setImageSize(Dimension size) {
        return plotter.setImageSize(size);
    }

    /**
     * Returns whether the data is ready to be plotted.
     *
     * @return {@code true} if the data is ready to be plotted, {@code false}
     * otherwise.
     */
    public boolean readyToPlot() {
        boolean readyToPlot = !graphDataList.isEmpty();
        for (GraphData data : graphDataList) {
            readyToPlot &= data.readyToPlot();
        }
        return readyToPlot;
    }

    /**
     * Returns all the labels that will be displayed on the plot.
     *
     * @return the plot labels.
     */
    public Collection<String> getPlotLabels() {
        final List<String> configurableOptions = new ArrayList<>();

        for (GraphData data : graphDataList) {
            if (data.getXAxis() != null) {
                configurableOptions.add(data.getXAxis().displayName());
            }
            if (data.getYAxis() != null) {
                configurableOptions.add(data.getYAxis().displayName());
            }
            for (PlottableLine group : data.getPlottableLines()) {
                if (group.getName() != null) {
                    configurableOptions.add(group.getName());
                }
            }
        }

        return configurableOptions;
    }

    /**
     * Returns table models containing the data being used to generate the plot.
     * 
     * @return the plot data tables.
     */
    public Collection<TableModel> getGraphDataTableModels() {
        final List<TableModel> graphDataTableModels = new ArrayList<>();

        for (GraphData data : graphDataList) {
            if (!data.readyToPlot()) {
                continue;
            }
            Object[][] tableData = data.getTableData();
            if (tableData.length > 0) {
                final List<String> columnNames = new ArrayList<>();
                columnNames.add(data.getXAxis().displayName());
                Collection<? extends PlottableLine> seriesGroups = data.getPlottableLines();
                if (seriesGroups.size() > 0) {
                    for (PlottableLine seriesGroup : seriesGroups) {
                        columnNames.add(seriesGroup.getName());
                    }
                } else {
                    columnNames.add(data.getYAxis().displayName());
                }
                graphDataTableModels.add(new DefaultTableModel(tableData, columnNames.toArray()));
            }
        }

        return graphDataTableModels;
    }

    /**
     * Return the number of graph data currently being held.
     *
     * @return the number of graph data.
     */
    public int getGraphDataCount() {
        return graphDataList.size();
    }

    /**
     * Add the given {@code AnalysisData} to those being stored.
     *
     * @param analysisData the analysis data to add.
     */
    public void addAnalysisData(AnalysisData analysisData) {
        if (analysisData == null) {
            throw new NullPointerException();
        }
        analysisDataList.add(analysisData);
    }

    /**
     * Remove the given {@code AnalysisData} from those being stored.
     *
     * @param analysisData the analysis data to remove.
     */
    public void removeAnalysisData(AnalysisData analysisData) {
        if (analysisData == null) {
            throw new NullPointerException();
        }
        boolean wasPresent = analysisDataList.remove(analysisData);
        if (!wasPresent) {
            throw new IllegalArgumentException("Attempt to remove AnalysisData"
                    + " not stored: " + analysisData);
        }
    }

    /**
     * Returns the persistence object representation of the graph data currently
     * being stored.
     *
     * @param graphAnalysisMethodData the analysis method data stored currently
     * held for each graph data.
     * @return the list of graph data persistence objects.
     */
    public List<Graph> getGraphs(Map<GraphData, List<AnalysisMethodData>> graphAnalysisMethodData) {
        final List<Graph> graphs = new ArrayList<>();
        for (GraphData graphData : graphDataList) {

            List<AnalysisMethodData> analysisMethods = graphAnalysisMethodData.get(graphData);
            if (analysisMethods == null) {
                analysisMethods = Collections.emptyList();
            }

            graphs.add(graphData.asGraph(analysisMethods));
        }
        return graphs;
    }

    /**
     * Populate the graph model and relevant sub models using the given graph
     * data persistence objects.
     *
     * @param graphs the graph data persistence objects.
     * @return a map of the persistence objects against the graph data that was
     * populated from them.
     */
    public Map<Graph, GraphData> updateFromAnalysis(Collection<Graph> graphs) {
        final Map<Graph, GraphData> graphMap = new HashMap<>();

        try {
            for (Graph graph : graphs) {
                final GraphData graphData = new GraphData(analysisModel);
                graphData.setXAxis(graph.getxAxis());
                graphData.setYAxis(graph.getyAxis());
                graphData.setYAxisType(graph.getyAxisType());
                for (Series series : graph.getSeries()) {
                    graphData.addSeries(series);
                }

                graphMap.put(graph, graphData);
                graphDataList.add(graphData);
            }
        } catch (DatabaseException | GraphDataException | DerivedDataException ex) {
            LOG.error("Error loading graph model from analysis", ex);
        }

        return graphMap;
    }

    /**
     * Return the combo box model holding the different {@link PlotType}s
     * available.
     *
     * @return the plot type combo box model.
     */
    public ComboBoxModel<PlotType> getGraphTypeSelectioModel() {
        return typeSelectionModel;
    }

}
