package uk.co.awe.pmat.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeListener;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.Pair;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class GraphConfig {

    private final Map<String, String> graphLabels = new HashMap<>();
    private final Map<String, String> oldGraphLabels = new HashMap<>();
    private final Map<AxisName, Double> axesMinimum = new EnumMap<>(AxisName.class);
    private final Map<AxisName, Double> axesMaximum = new EnumMap<>(AxisName.class);
    private final Map<AxisName, Boolean> axesLog = new EnumMap<>(AxisName.class);
    private final ChangeEventSupport changeEventSupport = new ChangeEventSupport(this);
    
    private KeyShow keyShow;
    private KeyBox keyBox;
    private KeyHorizontalPosition keyHorizontalPosition;
    private KeyVerticalPosition keyVerticalPosition;
    private KeyPlacement keyPlacement;
    private KeyOrientation keyOrientation;
    
    public GraphConfig() {
        // Setting default values.
        for (AxisName axis : AxisName.values()) {
            axesMinimum.put(axis, null);
            axesMaximum.put(axis, null);
            axesLog.put(axis, false);
        }
        keyShow = KeyShow.on;
        keyBox = KeyBox.nobox;
        keyHorizontalPosition = KeyHorizontalPosition.right;
        keyVerticalPosition = KeyVerticalPosition.top;
        keyPlacement = KeyPlacement.inside;
        keyOrientation = KeyOrientation.vertical;
    }

    public String getGraphLabel(String displayName) {
        return graphLabels.get(displayName);
    }

    public boolean getAxisLog(AxisName axisName) {
        return axesLog.get(axisName);
    }

    public Double getAxisMinimum(AxisName axisName) {
        return axesMinimum.get(axisName);
    }

    public Double getAxisMaximum(AxisName axisName) {
        return axesMaximum.get(axisName);
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Enum<T> getKeyOption (Class<T> cls) {
        if (KeyBox.class.equals(cls)) {
            return (Enum<T>) keyBox;
        } else if (KeyShow.class.equals(cls)) {
            return (Enum<T>) keyShow;
        } else if (KeyHorizontalPosition.class.equals(cls)) {
            return (Enum<T>) keyHorizontalPosition;
        } else if (KeyVerticalPosition.class.equals(cls)) {
            return (Enum<T>) keyVerticalPosition;
        } else if (KeyPlacement.class.equals(cls)) {
            return (Enum<T>) keyPlacement;
        } else if (KeyOrientation.class.equals(cls)) {
            return (Enum<T>) keyOrientation;
        } else {
            throw new IllegalArgumentException("Unknown key option " + cls);
        }
    }

    public List<Pair<String, String>> getLabelRenames() {
        List<Pair<String, String>> renames = new ArrayList<>();
        for (Map.Entry<String, String> entry : graphLabels.entrySet()) {
            if (!entry.getKey().equals(entry.getValue())) {
                renames.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }
        return renames;
    }

    public void addLabelRenames(Collection<Pair<String, String>> labelRenames) {
        for (Pair<String, String> labelRename : labelRenames) {
            graphLabels.put(labelRename.getFirst(), labelRename.getSecond());
        }
    }

    public Map<String, String> getGraphLabels() {
        return graphLabels;
    }

    public void setGraphLabel(String label, String rename) {
        graphLabels.put(label, rename);
    }

    public void setAxisMinimum(AxisName axisName, Double min) {
        axesMinimum.put(axisName, min);
    }

    public void setAxisMaximum(AxisName axisName, Double max) {
        axesMaximum.put(axisName, max);
    }

    public void setAxisLog(AxisName axisName, boolean log) {
        axesLog.put(axisName, log);
    }

    /**
     * Set the graph configuration.
     *
     * @param value the graph configuration setting.
     */
    public void setKeyOption(Enum<?> value) {
        if (value instanceof KeyBox) {
            keyBox = (KeyBox) value;
        } else if (value instanceof KeyShow) {
            keyShow = (KeyShow) value;
        } else if (value instanceof KeyHorizontalPosition) {
            keyHorizontalPosition = (KeyHorizontalPosition) value;
        } else if (value instanceof KeyVerticalPosition) {
            keyVerticalPosition = (KeyVerticalPosition) value;
        } else if (value instanceof KeyPlacement) {
            keyPlacement = (KeyPlacement) value;
        } else if (value instanceof KeyOrientation) {
            keyOrientation = (KeyOrientation) value;
        } else {
            throw new IllegalArgumentException("Unknown key option "
                    + value.getClass().getSimpleName());
        }
    }
    
    /**
     * Update the collection of graph labels that are being displayed on the
     * plot.
     * 
     * @param labels 
     */
    public void updateGraphLabels(Collection<String> labels) {
        oldGraphLabels.putAll(graphLabels);
        graphLabels.clear();

        for (String label : labels) {
            if (oldGraphLabels.containsKey(label)) {
                graphLabels.put(label, oldGraphLabels.get(label));
            } else {
                graphLabels.put(label, label);
            }
        }
        
        changeEventSupport.fireChangeEvent();
    }

    public void addChangeListener(ChangeListener listener) {
        changeEventSupport.addChangeListener(listener);
    }
    
    /**
     * An {@code Enum} representation of the style of the box around the plot
     * key.
     */
    public enum KeyBox {
        /** Display the plot key with a box. */  box,
        /** Display the plot key with no box. */ nobox
    }

    /**
     * An {@code Enum} representation of whether the plot key should be
     * displayed.
     */
    public enum KeyShow {
        /** Display the plot key. */       on,
        /** Do not display the plot key.*/ off
    }

    /**
     * An {@code Enum} representation of the horizontal position to display the
     * plot key.
     */
    public enum KeyHorizontalPosition {
        /** Display the plot key on the left of the plot. */   left,
        /** Display the plot key on the right of the plot. */  right,
        /** Display the plot key in the centre of the plot. */ center
    }

    /**
     * An {@code Enum} representation of the vertical position to display the
     * plot key.
     */
    public enum KeyVerticalPosition {
        /** Display the plot key at the top of the plot. */    top,
        /** Display the plot key at the bottom of the plot. */ bottom,
        /** Display the plot key in the centre of the plot. */ center
    }

    /**
     * An {@code Enum} representation of the placement of the plot key in
     * relation to the plot grid.
     */
    public enum KeyPlacement {
        /** Display the plot key inside of the plot grid. */  inside,
        /** Display the plot key outside of the plot grid. */ outside
    }

    /**
     * An {@code Enum} representation of the orientation of the plot key.
     */
    public enum KeyOrientation {
        /** Display the plot key elements in a vertical orientation */
        vertical,
        /** Display the plot key elements in a horizontal orientation */
        horizontal
    }
    
}
