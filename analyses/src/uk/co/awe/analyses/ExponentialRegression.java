package uk.co.awe.analyses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import uk.ac.warwick.dcs.hpsg.warpp.math.ExponentialLine;
import uk.ac.warwick.dcs.hpsg.warpp.math.Point2D;
import uk.ac.warwick.dcs.hpsg.warpp.math.Scalar;
import uk.ac.warwick.dcs.hpsg.warpp.math.regression.least.LeastSquaresRegression;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import static uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;

/**
 * An implementation of the AnalysisMethod interface which performs exponential
 * regression on the set of data using the exponential regression tools from
 * the WARPP library.
 * 
 * @author AWE Plc
 */
public class ExponentialRegression implements AnalysisMethod {

    private static final Logger LOG = LoggerFactory.getLogger(ExponentialRegression.class);
    
    private final RegressionImpl<ExponentialLine> regressionImpl;
    
    /**
     * Create an ExponentialRegression analysis method.
     */
    public ExponentialRegression() {
        this.regressionImpl = new RegressionImpl<ExponentialLine>();
    }
    
    @Override
    public String analysisMethodName() {
        return "Exponential Regression";
    }

    @Override
    public void performAnalysis(int analysisID, List<DataPoint> dataPoints) {
        List<Point2D> data = this.regressionImpl.processDataPoints(analysisID, dataPoints);

        ExponentialLine analysis = LeastSquaresRegression.exponentialRegression(data.toArray(new Point2D[0]));
        LOG.info("Analysis Result: " + analysis);
        
        this.regressionImpl.setAnalysis(analysisID, analysis);
    }

    @Override
    public List<Double> exportAnalysis(int analysisID) {
        List<Double> exportAnalysis = this.regressionImpl.exportMinMax(analysisID);
        
        ExponentialLine analysis = this.regressionImpl.getAnalysis(analysisID);
        
        exportAnalysis.add(analysis.getScalar().getValue());
        exportAnalysis.add(analysis.getExponent().getValue());
        
        return exportAnalysis;
    }

    @Override
    public int importAnalysis(List<Double> in) {
        
        final int analysisInputSize = 4;
        
        if (in.size() != analysisInputSize) {
            throw new IllegalArgumentException("Invalid argument list to "
                    + "analysis import");
        }
        int analysisID = this.regressionImpl.getNextID();

        this.regressionImpl.importMinMax(analysisID, in);

        ExponentialLine line = new ExponentialLine();
        
        final int scalarIdx = 2;
        final int exponentIdx = 3;
        
        line.setScalar(new Scalar(in.get(scalarIdx)));
        line.setExponent(new Scalar(in.get(exponentIdx)));
        
        this.regressionImpl.setAnalysis(analysisID, line);
        
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
        ExponentialLine line = this.regressionImpl.getAnalysis(analysisID);
        return String.format("y = %.3E e^%.3E",
                line.getScalar().getValue(), line.getExponent().getValue());
    }
}
