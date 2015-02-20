package uk.co.awe.pmat.gui.models.analysis;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.analysis.RestrictionCollection;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.AnalysisMethodData;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.DerivedData;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import uk.co.awe.pmat.graph.GraphConfig;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.graph.GraphModel;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.events.EventHub.EventType;
import uk.co.awe.pmat.gui.events.EventListener;
import uk.co.awe.pmat.gui.graph.GraphDataPanel;
import uk.co.awe.pmat.gui.models.DataFilterModel;
import uk.co.awe.pmat.gui.models.DerivedDataModel;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.Pair;

/**
 * The model underlying the Analysis panel and any functionality shared between
 * the Analysis panel's child panels.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AnalysisModel implements GuiModel, EventListener,
		RestrictionCollection {

	private static final Logger LOG = LoggerFactory
			.getLogger(AnalysisModel.class);

	private final Configuration config;
	private final PropertyChangeListener taskListener;
	private final EventHub eventHub;

	private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);

	// GUI Models
	private final GraphModel graphModel;
	private final DataFilterModel dataFilterModel;
	private final PerformAnalysisModel performAnalysisModel;
	private final SaveAnalysisModel saveAnalysisModel;
	private final DerivedDataModel derivedDataModel;
	private final AxisListModel axisListModel;

	/**
	 * Create an {@code AnalysisModel} instance with the application properties
	 * and a connection to the database.
	 * 
	 * @param config
	 *            the application configuration.
	 * @param taskListener
	 *            a listener to be informed of the state of any back ground
	 *            tasks launched.
	 */
	public AnalysisModel(Configuration config,
			PropertyChangeListener taskListener) {
		this.config = config;
		this.taskListener = taskListener;
		this.eventHub = new EventHub(taskListener);

		graphModel = new GraphModel(this, eventHub);
		dataFilterModel = new DataFilterModel(eventHub);
		performAnalysisModel = new PerformAnalysisModel(graphModel, eventHub);
		saveAnalysisModel = new SaveAnalysisModel(this, config);
		derivedDataModel = new DerivedDataModel(eventHub);
		axisListModel = new AxisListModel(this, eventHub, taskListener);

		eventHub.addEventListener(graphModel, EventHub.EventType.AXIS,
				EventHub.EventType.ANALYSIS, EventHub.EventType.DERIVED_DATA,
				EventHub.EventType.SERIES, EventHub.EventType.FILTERS,
				EventHub.EventType.GRAPH);
		eventHub.addEventListener(performAnalysisModel,
				EventHub.EventType.AXIS, EventHub.EventType.DERIVED_DATA);
		eventHub.addEventListener(derivedDataModel, EventHub.EventType.FILTERS);
		eventHub.addEventListener(axisListModel,
				EventHub.EventType.DERIVED_DATA, EventHub.EventType.FILTERS);
		eventHub.addEventListener((EventListener) this);
	}

	@Override
	public void receiveEvent(EventType eventType) {
		eventSupport.fireChangeEvent();
	}

	/**
	 * Return the model that underpins the {@code DataFilterPanel}.
	 * 
	 * @return The criteria list panel model
	 */
	public DataFilterModel getDataFilterModel() {
		return dataFilterModel;
	}

	/**
	 * Return the model that underpins the {@code GraphPanel}.
	 * 
	 * @return The graph panel model
	 * @see uk.co.awe.pmat.gui.graph.GraphPanel
	 */
	public GraphModel getGraphModel() {
		return graphModel;
	}

	/**
	 * Return the model that underpins the {@code DerivedDataPanel}.
	 * 
	 * @return the derived data panel model.
	 * @see uk.co.awe.pmat.gui.analysis.DerivedDataPanel
	 */
	public DerivedDataModel getDerivedDataModel() {
		return derivedDataModel;
	}

	/**
	 * Return the model that underpins the {@code PerformAnalysisPanel}.
	 * 
	 * @return the perform analysis panel model.
	 * @see uk.co.awe.pmat.gui.analysis.PerformAnalysisPanel
	 */
	public PerformAnalysisModel getPerformAnalysisModel() {
		return performAnalysisModel;
	}

	/**
	 * Return the model that underpins the {@code SaveAnalysisPanel}.
	 * 
	 * @return the save analysis panel model.
	 * @see uk.co.awe.pmat.gui.analysis.SaveAnalysisPanel
	 */
	public SaveAnalysisModel getSaveAnalysisModel() {
		return saveAnalysisModel;
	}

	/**
	 * Return the model that underpins the {@code AxisListPanel}.
	 * 
	 * @return the axis list panel model.
	 */
	public AxisListModel getAxisSelectionModel() {
		return axisListModel;
	}

	/**
	 * Return the model that underpins the {@code GraphLabelPanel} as well as
	 * the {@code GraphConfigPanel}.
	 * 
	 * @return The graph configuration model
	 * @see uk.co.awe.pmat.gui.graph.GraphLabelPanel
	 * @see uk.co.awe.pmat.gui.graph.GraphConfigPanel
	 */
	public GraphConfig getGraphConfig() {
		return graphModel.getGraphConfig();
	}

	/**
	 * Returns possible result names constrained by the criteria list.
	 * 
	 * @return A list of the result names
	 * @throws DatabaseException
	 *             if no connection to the database could be established
	 */
	public List<String> getResultNames() throws DatabaseException {
		return dataFilterModel.getFilteredResultNames();
	}

	/**
	 * Returns possible parameter names constrained by the criteria list.
	 * 
	 * @return A list of the parameter names
	 * @throws DatabaseException
	 *             if no connection to the database could be established
	 */
	public List<String> getParameterNames() throws DatabaseException {
		return dataFilterModel.getFilteredParameterNames();
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		eventSupport.addChangeListener(listener);
	}

	/**
	 * Generate a collection of {@code GraphDataPanel}s with one for each graph
	 * data table. These panels are used to display the data being used to
	 * create the graph panels.
	 * 
	 * @return The collection of panels
	 * @see GraphDataPanel
	 */
	public Collection<JPanel> getGraphDataPanels() {
		final List<JPanel> graphDataPanels = new ArrayList<JPanel>();

		for (TableModel model : graphModel.getGraphDataTableModels()) {
			graphDataPanels.add(new GraphDataPanel(model, config));
		}

		return graphDataPanels;
	}

	/**
	 * Export an analysis that has been saved into the database into an XML
	 * file.
	 * 
	 * @param analysis
	 *            the analysis entity object to export.
	 * @param file
	 *            the file to export to.
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 * @throws IOException
	 *             if an error occurs when writing the analysis to file.
	 */
	void exportAnalysis(Analysis analysis, File file) throws DatabaseException,
			IOException {

		XMLSerialiser serialiser = new XMLSerialiser();
		List<XMLSerialisable> objects = new ArrayList<XMLSerialisable>();
		objects.add(analysis);
		serialiser.serialise(file, objects);
	}

	/**
	 * Load an analysis from the database and use it to populate all the
	 * relevant analysis models to recreate the displayed analysis.
	 * 
	 * @param analysis
	 *            the analysis entity object to load from.
	 * @return a new analysis model with state set to represent the loaded
	 *         analysis.
	 */
	@SuppressWarnings("unchecked")
	AnalysisModel loadAnalysis(Analysis analysis) {

		AnalysisModel analysisModel = new AnalysisModel(config, taskListener);

		analysisModel.dataFilterModel.addAllRestrictions(analysis
				.getRestrictions());
		analysisModel.derivedDataModel.updateFromAnalysis(analysis
				.getDerivedData());
		Collection<Graph> graphs = analysisModel.derivedDataModel
				.updateDerivedDataAxes(analysis.getGraphs());
		Map<Graph, GraphData> graphMap = analysisModel.graphModel
				.updateFromAnalysis(graphs);
		analysisModel.axisListModel.updateFromAnalysis(graphMap);
		analysisModel.graphModel.getGraphConfig().addLabelRenames(
				analysis.getLabelRenames());
		analysisModel.performAnalysisModel.updateFromAnalysis(
				analysisModel.graphModel, graphMap);
		analysisModel.saveAnalysisModel.updateFromAnalysis(analysis);

		analysisModel.eventHub.notifyEvent(EventHub.EventType.AXIS);
		return analysisModel;
	}

	/**
	 * Save the currently displayed analysis into the database.
	 * 
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 */
	void saveAnalysis() throws DatabaseException {
		final Date date = Calendar.getInstance().getTime();
		final String creator = saveAnalysisModel.getCreator();
		final String notes = saveAnalysisModel.getNotes();
		final boolean isPrivate = saveAnalysisModel.isPrivate();
		final List<DerivedData> derivedData = derivedDataModel
				.getAllDerivedData();
		final List<Restriction> restrictions = dataFilterModel
				.getRestrictions();
		final List<Pair<String, String>> renames = graphModel.getGraphConfig()
				.getLabelRenames();
		final Map<GraphData, List<AnalysisMethodData>> perfAnalyses = performAnalysisModel
				.getGraphAnalyses();
		final List<Graph> graphs = graphModel.getGraphs(perfAnalyses);

		Analysis analysis = new Analysis(creator, date, notes, isPrivate,
				graphs, derivedData, restrictions, renames);
		DatabaseManager.getConnection().save(analysis);
	}

	/**
	 * Delete the given analysis entity object from the database.
	 * 
	 * @param analysis
	 *            the analysis to delete.
	 * @throws DatabaseException
	 *             if no connection to the database could be established.
	 */
	void deleteAnalysis(Analysis analysis) throws DatabaseException {
		DatabaseManager.getConnection().delete(analysis);
	}

	/**
	 * Return the restriction currently stored in the analysis.
	 * 
	 * @return the restrictions.
	 */
	public Collection<Restriction> getRestrictions() {
		return dataFilterModel.getRestrictions();
	}

	/**
	 * Return the all the possible ranks for {@code DataSets}s filtered using
	 * the current restrictions.
	 * 
	 * @return the {@code Rank}s.
	 * @throws DatabaseException
	 *             if an error occurs during the query.
	 */
	List<Rank> getRanks() throws DatabaseException {
		return dataFilterModel.getRanks(Category.COMPILER, null);
	}

	public EventHub getEventHub() {
		return eventHub;
	}
}
