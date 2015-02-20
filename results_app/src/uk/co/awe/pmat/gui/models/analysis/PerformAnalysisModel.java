package uk.co.awe.pmat.gui.models.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import uk.co.awe.pmat.analysis.AnalysisMethodJarLoader;
import uk.co.awe.pmat.db.AnalysisMethodData;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.graph.GraphModel;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.events.EventHub.EventType;
import uk.co.awe.pmat.gui.events.EventListener;
import uk.co.awe.pmat.utils.ChangeEventSupport;

/**
 * The model underlying each line in the {@code PerformAnalysisListPanel}.
 * 
 * @author AWE Plc copyright 2013
 */
public final class PerformAnalysisModel implements GuiModel, EventListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(PerformAnalysisModel.class);

	private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
	private final GraphModel graphModel;
	private final EventHub eventHub;
	private final List<PerformAnalysisAxisModel> performAnalysisAxisModels;
	private final List<Class<AnalysisMethod>> analysisMethods;

	/**
	 * Create a new {@code PerformAnalysisModel}.
	 * 
	 * @param analysisModel
	 *            the underlying analysis model.
	 */
	public PerformAnalysisModel(GraphModel graphModel, EventHub eventHub) {
        this.graphModel = graphModel;
        this.eventHub = eventHub;
        performAnalysisAxisModels = new ArrayList<>();
        analysisMethods = new ArrayList<>();

        loadAnalysisMethods();
    }

	@Override
	public void addChangeListener(ChangeListener listener) {
		eventSupport.addChangeListener(listener);
	}

	@Override
	public void receiveEvent(EventType eventType) {
		for (PerformAnalysisAxisModel axisModel : performAnalysisAxisModels) {
			axisModel.updateAxisModel();
		}
	}

	/**
	 * Return the analysis methods that can be used by the {@code
	 * PerformAnalysisAxisModel} to perform analyses.
	 * 
	 * @return the analysis methods.
	 */
	Collection<Class<AnalysisMethod>> getAnalysisMethods() {
		return Collections.unmodifiableCollection(analysisMethods);
	}

	/**
	 * Add a new axis model tho list of axis models.
	 */
	public void addAxisAnalysis() {
		if (!performAnalysisAxisModels.isEmpty()) {
			performAnalysisAxisModels.get(performAnalysisAxisModels.size() - 1)
					.setCurrent(false);
		}
		PerformAnalysisAxisModel model = new PerformAnalysisAxisModel(this,
				graphModel, eventHub);

		performAnalysisAxisModels.add(model);
		eventSupport.fireChangeEvent();
	}

	/**
	 * Return the axis models.
	 * 
	 * @return the axis models.
	 */
	public Iterable<PerformAnalysisAxisModel> getAxisAnalyses() {
		return Collections.unmodifiableCollection(performAnalysisAxisModels);
	}

	/**
	 * Remove the given axis model from the collection of axis models.
	 * 
	 * @param axisModel
	 *            the axis to remove.
	 */
	void removeAxisAnalysis(PerformAnalysisAxisModel axisModel) {
		performAnalysisAxisModels.remove(axisModel);
		eventSupport.fireChangeEvent();
	}

	/**
	 * Find and load all classes in the analysis method folder that implement
	 * the {@code AnalysisMethod} interface.
	 */
	private void loadAnalysisMethods() {
		final String analysisMethodFolder = Constants.Analysis.METHOD_FOLDER;

		File analysesFolder = new File(analysisMethodFolder);
		if (!analysesFolder.exists()) {
			LOG.error("The analysis methods folder does not exist: "
					+ analysisMethodFolder);
			return;
		}
		for (File analysisFile : analysesFolder.listFiles()) {
			if (analysisFile.getName().endsWith(".jar")) {
				try {
					AnalysisMethodJarLoader analysisLoader = new AnalysisMethodJarLoader(
							analysisFile);

					for (Class<AnalysisMethod> analysisClass : analysisLoader
							.getAnalysisMethods()) {
						analysisMethods.add(analysisClass);
					}
				} catch (RuntimeException ex) {
					throw ex; // Catch everything except RuntimeExceptions
				} catch (Exception ex) {
					LOG.error("Failed to load AnalysisMethod classes from Jar "
							+ analysisFile, ex);
				}
			}
		}
	}

	/**
	 * Return a map contained the analysis method persistence data for each
	 * {@code GraphData} object.
	 * 
	 * @return the graph analyses.
	 */
	Map<GraphData, List<AnalysisMethodData>> getGraphAnalyses() {
        Map<GraphData, List<AnalysisMethodData>> perfAnalyses
                = new IdentityHashMap<>();

        for (PerformAnalysisAxisModel perfAnalysis : performAnalysisAxisModels) {
            final GraphData graphData = perfAnalysis.getGraphData();
            if (!perfAnalyses.containsKey(graphData)) {
                perfAnalyses.put(graphData, new ArrayList<AnalysisMethodData>());
            }
            final AnalysisMethodData analysisMethod = perfAnalysis.asAnalysisMethodData();
            if (analysisMethod != null) {
                perfAnalyses.get(graphData).add(analysisMethod);
            }
        }
        
        return perfAnalyses;
    }

	/**
	 * Update the state of the analysis models from the given persistence data.
	 * 
	 * @param graphModel
	 *            the graph model.
	 * @param graphMap
	 *            the persistence data.
	 */
	void updateFromAnalysis(GraphModel graphModel,
			Map<Graph, GraphData> graphMap) {
		for (Map.Entry<Graph, GraphData> entry : graphMap.entrySet()) {
			final Graph graph = entry.getKey();
			final GraphData graphData = entry.getValue();
			for (AnalysisMethodData anaData : graph.getAnalysisMethods()) {
				final PerformAnalysisAxisModel axisModel = new PerformAnalysisAxisModel(
						this, graphModel, eventHub);
				final String clsName = anaData.getClassName();
				Class<AnalysisMethod> cls = findAnalysisMethodClass(clsName);
				axisModel.updateFromAnalysis(graphData, anaData, cls);
				performAnalysisAxisModels.add(axisModel);
			}
		}
	}

	/**
	 * Find the loaded analysis method class with the given name.
	 * 
	 * @param clsName
	 *            the class name.
	 * @return the analysis method class.
	 */
	private Class<AnalysisMethod> findAnalysisMethodClass(String clsName) {
		for (Class<AnalysisMethod> method : analysisMethods) {
			if (method.getName().equals(clsName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Unknown method name " + clsName);
	}

}
