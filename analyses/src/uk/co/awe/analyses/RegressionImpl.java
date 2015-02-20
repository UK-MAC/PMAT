package uk.co.awe.analyses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.warwick.dcs.hpsg.warpp.math.Line;
import uk.ac.warwick.dcs.hpsg.warpp.math.Point2D;
import static uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;

/**
 * A worker class which is parameterised by the line type used by each of the
 * regression methods and factors out their common functionality.
 * 
 * @param <T> The type of line used in the analysis, ie. a simple line, an
 * exponential line, etc.
 * @author AWE Plc
 */
class RegressionImpl <T extends Line> {

    private static final int DEFAULT_NUM_POINTS_USED_IN_PLOT = 100;

    private final Map<Integer, T> analyses;
    private final Map<Integer, Double> analysisMinX;
    private final Map<Integer, Double> analysisMaxX;
    private int numPointsUsedInPlots = DEFAULT_NUM_POINTS_USED_IN_PLOT;
    
    /**
     * Create a RegressionImpl object.
     */
    RegressionImpl() {
        this.analysisMinX = new HashMap<Integer, Double>();
        this.analysisMaxX = new HashMap<Integer, Double>();
        this.analyses = new HashMap<Integer, T>();
    }
    
    /**
     * Return the collection of analysis IDs which are used to identify the
     * analyses of each dataset.
     * 
     * @return A collection of analysis IDs
     */
    Collection<Integer> returnAnalysisIDs() {
        return this.analyses.keySet();
    }
    
    /**
     * Return the data associated with the give analysis ID.
     * 
     * @param analysisID The ID of the analysis
     * @return The analysis data
     */
    List<DataPoint> returnAnalysis(int analysisID) {
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        for (int dataIdx = 0; dataIdx < this.numPointsUsedInPlots; ++dataIdx) {
            double xValue = distibuteValueLogrithmically(analysisID, dataIdx);
            double yValue = this.analyses.get(analysisID).getFittedValue(xValue);
            dataPoints.add(new DataPoint(xValue, yValue, 0.0));
        }
        return dataPoints;
    }

    /**
     * Return the pointIndex'th point in a logarithmic distribution of points
     * between the min and max x values for the selected analysis.
     *
     * @param analysisID The ID of the analysis
     * @param pointIndex The index of the point in the distribution
     * @return The logarithmically distributed x value
     */
    private double distibuteValueLogrithmically(int analysisID, int pointIndex) {
        double minX = this.analysisMinX.get(analysisID);
        double maxX = this.analysisMaxX.get(analysisID);
        if (pointIndex == 0) {
            return minX;
        }
        if (pointIndex == this.numPointsUsedInPlots - 1) {
            return maxX;
        }
        double newMin = minX;
        if (minX == 0) {
            newMin = Math.min(1, (maxX - minX) / this.numPointsUsedInPlots);
        }
        return Math.exp(
                (Math.log(maxX) - Math.log(newMin))
                / (this.numPointsUsedInPlots - 1)
                * pointIndex
                + Math.log(newMin));
    }
    
    /**
     * Modify the number of points used in the analysis to produce smoother
     * analyses but at the cost of more time and larger data sets.
     * 
     * @param num The number of points of data to produce in the analysis
     */
    void setNumPointsUsedInPlots(int num) {
        this.numPointsUsedInPlots = num;
    }
    
    /**
     * Transform the list of DataPoints into a list of Point2D and calculate the
     * minimum and maximum points which are stored against the analsys ID.
     * 
     * @param analysisID The ID of the analysis to store the minimum and maximum
     * data values against
     * @param dataPoints The list of DataPoints to transform
     * @return The transformed data
     */
    List<Point2D> processDataPoints(int analysisID, List<DataPoint> dataPoints) {
        
        List<Point2D> data = new ArrayList<Point2D>();
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for (DataPoint point : dataPoints) {
            if (point.getXValue() != null && point.getYValue() != null) {
                minX = Math.min(minX, point.getXValue());
                maxX = Math.max(maxX, point.getXValue());
                data.add(new Point2D(point.getXValue(), point.getYValue()));
            }
        }
        
        this.analysisMinX.put(analysisID, minX);
        this.analysisMaxX.put(analysisID, maxX);
        
        return data;
    }
    
    /**
     * Store the results of the analysis against the given analysis ID.
     * 
     * @param analysisID The ID of the analysis
     * @param line The analysis result in the form of a subclass of Line
     */
    void setAnalysis(int analysisID, T line) {
        this.analyses.put(analysisID, line);
    }

    /**
     * Return the maximum and minimum data values stored against the given
     * analysis ID.
     * 
     * @param analysisID The ID of the analysis
     * @return A list containing the minimum and maximum data points
     */
    List<Double> exportMinMax(int analysisID) {
        return Arrays.asList(new Double[] {
            this.analysisMinX.get(analysisID),
            this.analysisMaxX.get(analysisID)
        });
    }

    /**
     * Return the stored analysis in the form of a subclass of Line as specified
     * by the template parameter of this object.
     * 
     * @param analysisID The ID of the analysis
     * @return The stored analysis in the form of a subclass of Line
     */
    T getAnalysis(int analysisID) {
        return this.analyses.get(analysisID);
    }

    /**
     * Return the next available analysis ID.
     * 
     * @return An analysis ID which currently has no analysis stored against it
     */
    int getNextID() {
        int analysisID = 0;
        while (this.analyses.containsKey(analysisID)) {
            ++analysisID;
        }
        return analysisID;
    }

    /**
     * Extract the minimum and maximum values from a list and store them against
     * the given analysis ID.
     * 
     * @param analysisID The ID of the analysis
     * @param in The list of numbers from which to obtain the minimum and
     * maximum values
     */
    void importMinMax(int analysisID, List<Double> in) {
        assert in.size() >= 2 : "Not enough items in input list";

        this.analysisMinX.put(analysisID, in.get(0));
        this.analysisMaxX.put(analysisID, in.get(1));
    }

}
