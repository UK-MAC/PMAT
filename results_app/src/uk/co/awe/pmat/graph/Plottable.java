package uk.co.awe.pmat.graph;

import java.util.Collection;
import uk.co.awe.pmat.db.axis.Axis;

/**
 * An interface for objects which can be plotted (ie. via GnuPlot). Such an
 * object needs to return both data and configuration information such as line
 * type and axes names.
 * 
 * @author AWE Plc copyright 2013
 */
public interface Plottable {

	/**
	 * Return the graph data in tabular form with the data given in rows by
	 * columns where the first column is the x axis and the remaining columns
	 * are the y axis series.
	 * 
	 * @return The graph data in tabular form
	 */
	Object[][] getTableData();

	/**
	 * Return the name of the X axis.
	 * 
	 * @return The X axis name
	 */
	Axis getXAxis();

	/**
	 * Return the name of Y axis.
	 * 
	 * @return The Y axis name
	 */
	Axis getYAxis();

	/**
	 * Return a list of the lines to use to display the data. Each line contains
	 * a line type and a name.
	 * 
	 * @return A list of PlottableLines
	 */
	Collection<? extends PlottableLine> getPlottableLines();

}
