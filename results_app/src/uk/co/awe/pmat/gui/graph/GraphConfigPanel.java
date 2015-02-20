package uk.co.awe.pmat.gui.graph;

import uk.co.awe.pmat.gui.models.GraphConfigModel;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.graph.GraphModel;
import uk.co.awe.pmat.graph.PlotType;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.co.awe.pmat.gui.events.EventHub;

/**
 * A panel to display graph configuration settings such as log axes and axis
 * ranges.
 * 
 * @author AWE Plc copyright 2013
 */
public final class GraphConfigPanel extends JPanel implements ChangeListener {

	private final GraphConfigModel graphConfigModel;

	/**
	 * Create a new {@code GraphConfigPanel}.
	 * 
	 * @param graphModel
	 *            the graph model driving this panel.
	 */
	public GraphConfigPanel(GraphModel graphModel, EventHub eventHub) {
		initComponents();

		this.graphConfigModel = new GraphConfigModel(graphModel
				.getGraphConfig(), eventHub);
		graphConfigModel.addChangeListener((ChangeListener) this);

		x1MinField.addFocusListener(graphConfigModel
				.getMinFocusListener(AxisName.X1));
		x2MinField.addFocusListener(graphConfigModel
				.getMinFocusListener(AxisName.X2));
		y1MinField.addFocusListener(graphConfigModel
				.getMinFocusListener(AxisName.Y1));
		y2MinField.addFocusListener(graphConfigModel
				.getMinFocusListener(AxisName.Y2));

		x1MaxField.addFocusListener(graphConfigModel
				.getMaxFocusListener(AxisName.X1));
		x2MaxField.addFocusListener(graphConfigModel
				.getMaxFocusListener(AxisName.X2));
		y1MaxField.addFocusListener(graphConfigModel
				.getMaxFocusListener(AxisName.Y1));
		y2MaxField.addFocusListener(graphConfigModel
				.getMaxFocusListener(AxisName.Y2));

		x1LogButton.addActionListener(graphConfigModel
				.getLogActionListener(AxisName.X1));
		x2LogButton.addActionListener(graphConfigModel
				.getLogActionListener(AxisName.X2));
		y1LogButton.addActionListener(graphConfigModel
				.getLogActionListener(AxisName.Y1));
		y2LogButton.addActionListener(graphConfigModel
				.getLogActionListener(AxisName.Y2));

		graphConfigModel.setKeyVerticalListeners(topButton, middleButton,
				bottomButton);
		graphConfigModel.setKeyHorizontalListeners(leftButton, centerButton,
				rightButton);
		graphConfigModel.setKeyOrientationListeners(verticalButton,
				horizontalButton);
		graphConfigModel.setKeyPlacementListeners(insideButton, outsideButton);

		boxCheck.addActionListener(graphConfigModel.getBoxActionListener());
		showCheck.addActionListener(graphConfigModel.getShowActionListener());

		updateRangeFields();
		updateCheckBoxes();
		updateLogButtons();

		typeSelection.setModel(graphModel.getGraphTypeSelectioModel());
	}

	/**
	 * Update the range text fields to display the current axis values.
	 */
	private void updateRangeFields() {
		x1MinField.setText(graphConfigModel.getAxisMin(AxisName.X1));
		x2MinField.setText(graphConfigModel.getAxisMin(AxisName.X2));
		y1MinField.setText(graphConfigModel.getAxisMin(AxisName.Y1));
		y2MinField.setText(graphConfigModel.getAxisMin(AxisName.Y2));

		x1MaxField.setText(graphConfigModel.getAxisMax(AxisName.X1));
		x2MaxField.setText(graphConfigModel.getAxisMax(AxisName.X2));
		y1MaxField.setText(graphConfigModel.getAxisMax(AxisName.Y1));
		y2MaxField.setText(graphConfigModel.getAxisMax(AxisName.Y2));
	}

	private void updateCheckBoxes() {
		graphConfigModel.setKeyVerticalSelection(legendPosVertGroup, topButton
				.getModel(), middleButton.getModel(), bottomButton.getModel());
		graphConfigModel.setKeyHorizontalSelection(legendPosHorizGroup,
				leftButton.getModel(), centerButton.getModel(), rightButton
						.getModel());
		graphConfigModel.setKeyOrientationSelection(legendOrientGroup,
				verticalButton.getModel(), horizontalButton.getModel());
		graphConfigModel.setKeyPlacementSelection(legendPlacementGroup,
				insideButton.getModel(), outsideButton.getModel());
	}

	private void updateLogButtons() {
		x1LogButton.setSelected(graphConfigModel.getAxisLog(AxisName.X1));
		x2LogButton.setSelected(graphConfigModel.getAxisLog(AxisName.X2));
		y1LogButton.setSelected(graphConfigModel.getAxisLog(AxisName.Y1));
		y2LogButton.setSelected(graphConfigModel.getAxisLog(AxisName.Y2));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		updateRangeFields();
		updateCheckBoxes();
		updateLogButtons();
	}

	/**
	 * This method is called from within the constructor to initialise the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new javax.swing.BoxLayout(this,
				javax.swing.BoxLayout.PAGE_AXIS));

		typePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createTitledBorder("Graph Type"),
				javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		typePanel.setLayout(new javax.swing.BoxLayout(typePanel,
				javax.swing.BoxLayout.LINE_AXIS));
		typePanel.add(typeSelection);

		add(typePanel);

		logPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createTitledBorder("Log Axes"),
				javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		logPanel.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

		x1LogButton.setText("X1 Log");
		logPanel.add(x1LogButton);

		x2LogButton.setText("X2 Log");
		x2LogButton.setEnabled(false);
		logPanel.add(x2LogButton);

		y1LogButton.setText("Y1 Log");
		logPanel.add(y1LogButton);

		y2LogButton.setText("Y2 Log");
		y2LogButton.setEnabled(false);
		logPanel.add(y2LogButton);

		add(logPanel);

		rangePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createTitledBorder("Ranges"),
				javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		rangePanel.setLayout(new java.awt.GridBagLayout());

		minLabel.setText("Minimum");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		rangePanel.add(minLabel, gridBagConstraints);

		maxLabel.setText("Maximum");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		rangePanel.add(maxLabel, gridBagConstraints);

		x1Label.setText("X1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(x1Label, gridBagConstraints);

		x2Label.setText("X2");
		x2Label.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(x2Label, gridBagConstraints);

		y1Label.setText("Y1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(y1Label, gridBagConstraints);

		y2Label.setText("Y2");
		y2Label.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		rangePanel.add(y2Label, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(x1MinField, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(x1MaxField, gridBagConstraints);

		x2MinField.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(x2MinField, gridBagConstraints);

		x2MaxField.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(x2MaxField, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(y1MinField, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		rangePanel.add(y1MaxField, gridBagConstraints);

		y2MinField.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		rangePanel.add(y2MinField, gridBagConstraints);

		y2MaxField.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		rangePanel.add(y2MaxField, gridBagConstraints);

		add(rangePanel);

		legendPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createTitledBorder("Legend"),
				javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		legendPanel.setLayout(new java.awt.GridBagLayout());

		legendPosVertGroup.add(topButton);
		topButton.setSelected(true);
		topButton.setText("Top");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(topButton, gridBagConstraints);

		legendPosVertGroup.add(bottomButton);
		bottomButton.setText("Bottom");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(bottomButton, gridBagConstraints);

		legendPosVertGroup.add(middleButton);
		middleButton.setText("Middle");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
		legendPanel.add(middleButton, gridBagConstraints);

		legendPosHorizGroup.add(leftButton);
		leftButton.setText("Left");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(leftButton, gridBagConstraints);

		legendPosHorizGroup.add(rightButton);
		rightButton.setSelected(true);
		rightButton.setText("Right");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(rightButton, gridBagConstraints);

		legendPosHorizGroup.add(centerButton);
		centerButton.setText("Centre");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
		legendPanel.add(centerButton, gridBagConstraints);

		legendOrientGroup.add(verticalButton);
		verticalButton.setSelected(true);
		verticalButton.setText("Vertical");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(verticalButton, gridBagConstraints);

		boxCheck.setText("Box");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		legendPanel.add(boxCheck, gridBagConstraints);

		showCheck.setSelected(true);
		showCheck.setText("Show");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		legendPanel.add(showCheck, gridBagConstraints);

		legendOrientGroup.add(horizontalButton);
		horizontalButton.setText("Horizontal");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(horizontalButton, gridBagConstraints);

		legendPlacementGroup.add(insideButton);
		insideButton.setSelected(true);
		insideButton.setText("Inside");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(insideButton, gridBagConstraints);

		legendPlacementGroup.add(outsideButton);
		outsideButton.setText("Outside");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
		legendPanel.add(outsideButton, gridBagConstraints);

		add(legendPanel);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JRadioButton bottomButton = new javax.swing.JRadioButton();
	private final javax.swing.JCheckBox boxCheck = new javax.swing.JCheckBox();
	private final javax.swing.JRadioButton centerButton = new javax.swing.JRadioButton();
	private final javax.swing.JRadioButton horizontalButton = new javax.swing.JRadioButton();
	private final javax.swing.JRadioButton insideButton = new javax.swing.JRadioButton();
	private final javax.swing.JRadioButton leftButton = new javax.swing.JRadioButton();
	private final javax.swing.ButtonGroup legendOrientGroup = new javax.swing.ButtonGroup();
	private final javax.swing.JPanel legendPanel = new javax.swing.JPanel();
	private final javax.swing.ButtonGroup legendPlacementGroup = new javax.swing.ButtonGroup();
	private final javax.swing.ButtonGroup legendPosHorizGroup = new javax.swing.ButtonGroup();
	private final javax.swing.ButtonGroup legendPosVertGroup = new javax.swing.ButtonGroup();
	private final javax.swing.JPanel logPanel = new javax.swing.JPanel();
	private final javax.swing.JLabel maxLabel = new javax.swing.JLabel();
	private final javax.swing.JRadioButton middleButton = new javax.swing.JRadioButton();
	private final javax.swing.JLabel minLabel = new javax.swing.JLabel();
	private final javax.swing.JRadioButton outsideButton = new javax.swing.JRadioButton();
	private final javax.swing.JPanel rangePanel = new javax.swing.JPanel();
	private final javax.swing.JRadioButton rightButton = new javax.swing.JRadioButton();
	private final javax.swing.JCheckBox showCheck = new javax.swing.JCheckBox();
	private final javax.swing.JRadioButton topButton = new javax.swing.JRadioButton();
	private final javax.swing.JPanel typePanel = new javax.swing.JPanel();
	private final javax.swing.JComboBox<PlotType> typeSelection = new javax.swing.JComboBox<PlotType>();
	private final javax.swing.JRadioButton verticalButton = new javax.swing.JRadioButton();
	private final javax.swing.JLabel x1Label = new javax.swing.JLabel();
	private final javax.swing.JCheckBox x1LogButton = new javax.swing.JCheckBox();
	private final javax.swing.JTextField x1MaxField = new javax.swing.JTextField();
	private final javax.swing.JTextField x1MinField = new javax.swing.JTextField();
	private final javax.swing.JLabel x2Label = new javax.swing.JLabel();
	private final javax.swing.JCheckBox x2LogButton = new javax.swing.JCheckBox();
	private final javax.swing.JTextField x2MaxField = new javax.swing.JTextField();
	private final javax.swing.JTextField x2MinField = new javax.swing.JTextField();
	private final javax.swing.JLabel y1Label = new javax.swing.JLabel();
	private final javax.swing.JCheckBox y1LogButton = new javax.swing.JCheckBox();
	private final javax.swing.JTextField y1MaxField = new javax.swing.JTextField();
	private final javax.swing.JTextField y1MinField = new javax.swing.JTextField();
	private final javax.swing.JLabel y2Label = new javax.swing.JLabel();
	private final javax.swing.JCheckBox y2LogButton = new javax.swing.JCheckBox();
	private final javax.swing.JTextField y2MaxField = new javax.swing.JTextField();
	private final javax.swing.JTextField y2MinField = new javax.swing.JTextField();
	// End of variables declaration//GEN-END:variables

}
