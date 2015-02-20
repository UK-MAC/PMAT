package uk.co.awe.pmat.analysis;

import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;

/**
 * An interface for analysis methods which can be used by the application to
 * analyse data lines.
 * 
 * @author AWE Plc copyright 2013
 */
public interface AnalysisMethod {

	/**
	 * An immutable static class which is used for transferring the data to and
	 * from the analysis methods. This is contained within the interface to keep
	 * the analysis methods independent from the way the application stores the
	 * data internally.
	 */
	public static class DataPoint {

		private final Double xValue;
		private final Double yValue;
		private final Double errValue;

		/**
		 * Construct a new immutable {@code DataPoint}.
		 * 
		 * @param xValue
		 *            The x value
		 * @param yValue
		 *            The y value
		 * @param errValue
		 *            The y error value
		 */
		public DataPoint(Double xValue, Double yValue, Double errValue) {
			this.xValue = xValue;
			this.yValue = yValue;
			this.errValue = errValue;
		}

		/**
		 * The error value of the point in the y axis.
		 * 
		 * @return The error value
		 */
		public Double getErrValue() {
			return errValue;
		}

		/**
		 * The value of the point along the x axis.
		 * 
		 * @return The x value
		 */
		public Double getXValue() {
			return xValue;
		}

		/**
		 * The value of the point along the y axis.
		 * 
		 * @return The y value
		 */
		public Double getYValue() {
			return yValue;
		}

		@Override
		public String toString() {
			return String.format("<%3.2f: %3.2f>", xValue, yValue);
		}
	}

	/**
	 * This method should return the display name of the analysis method. This
	 * will be the name that the user sees when they select what method to apply
	 * to the data.
	 * 
	 * @return A string containing the analysis method's name
	 */
	String analysisMethodName();

	/**
	 * This method should perform any required analysis with the data and store
	 * it against the {@code analysisID} for later recall. This allows multiple
	 * lines to be analysed and plotted independently.
	 * 
	 * @param analysisID
	 *            The id of the data set to store any performed analyses against
	 * @param dataPoints
	 *            The data on which to perform any analyses
	 * @see DataPoint
	 */
	void performAnalysis(int analysisID, List<DataPoint> dataPoints);

	/**
	 * This method should return a data set containing the results of any
	 * performed analyses for the given {@code analysisID}. This data could be
	 * given for any set of x values but should most likely return results for x
	 * values over a range containing the analysed data set.
	 * 
	 * @param analysisID
	 *            The id of the analysis to return
	 * @return The set of data points containing the analysis
	 * @see DataPoint
	 */
	List<DataPoint> returnAnalysis(int analysisID);

	/**
	 * This method should export a list of values which contains all the
	 * information needed to recreate this analysis. I.e. a linear regression
	 * analysis might export the gradient and intersection values, along with x
	 * range over which the analysis was performed.
	 * 
	 * @param analysisID
	 *            The id of the analysis to export
	 * @return The export analysis values
	 */
	List<Double> exportAnalysis(int analysisID);

	/**
	 * This method should import a list of values and use them to recreate the
	 * analysis. See exportAnalysis for an example of what these values might
	 * contain.
	 * 
	 * @param in
	 *            The values to import
	 * @return The ID given to the newly imported analysis
	 */
	int importAnalysis(List<Double> in);

	/**
	 * This method should return all the analysis IDs currently stored by this
	 * analysis method. I.e. This should be the collection of all IDs for which
	 * {@link #returnAnalysis} can return data for.
	 * 
	 * @return A collection of the IDs
	 */
	Collection<Integer> returnAnalysisIDs();

	/**
	 * This method should return whether of not the analysis method provides
	 * additional configuration options via a configuration panel.
	 * 
	 * @return {@code true} if a configuration panel is provided, otherwise
	 *         {@code false}
	 */
	boolean hasConfigPanel();

	/**
	 * If the {@code AnalysisMethod} provides a configuration panel this method
	 * should return said panel, otherwise an
	 * {@link UnsupportedOperationException} should be thrown.
	 * 
	 * @return The configuration panel
	 * @throws UnsupportedOperationException
	 *             if not panel is provided
	 */
	JPanel getConfigPanel() throws UnsupportedOperationException;

	/**
	 * Return an equation or similar which can be displayed with an analysis,
	 * i.e. it might be displayed on the graph next to the analysis line.
	 * 
	 * @param analysisID
	 *            The id of the analysis to summarise
	 * @return A summary of the analysis
	 */
	String getAnalysisResult(int analysisID);

}
