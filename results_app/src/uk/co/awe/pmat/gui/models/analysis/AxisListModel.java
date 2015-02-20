package uk.co.awe.pmat.gui.models.analysis;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.gui.analysis.SelectSeriesDialog;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.events.EventListener;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.Pair;

/**
 * The model behind behind the {@code AxisListPanel}. This is used to hold the
 * axis which are being plotted.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AxisListModel implements GuiModel, EventListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(AxisListModel.class);

	private final EventHub eventHub;
	private final AnalysisModel analysisModel;
	private final List<AxisModel> axisModels;
	private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
	private final PropertyChangeListener taskListener;

	/**
	 * Create a new {@code AxisListModel} which a reference to the underlying
	 * analysis model.
	 * 
	 * @param analysisModel
	 *            the model behind all the analysis panels.
	 * @param taskListener
	 *            the listener to be informed of background tasks.
	 */
	AxisListModel(AnalysisModel analysisModel, EventHub eventHub,
			PropertyChangeListener taskListener) {
		this.analysisModel = analysisModel;
		this.eventHub = eventHub;
		this.taskListener = taskListener;
		axisModels = new ArrayList<AxisModel>();
	}

	@Override
	public void receiveEvent(EventHub.EventType eventType) {
		for (AxisModel axisModel : axisModels) {
			axisModel.updateModels();
		}
	}

	/**
	 * Return all the axis models currently in use.
	 * 
	 * @return The axis models
	 */
	public List<AxisModel> getAxisModels() {
		return Collections.unmodifiableList(axisModels);
	}

	/**
	 * Add a new axis model.
	 */
	public void addAxisModel() {
		addAxisModel(analysisModel.getGraphModel().newGraphData());
	}

	/**
	 * Add a new axis model with the given graph data.
	 * 
	 * @param data
	 *            the graph data.
	 */
	private void addAxisModel(GraphData data) {
		axisModels.add(new AxisModel(this, data, taskListener));
		eventSupport.fireChangeEvent();
	}

	/**
	 * Remove the given axis model.
	 * 
	 * @param axisModel
	 *            The axis model to remove
	 */
	public void removeAxisModel(AxisModel axisModel) {
		axisModels.remove(axisModel);
	}

	/**
	 * Broadcast to interested parties that the state of the axis model has been
	 * altered.
	 */
	void axisStateChanged() {
		eventHub.notifyEvent(EventHub.EventType.AXIS);
	}

	/**
	 * Display a series dialog allowing setting/editing of the series for this
	 * axis.
	 * 
	 * @param axisNumber
	 *            The number of this axis in the {@code AxisListPanel}
	 * @param graphData
	 *            The graph data associated with the axis for whom the series
	 *            are being modified
	 */
	void showSeriesDialog(String axisNumber, GraphData graphData) {
		SelectSeriesDialog.showSeriesDialog("Edit series for axis "
				+ axisNumber, analysisModel, eventHub, graphData, taskListener);
	}

	/**
	 * Return whether this {@code AxisListModel} contains any {@code AxisModel}
	 * s.
	 * 
	 * @return {@code true} if the axis list is empty, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return axisModels.isEmpty();
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		eventSupport.addChangeListener(listener);
	}

	/**
	 * Update the state of the axis list from the given persistence data.
	 * 
	 * @param graphMap
	 *            the persistence state.
	 */
	void updateFromAnalysis(Map<Graph, GraphData> graphMap) {
		for (GraphData graphData : graphMap.values()) {
			axisModels.add(new AxisModel(this, graphData, taskListener));
		}
	}

	/**
	 * Return the list of possible X axis values.
	 * 
	 * @return the x axis values.
	 */
	List<Axis> getxAxisValues() {
		final List<Axis> values = new ArrayList<Axis>();
		try {
			for (String param : analysisModel.getParameterNames()) {
				values.add(AxisType.PARAMETER.newAxis(AxisName.X1, param));
			}
			for (String result : analysisModel.getResultNames()) {
				values.add(AxisType.RESULT.newAxis(AxisName.X1, result));
			}
			for (MetaData.Type type : MetaData.Type.values()) {
				values
						.add(AxisType.META_DATA.newAxis(AxisName.X1, type
								.name()));
			}
		} catch (DatabaseException ex) {
			LOG.error("Failed to get parameter name list", ex);
		}
		return values;
	}

	/**
	 * Return the list of possible Y axis values.
	 * 
	 * @return the y axis values.
	 */
	List<Axis> getyAxisValues() {
		final List<Axis> values = new ArrayList<Axis>();
		try {
			for (String result : analysisModel.getResultNames()) {
				values.add(AxisType.RESULT.newAxis(AxisName.Y1, result));
			}
			for (Pair<String, DerivedData> pair : analysisModel
					.getDerivedDataModel().getDerivedData()) {
				final String name = pair.getFirst();
				final DerivedData derivedData = pair.getSecond();
				values.add(Axis.newDerivedAxis(AxisName.Y1, name, derivedData));
			}
		} catch (Exception ex) {
			LOG.error("Failed to get result name list", ex);
		}
		return values;
	}

	/**
	 * Return the list of possible ranks.
	 * 
	 * @return the ranks.
	 */
	List<Rank> getRanks() {
		try {
			return analysisModel.getRanks();
		} catch (DatabaseException ex) {
			LOG.error("Failed to get result name list", ex);
		}
		return Collections.emptyList();
	}

}
