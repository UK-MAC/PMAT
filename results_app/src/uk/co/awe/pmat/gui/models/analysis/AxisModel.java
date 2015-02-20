package uk.co.awe.pmat.gui.models.analysis;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.graph.GraphData;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.gui.utils.ThreadedAction;

/**
 * The model behind each axis line in the {@code AxisListPanel}. This model
 * drives the combo boxes and add/remove button of the {@code AxisPanel}.
 * 
 * @author AWE Plc copyright 2013
 */
public final class AxisModel {

	private static final Logger LOG = LoggerFactory.getLogger(AxisModel.class);
	private static final String Y_AXIS_HEADER = "Select Axis...";
	private static final String X_AXIS_HEADER = "Select Axis...";
	private static final AxisValueType DEFAULT_RESULT_TYPE = AxisValueType.VALUE;
	private static final Rank DEFUALT_RANK = Rank.ANY_RANK;

	private final AxisListModel axisListModel;
	private final GraphData graphData;
	private final DefaultComboBoxModel<Axis> xAxisModel;
	private final DefaultComboBoxModel<Axis> yAxisModel;
	private final DefaultComboBoxModel<AxisValueType> yAxisTypeModel;
	private final DefaultComboBoxModel<Rank> rankModel;

	/**
	 * Create a new {@code AxisModel} with a reference to the its containing
	 * {@code AxisListModel} and the {@code GraphData} object to which it is
	 * associated in the graph.
	 * 
	 * @param axisListModel
	 *            the list model which contains this model.
	 * @param graphData
	 *            the graph data associated with this axis.
	 * @param taskListener
	 *            a listener to be informed of any background tasks.
	 */
	AxisModel(final AxisListModel axisListModel,
            final GraphData graphData, final PropertyChangeListener taskListener) {
        this.axisListModel = axisListModel;
        this.graphData = graphData;

        xAxisModel = new DefaultComboBoxModel<>(X_AXIS_HEADER);
        yAxisModel = new DefaultComboBoxModel<>(Y_AXIS_HEADER);
        yAxisTypeModel = new DefaultComboBoxModel<>();
        rankModel = new DefaultComboBoxModel<>();

        /*
         * When we are loading this from the database we will set the models to
         * some values, then lock them out. Other wise the get... calls will
         * return null and do nothing.
         */
        xAxisModel.setSelectedElement(graphData.getXAxis());
        yAxisModel.setSelectedElement(graphData.getYAxis());
        yAxisTypeModel.setSelectedElement(graphData.getYAxisType());
        rankModel.setSelectedElement(graphData.getRank());

        /**
         * Now set the selection actions.
         */
        xAxisModel.setSelectionAction(new ThreadedAction("xAxisSelection", taskListener) {
            @Override
            public void actionPerformedInBackground() throws Exception {
                graphData.setXAxis(xAxisModel.getSelectedElement());
            }

            @Override
            public void done() {
                if (xAxisModel.getSelectedElement() != null) {
                    axisListModel.axisStateChanged();
                }
            }
        });
        yAxisModel.setSelectionAction(new ThreadedAction("yAxisSelection", taskListener) {
            @Override
            public void actionPerformedInBackground() throws Exception {
                graphData.setYAxis(yAxisModel.getSelectedElement());
            }

            @Override
            public void done() {
                if (yAxisModel.getSelectedElement() != null) {
                    axisListModel.axisStateChanged();
                }
            }
        });
        yAxisTypeModel.setSelectionAction(new ThreadedAction("yAxisTypeSelection", taskListener) {
            @Override
            public void actionPerformedInBackground() throws Exception {
                graphData.setYAxisType(yAxisTypeModel.getSelectedElement());
            }

            @Override
            public void done() {
                if (yAxisTypeModel.getSelectedElement() != null) {
                    axisListModel.axisStateChanged();
                }
            }
        });
        rankModel.setSelectionAction(new ThreadedAction("rankSelection", taskListener) {
            @Override
            public void actionPerformedInBackground() throws Exception {
                graphData.setRank(rankModel.getSelectedElement());
            }

            @Override
            public void done() {
                if (rankModel.getSelectedElement() != null) {
                    axisListModel.axisStateChanged();
                }
            }
        });

        updateModels();
    }

	/**
	 * Add an axis the axis list.
	 */
	public void addPanel() {
		axisListModel.addAxisModel();
	}

	/**
	 * Remove this axis from the axis list.
	 */
	public void removePanel() {
		axisListModel.removeAxisModel(this);
	}

	/**
	 * Return the model driving the X Axis combo box.
	 * 
	 * @return the combo box model.
	 */
	public ComboBoxModel<Axis> getXAxisModel() {
		return xAxisModel;
	}

	/**
	 * Return the model driving the Y Axis combo box.
	 * 
	 * @return the combo box model.
	 */
	public ComboBoxModel<Axis> getyAxisModel() {
		return yAxisModel;
	}

	/**
	 * Return the model driving the Y Axis Type combo box.
	 * 
	 * @return the combo box model.
	 */
	public ComboBoxModel<AxisValueType> getYAxisTypeModel() {
		return yAxisTypeModel;
	}

	/**
	 * Return the model driving the {@code Rank} combo box.
	 * 
	 * @return the combo box model.
	 */
	public ComboBoxModel<Rank> getRankModel() {
		return rankModel;
	}

	/**
	 * Update all the combo box models. This is usually called when the state of
	 * the analysis model has changed so that the values stored in the combo box
	 * models are no longer up to date.
	 */
	void updateModels() {
		final Axis selectedxAxis = yAxisModel.getSelectedElement();
		final Axis selectedyAxis = xAxisModel.getSelectedElement();
		final AxisValueType selectedType = yAxisTypeModel.getSelectedElement() == null ? DEFAULT_RESULT_TYPE
				: yAxisTypeModel.getSelectedElement();
		final Rank selectedRank = rankModel.getSelectedElement() == null ? DEFUALT_RANK
				: rankModel.getSelectedElement();

		xAxisModel.removeAllElements();
		yAxisModel.removeAllElements();
		yAxisTypeModel.removeAllElements();
		rankModel.removeAllElements();

		final List<Axis> xAxisValues = axisListModel.getxAxisValues();
		final List<Axis> yAxisValues = axisListModel.getyAxisValues();
		final List<AxisValueType> yAxisTypes = Arrays.asList(AxisValueType
				.values());
		final List<Rank> ranks = axisListModel.getRanks();

		Collections.sort(xAxisValues);
		Collections.sort(yAxisValues);
		Collections.sort(yAxisTypes);
		Collections.sort(ranks);

		xAxisModel.addAllElements(xAxisValues);
		yAxisModel.addAllElements(yAxisValues);
		yAxisTypeModel.addAllElements(yAxisTypes);
		rankModel.addAllElements(ranks);

		xAxisModel.setSelectedElement(selectedyAxis);
		yAxisModel.setSelectedElement(selectedxAxis);
		yAxisTypeModel.setSelectedElement(selectedType);
		rankModel.setSelectedElement(selectedRank);
	}

	/**
	 * Display a series dialog allowing setting/editing of the series for this
	 * axis.
	 * 
	 * @param axisNumber
	 *            The number of this axis in the {@code AxisListPanel}
	 */
	public void showSeriesDialog(String axisNumber) {
		axisListModel.showSeriesDialog(axisNumber, graphData);
	}

}
