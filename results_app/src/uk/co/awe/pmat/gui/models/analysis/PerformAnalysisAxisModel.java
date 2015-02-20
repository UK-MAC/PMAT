package uk.co.awe.pmat.gui.models.analysis;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import javax.swing.event.ListDataEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.analysis.AnalysisData;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;
import uk.co.awe.pmat.db.AnalysisMethodData;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.graph.PlottableLine;
import uk.co.awe.pmat.graph.GraphModel;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.gui.utils.ListDataAdaptor;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * The model underlying each line in the {@code PerformAnalysisPanel}.
 * 
 * @author AWE Plc copyright 2013
 */
public final class PerformAnalysisAxisModel implements GuiModel {

	private static final Logger LOG = LoggerFactory
			.getLogger(PerformAnalysisAxisModel.class);

	private static final String ANALYSIS_TYPES_HEADER = "Select Analysis Type...";
	private static final String AXES_HEADER = "Axis...";

	private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
	private final GraphModel graphModel;
	private final PerformAnalysisModel performAnalysisModel;
	private final DefaultComboBoxModel<Class<AnalysisMethod>> analysisTypeModel;
	private final DefaultComboBoxModel<GraphData> axisModel;
	private final List<String> equations;
	private final EventHub eventHub;

	private GraphData selectedGraphData;
	private Class<AnalysisMethod> selectedAnalysisMethod;
	private AnalysisMethod selectedAnalysisMethodInstance;
	private Integer rangeFrom;
	private Integer rangeTo;
	private AnalysisData currentAnalysisData;

	private boolean current;
	private boolean configurable;
	private boolean shown;

	/**
	 * Create a new {@code PerformAnalysisAxisModel} with a link to the model
	 * behind the {@code PerformAnalysisPanel} and the model behind the graph.
	 * 
	 * @param performAnalysisModel
	 *            the model driving the perform analysis panel.
	 * @param graphModel
	 *            the mode driving the graph.
	 */
	public PerformAnalysisAxisModel(PerformAnalysisModel performAnalysisModel, GraphModel graphModel, EventHub eventHub) {
        this.graphModel = graphModel;
        this.performAnalysisModel = performAnalysisModel;
        this.eventHub = eventHub;

        analysisTypeModel = new DefaultComboBoxModel<>(ANALYSIS_TYPES_HEADER,
                performAnalysisModel.getAnalysisMethods());
        analysisTypeModel.addListDataListener(new ListDataAdaptor() {
            @Override public void selectionChanged(ListDataEvent e) {
                updateAnalysisType();
            }
        });

        axisModel = new DefaultComboBoxModel<>(AXES_HEADER,
                graphModel.getGraphData());
        axisModel.addListDataListener(new ListDataAdaptor() {
            @Override public void selectionChanged(ListDataEvent e) {
                updateAxis();
            }
        });

        equations = new ArrayList<>();
        current = true;
    }

	/**
	 * If the axis has been changed then redo the analysis.
	 */
	private void updateAxis() {
		GraphData graphData = axisModel.getSelectedElement();
		if (graphData != null && !graphData.equals(selectedGraphData)) {
			selectedGraphData = graphData;
			setRanges();
			performAnalysis();
		}
	}

	/**
	 * Returns whether the selected analysis has configuration options.
	 * 
	 * @return {@code true} if the analysis is configurable, {@code fale}
	 *         otherwise.
	 */
	public boolean isConfigurable() {
		return configurable;
	}

	/**
	 * If the analysis type has changers, create an analysis instance for the
	 * selected analysis type and redo the analysis.
	 */
	private void updateAnalysisType() {
        Class<AnalysisMethod> analysisMethod = analysisTypeModel.getSelectedElement();
        if (analysisMethod != null
                && !analysisMethod.equals(selectedAnalysisMethod)) {
            try {
                selectedAnalysisMethod = analysisMethod;
                selectedAnalysisMethodInstance = analysisMethod.newInstance();
                configurable = selectedAnalysisMethodInstance.hasConfigPanel();
                performAnalysis();
                fireChangeListeners();
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.warn("Failed to instantiate AnalysisMethod "
                        + analysisMethod.getSimpleName(), ex);
                selectedAnalysisMethod = null;
                selectedAnalysisMethodInstance = null;
            }
        }
    }

	/**
	 * Set whether the analysis shown be shown.
	 * 
	 * @param flag
	 *            the shown flag; {@code true} for the analysis to be shown,
	 *            {@code false} otherwise.
	 */
	public void setAnalysisShown(boolean flag) {
		shown = flag;
		performAnalysis();
	}

	/**
	 * Return the graph data associated with this analysis axis.
	 * 
	 * @return the graph data.
	 */
	public GraphData getGraphData() {
		return selectedGraphData;
	}

	/**
	 * Returns the list of data that falls between the range values set.
	 * 
	 * @param data
	 *            the data to filter.
	 * @return the filtered data.
	 */
	private List<DataPoint> getDataInRange(List<DataPoint> data) {
        final List<DataPoint> dataPoints = new ArrayList<>();

        for (DataPoint point : data) {
            if (point.getXValue() >= rangeFrom
                    && point.getXValue() <= rangeTo) {
                dataPoints.add(point);
            }
        }

        return dataPoints;
    }

	/**
	 * Use the selected analysis method to analyses the selected graph data and
	 * store the result.
	 */
	private void performAnalysis() {

		boolean modified = false;
		if (currentAnalysisData != null) {
			graphModel.removeAnalysisData(currentAnalysisData);
			modified = true;
		}
		currentAnalysisData = null;
		equations.clear();

		if (shown && selectedGraphData != null
				&& selectedAnalysisMethod != null) {

			final Collection<? extends PlottableLine> plottableLines = selectedGraphData
					.getPlottableLines();
			final AnalysisMethod analysisMethod = selectedAnalysisMethodInstance;

			if (plottableLines.isEmpty()) {
				final List<DataPoint> seriesData = selectedGraphData
						.getSeriesData(null);
				analysisMethod.performAnalysis(0, getDataInRange(seriesData));
				equations.add(analysisMethod.getAnalysisResult(0));
			} else {
				int seriesIdx = 0;
				for (PlottableLine plottableLine : plottableLines) {
					final List<DataPoint> seriesData = selectedGraphData
							.getSeriesData(plottableLine);
					analysisMethod.performAnalysis(seriesIdx,
							getDataInRange(seriesData));
					equations.add(analysisMethod.getAnalysisResult(seriesIdx));
					++seriesIdx;
				}
			}
			currentAnalysisData = new AnalysisData(
					selectedAnalysisMethodInstance, selectedGraphData
							.getXAxis(), selectedGraphData.getYAxis(),
					selectedGraphData.getPlottableLines());

			graphModel.addAnalysisData(currentAnalysisData);
			modified = true;
		}

		if (modified) {
			eventHub.notifyEvent(EventHub.EventType.ANALYSIS);
		}
		fireChangeListeners();
	}

	/**
	 * Show a dialog containing the analysis methods configuration panel.
	 * 
	 * @param comp
	 *            the component that will be the parent of the dialog.
	 * @param location
	 *            where the dialog should be opened.
	 */
	public void showConfiguration(Component comp, Point location) {
		Window window = SwingUtilities.getWindowAncestor(comp);
		JDialog configDialog = new JDialog(window, "Configuration",
				JDialog.DEFAULT_MODALITY_TYPE);
		configDialog.setContentPane(selectedAnalysisMethodInstance
				.getConfigPanel());
		configDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		configDialog.pack();
		configDialog.setLocation(location);
		configDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				performAnalysis();
			}
		});
		configDialog.setVisible(true);
	}

	/**
	 * Show a dialog containing the equations given by the analysis performed by
	 * the selected analysis method.
	 * 
	 * @param comp
	 *            the component that will be the parent of the dialog.
	 * @param location
	 *            where the dialog should be opened.
	 */
	public void showEquations(Component comp, Point location) {
		Window parent = SwingUtilities.getWindowAncestor(comp);
		JDialog equationDialog = new JDialog(parent, "Equations",
				JDialog.DEFAULT_MODALITY_TYPE);
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea text = new JTextArea(
				StringUtils.joinStrings(equations, "\n"), 10, 30);
		text.setEditable(false);
		panel.add(text);
		equationDialog.setContentPane(panel);
		equationDialog.setLocation(location);
		equationDialog.pack();
		equationDialog.setVisible(true);
	}

	/**
	 * Set the analysis ranges to the min and max values of the graph data.
	 */
	private void setRanges() {
		Integer oldRangeFrom = rangeFrom;
		Integer oldRangeTo = rangeTo;
		rangeFrom = (int) getMinX();
		rangeTo = (int) getMaxX();
		if (oldRangeFrom == null || oldRangeTo == null) {
			fireChangeListeners();
		}
	}

	/**
	 * Find the min x value in the graph data.
	 * 
	 * @return the min x value.
	 */
	private double getMinX() {
		double minX = Double.MAX_VALUE;
		Collection<? extends PlottableLine> graphLines = selectedGraphData
				.getPlottableLines();
		if (graphLines.isEmpty()) {
			minX = getMinX(selectedGraphData.getSeriesData(null));
		} else {
			for (PlottableLine graphLine : graphLines) {
				minX = Math.min(minX, getMinX(selectedGraphData
						.getSeriesData(graphLine)));
			}
		}
		return minX;
	}

	/**
	 * Find the min x value in the given data.
	 * 
	 * @param seriesData
	 *            the data to search.
	 * @return the min x value.
	 */
	private double getMinX(List<DataPoint> seriesData) {
		double min = Double.MAX_VALUE;
		for (DataPoint point : seriesData) {
			min = Math.min(min, point.getXValue());
		}
		return min;
	}

	/**
	 * Find the max x value in the graph data.
	 * 
	 * @return the max x value.
	 */
	private double getMaxX() {
		double maxX = 0;
		Collection<? extends PlottableLine> graphLines = selectedGraphData
				.getPlottableLines();
		if (graphLines.isEmpty()) {
			maxX = getMaxX(selectedGraphData.getSeriesData(null));
		} else {
			for (PlottableLine graphLine : graphLines) {
				maxX = Math.max(maxX, getMaxX(selectedGraphData
						.getSeriesData(graphLine)));
			}
		}
		return maxX;
	}

	/**
	 * Find the max x value in the given data.
	 * 
	 * @param seriesData
	 *            the data to search.
	 * @return the max x value.
	 */
	private double getMaxX(List<DataPoint> seriesData) {
		double max = 0;
		for (DataPoint point : seriesData) {
			max = Math.max(max, point.getXValue());
		}
		return max;
	}

	@Override
	public void addChangeListener(ChangeListener changeListener) {
		eventSupport.addChangeListener(changeListener);
	}

	/**
	 * Notify all change listeners that the model has been updated.
	 */
	private void fireChangeListeners() {
		eventSupport.fireChangeEvent();
	}

	/**
	 * Get the currently set range from value.
	 * 
	 * @return the range from.
	 */
	public Integer getRangeFrom() {
		return rangeFrom;
	}

	/**
	 * Get the currently set range to value.
	 * 
	 * @return the range to.
	 */
	public Integer getRangeTo() {
		return rangeTo;
	}

	/**
	 * Set the range from to the given value.
	 * 
	 * @param rangeFrom
	 *            the value to set.
	 */
	public void setRangeFrom(int rangeFrom) {
		Integer oldRangeFrom = rangeFrom;
		this.rangeFrom = rangeFrom;
		if (oldRangeFrom == null || !oldRangeFrom.equals(rangeFrom)) {
			performAnalysis();
			fireChangeListeners();
		}
	}

	/**
	 * Set the range to to the given value.
	 * 
	 * @param rangeTo
	 *            the value to set.
	 */
	public void setRangeTo(int rangeTo) {
		Integer oldRangeTo = rangeTo;
		this.rangeTo = rangeTo;
		if (oldRangeTo == null || !oldRangeTo.equals(rangeTo)) {
			performAnalysis();
			fireChangeListeners();
		}
	}

	/**
	 * Add an analysis axis the axis list.
	 */
	public void addAxisAnalysis() {
		performAnalysisModel.addAxisAnalysis();
	}

	/**
	 * Return the {@code ComboBoxModel} behind the analysis type selection box.
	 * 
	 * @return the analysis type model.
	 */
	public ComboBoxModel<Class<AnalysisMethod>> getAnalysisTypeModel() {
		return analysisTypeModel;
	}

	/**
	 * Return the {@code ComboBoxModel} behind the axis selection box.
	 * 
	 * @return the analysis axis model.
	 */
	public ComboBoxModel<GraphData> getAxisModel() {
		return axisModel;
	}

	/**
	 * Returns whether this analysis axis is the current axis, i.e. whether it
	 * is the bottom axis in the list.
	 * 
	 * @return {@code true} if this axis is the current axis, {@code false}
	 *         otherwise.
	 */
	public boolean isCurrent() {
		return current;
	}

	/**
	 * Remove this axis from the axis list.
	 */
	public void removeAxisAnalysis() {
		performAnalysisModel.removeAxisAnalysis(this);
	}

	/**
	 * Set this axis as the current axis.
	 * 
	 * @param current
	 *            {@code true} if this axis should be the current axis, {@code
	 *            false} otherwise.
	 */
	void setCurrent(boolean current) {
		this.current = current;
	}

	/**
	 * Update the axis selection model.
	 */
	public void updateAxisModel() {
		axisModel.removeAllElements();
		axisModel.addAllElements(graphModel.getGraphData());
	}

	/**
	 * Returns whether any equations have been generated by the selected
	 * analysis type.
	 * 
	 * @return {@code true} if equations have been generated, {@code false}
	 *         otherwise.
	 */
	public boolean hasEquations() {
		return !equations.isEmpty();
	}

	/**
	 * Returns the currently selected analysis method.
	 * 
	 * @return the analysis method.
	 */
	AnalysisMethod getAnalysisMethod() {
		return selectedAnalysisMethodInstance;
	}

	/**
	 * Create a persistence object for this {@code PerformAnalysisAxisModel}.
	 * 
	 * @return the persistence object.
	 */
	public AnalysisMethodData asAnalysisMethodData() {
		if (selectedAnalysisMethod == null) {
			return null;
		}
		return new AnalysisMethodData(selectedAnalysisMethod.getName(),
				rangeFrom, rangeTo);
	}

	/**
	 * Set the axis model state from a loaded {@code Analysis}.
	 * 
	 * @param graphData
	 *            the graph data.
	 * @param anaData
	 *            the analysis method data.
	 * @param cls
	 *            the analysis method class.
	 */
	void updateFromAnalysis(GraphData graphData, AnalysisMethodData anaData,
			Class<AnalysisMethod> cls) {
		rangeFrom = anaData.getRangeFrom();
		rangeTo = anaData.getRangeTo();
		axisModel.setSelectedElement(graphData);
		selectedGraphData = graphData;
		analysisTypeModel.setSelectedElement(cls);
		selectedAnalysisMethod = cls;
	}

}
