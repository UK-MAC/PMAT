package uk.co.awe.analyses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import uk.ac.warwick.dcs.hpsg.warpp.math.Line2D;
import uk.ac.warwick.dcs.hpsg.warpp.math.Point2D;
import uk.ac.warwick.dcs.hpsg.warpp.math.regression.least.LeastSquaresRegression;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import static uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;

/**
 * An implementation of the AnalysisMethod interface which performs linear
 * regression on the set of data using the least squares regression tools from
 * the WARPP library.
 * 
 * @author AWE Plc
 */
public class LinearRegression implements AnalysisMethod {

    private static final Logger LOG = LoggerFactory.getLogger(LinearRegression.class);
    
    private final RegressionImpl<Line2D> regressionImpl;
    
    /**
     * Create a LinearRegression analysis method.
     */
    public LinearRegression() {
        this.regressionImpl = new RegressionImpl<Line2D>();
    }
    
    @Override
    public String analysisMethodName() {
        return "Linear Regression";
    }

    @Override
    public void performAnalysis(int analysisID, List<DataPoint> dataPoints) {
        List<Point2D> data = this.regressionImpl.processDataPoints(analysisID, dataPoints);
        
        Line2D analysis = LeastSquaresRegression.leastSquaresRegression(data.toArray(new Point2D[0]));
        LOG.info("Analysis Result: " + analysis);
        
        this.regressionImpl.setAnalysis(analysisID, analysis);
    }
    
    
    @Override
    public List<Double> exportAnalysis(int analysisID) {
        List<Double> exportAnalysis = this.regressionImpl.exportMinMax(analysisID);
        
        exportAnalysis.add(this.regressionImpl.getAnalysis(analysisID).getGradient().getValue());
        exportAnalysis.add(this.regressionImpl.getAnalysis(analysisID).getIntersectionWithY().getValue());

        return exportAnalysis;
    }

    @Override
    public int importAnalysis(List<Double> in) {
        
        final int analysisInputSize = 1;
        
        if (in.size() != analysisInputSize) {
            throw new IllegalArgumentException("Invalid argument list to "
                    + "analysis import");
        }
        int analysisID = this.regressionImpl.getNextID();
        
        final int gradientIdx = 2;
        final int intersectIdx = 3;
        
        this.regressionImpl.importMinMax(analysisID, in);
        this.regressionImpl.setAnalysis(analysisID,
                new Line2D(in.get(gradientIdx), in.get(intersectIdx)));
        
        return analysisID;
    }

    @Override
    public boolean hasConfigPanel() {
        return false;
    }

    @Override
    public JPanel getConfigPanel() {
        throw new UnsupportedOperationException(this.getClass().getName()
                + " has no config dialog");
    }

    @Override
    public List<DataPoint> returnAnalysis(int analysisID) {
        return this.regressionImpl.returnAnalysis(analysisID);
    }

    @Override
    public Collection<Integer> returnAnalysisIDs() {
        return this.regressionImpl.returnAnalysisIDs();
    }

    @Override
    public String getAnalysisResult(int analysisID) {
        Line2D line = this.regressionImpl.getAnalysis(analysisID);
        return String.format("y = %.3E x + %.3E",
                line.getGradient().getValue(),
                line.getIntersectionWithY().getValue());
    }
}
