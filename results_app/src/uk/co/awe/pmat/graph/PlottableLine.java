package uk.co.awe.pmat.graph;

import uk.co.awe.pmat.LineType;

/**
 * An interface for the lines which make up a {@link Plottable} object and used
 * by the {@link Plotter} to define who the data contained in the
 * {@link Plottable} will be displayed.
 * 
 * @author AWE Plc copyright 2013
 */
public interface PlottableLine {

	/**
	 * Get the line type of the line.
	 * 
	 * @return The line type
	 */
	LineType getLineType();

	/**
	 * Get the name of the line.
	 * 
	 * @return The name
	 */
	String getName();
}
