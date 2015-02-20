package uk.co.awe.pmat.gui.models;

import uk.co.awe.pmat.gui.events.EventHub;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.graph.GraphConfig;
import uk.co.awe.pmat.gui.events.EventHub.EventType;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import static uk.co.awe.pmat.graph.GraphConfig.*;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public class GraphConfigModel implements GuiModel {

	private static final Color TEXT_FIELD_DEFAULT_BG = new JTextField()
			.getBackground();

	private final EventHub eventHub;
	private final GraphConfig graphConfig;
	private final ChangeEventSupport changeEventSupport = new ChangeEventSupport(
			this);

	public GraphConfigModel(GraphConfig graphConfig, EventHub eventHub) {
		this.eventHub = eventHub;
		this.graphConfig = graphConfig;
	}

	public FocusListener getMinFocusListener(final AxisName axisName) {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				final JTextField textField = (JTextField) e.getSource();
				final Double min = getNumberFromTextField(textField);
				graphConfig.setAxisMinimum(axisName, min);
				eventHub.notifyEvent(EventType.GRAPH);
			}
		};
	}

	public FocusListener getMaxFocusListener(final AxisName axisName) {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				final JTextField textField = (JTextField) e.getSource();
				final Double max = getNumberFromTextField(textField);
				graphConfig.setAxisMaximum(axisName, max);
				eventHub.notifyEvent(EventType.GRAPH);
			}
		};
	}

	public ActionListener getLogActionListener(final AxisName axisName) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JCheckBox button = (JCheckBox) e.getSource();
				graphConfig.setAxisLog(axisName, button.isSelected());
				eventHub.notifyEvent(EventType.GRAPH);
			}
		};
	}

	public String getAxisMin(AxisName axisName) {
		final Double min = graphConfig.getAxisMinimum(axisName);
		return min != null ? min.toString() : null;
	}

	public String getAxisMax(AxisName axisName) {
		final Double max = graphConfig.getAxisMaximum(axisName);
		return max != null ? max.toString() : null;
	}

	/**
	 * Extract and parse the text value from the given text field and return the
	 * number value if successful, otherwise return {@code null} and set the
	 * text fields background colour to red.
	 * 
	 * @param field
	 *            the text field to extract the number from.
	 * @return the number or {@code null} if the parsing failed.
	 */
	private Double getNumberFromTextField(JTextField field) {
		Double result = null;
		field.setBackground(TEXT_FIELD_DEFAULT_BG);
		try {
			String text = field.getText();
			if (!text.isEmpty()) {
				result = Double.parseDouble(field.getText());
			}
		} catch (NumberFormatException ex) {
			field.setBackground(Color.red);
		}
		return result;
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		changeEventSupport.addChangeListener(listener);
		graphConfig.addChangeListener(listener);
	}

	public void setKeyVerticalSelection(ButtonGroup legendPosVertGroup,
			ButtonModel topModel, ButtonModel middleModel,
			ButtonModel bottomeModel) {

		KeyVerticalPosition opt = (KeyVerticalPosition) graphConfig
				.getKeyOption(KeyVerticalPosition.class);

		switch (opt) {
		case top:
			legendPosVertGroup.setSelected(topModel, true);
			break;
		case center:
			legendPosVertGroup.setSelected(middleModel, true);
			break;
		case bottom:
			legendPosVertGroup.setSelected(bottomeModel, true);
			break;
		default:
			throw new IllegalStateException("Uknown KeyVerticalPosition " + opt);
		}
	}

	public void setKeyHorizontalSelection(ButtonGroup legendPosHorizGroup,
			ButtonModel leftModel, ButtonModel centerModel,
			ButtonModel rightModel) {

		KeyHorizontalPosition opt = (KeyHorizontalPosition) graphConfig
				.getKeyOption(KeyHorizontalPosition.class);

		switch (opt) {
		case left:
			legendPosHorizGroup.setSelected(leftModel, true);
			break;
		case center:
			legendPosHorizGroup.setSelected(centerModel, true);
			break;
		case right:
			legendPosHorizGroup.setSelected(rightModel, true);
			break;
		default:
			throw new IllegalStateException("Uknown KeyHorizontalPosition "
					+ opt);
		}
	}

	public void setKeyOrientationSelection(ButtonGroup legendOrientGroup,
			ButtonModel vertModel, ButtonModel horizModel) {

		KeyOrientation opt = (KeyOrientation) graphConfig
				.getKeyOption(KeyOrientation.class);

		switch (opt) {
		case vertical:
			legendOrientGroup.setSelected(vertModel, true);
			break;
		case horizontal:
			legendOrientGroup.setSelected(horizModel, true);
			break;
		default:
			throw new IllegalStateException("Uknown KeyOrientation " + opt);
		}
	}

	public void setKeyPlacementSelection(ButtonGroup legendPlacementGroup,
			ButtonModel insideModel, ButtonModel outsideModel) {

		KeyPlacement opt = (KeyPlacement) graphConfig
				.getKeyOption(KeyPlacement.class);

		switch (opt) {
		case inside:
			legendPlacementGroup.setSelected(insideModel, true);
			break;
		case outside:
			legendPlacementGroup.setSelected(outsideModel, true);
			break;
		default:
			throw new IllegalStateException("Uknown KeyPlacement " + opt);
		}
	}

	public boolean getAxisLog(AxisName axisName) {
		return graphConfig.getAxisLog(axisName);
	}

	public void setKeyVerticalListeners(JRadioButton topButton,
			JRadioButton middleButton, JRadioButton bottomButton) {

		class VertActionListener implements ActionListener {
			final KeyVerticalPosition pos;

			VertActionListener(KeyVerticalPosition pos) {
				this.pos = pos;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				graphConfig.setKeyOption(pos);
				eventHub.notifyEvent(EventType.GRAPH);
			}
		}

		topButton.addActionListener(new VertActionListener(
				KeyVerticalPosition.top));
		middleButton.addActionListener(new VertActionListener(
				KeyVerticalPosition.center));
		bottomButton.addActionListener(new VertActionListener(
				KeyVerticalPosition.bottom));
	}

	public void setKeyHorizontalListeners(JRadioButton leftButton,
			JRadioButton centerButton, JRadioButton rightButton) {

		class HorizActionListener implements ActionListener {
			final KeyHorizontalPosition pos;

			HorizActionListener(KeyHorizontalPosition pos) {
				this.pos = pos;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				graphConfig.setKeyOption(pos);
				eventHub.notifyEvent(EventType.GRAPH);
			}
		}

		leftButton.addActionListener(new HorizActionListener(
				KeyHorizontalPosition.left));
		centerButton.addActionListener(new HorizActionListener(
				KeyHorizontalPosition.center));
		rightButton.addActionListener(new HorizActionListener(
				KeyHorizontalPosition.right));
	}

	public void setKeyOrientationListeners(JRadioButton verticalButton,
			JRadioButton horizontalButton) {

		class VertActionListener implements ActionListener {
			final KeyOrientation pos;

			VertActionListener(KeyOrientation pos) {
				this.pos = pos;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				graphConfig.setKeyOption(pos);
				eventHub.notifyEvent(EventType.GRAPH);
			}
		}

		verticalButton.addActionListener(new VertActionListener(
				KeyOrientation.vertical));
		horizontalButton.addActionListener(new VertActionListener(
				KeyOrientation.horizontal));
	}

	public void setKeyPlacementListeners(JRadioButton insideButton,
			JRadioButton outsideButton) {

		class VertActionListener implements ActionListener {
			final KeyPlacement pos;

			VertActionListener(KeyPlacement pos) {
				this.pos = pos;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				graphConfig.setKeyOption(pos);
				eventHub.notifyEvent(EventType.GRAPH);
			}
		}

		insideButton.addActionListener(new VertActionListener(
				KeyPlacement.inside));
		outsideButton.addActionListener(new VertActionListener(
				KeyPlacement.outside));
	}

	public ActionListener getBoxActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JCheckBox box = (JCheckBox) e.getSource();
				graphConfig.setKeyOption(box.isSelected() ? KeyBox.box
						: KeyBox.nobox);
			}
		};
	}

	public ActionListener getShowActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JCheckBox show = (JCheckBox) e.getSource();
				graphConfig.setKeyOption(show.isSelected() ? KeyShow.on
						: KeyShow.off);
			}
		};
	}

}
