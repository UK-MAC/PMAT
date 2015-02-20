package uk.co.awe.analyses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import uk.ac.warwick.dcs.hpsg.warpp.math.LogarithmicLine;
import uk.ac.warwick.dcs.hpsg.warpp.math.Point2D;
import uk.ac.warwick.dcs.hpsg.warpp.math.Scalar;
import uk.ac.warwick.dcs.hpsg.warpp.math.regression.least.LeastSquaresRegression;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import static uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;

/**
 * An implementation of the AnalysisMethod interface which performs logarithm
 * regression on the set of data using the logarithm regression tools from
 * the WARPP library.
 * 
 * @author AWE Plc
 */
public class LogaritmicRegression implements AnalysisMethod {

    private static final Logger LOG = LoggerFactory.getLogger(LogaritmicRegression.class);
    
    private final RegressionImpl<LogarithmicLine> regressionImpl;
    
    /**
     * Create a LogaritmicRegression analysis method.
     */
    public LogaritmicRegression() {
        this.regressionImpl = new RegressionImpl<LogarithmicLine>();
    }
    
    @Override
    public String analysisMethodName() {
        return "Logarithmic Regression";
    }

    @Override
    public void performAnalysis(int analysisID, List<DataPoint> dataPoints) {
        List<Point2D> data = this.regressionImpl.processDataPoints(analysisID, dataPoints);

        LogarithmicLine analysis = LeastSquaresRegression.logarithmicRegression(data.toArray(new Point2D[0]));
        LOG.info("Analysis Result: " + analysis);
        
        this.regressionImpl.setAnalysis(analysisID, analysis);
    }

    @Override
    public List<Double> exportAnalysis(int analysisID) {
        List<Double> exportAnalysis = this.regressionImpl.exportMinMax(analysisID);
        
        LogarithmicLine analysis = this.regressionImpl.getAnalysis(analysisID);
        
        exportAnalysis.add(analysis.getMultiplicativeTerm().getValue());
        exportAnalysis.add(analysis.getAdditiveTerm().getValue());
        
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

        LogarithmicLine line = new LogarithmicLine();
        
        final int multiplicativeIdx = 2;
        final int additiveIdx = 3;
        
        line.setMultiplicativeTerm(new Scalar(in.get(multiplicativeIdx)));
        line.setAdditiveTerm(new Scalar(in.get(additiveIdx)));
        
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
        LogarithmicLine line = this.regressionImpl.getAnalysis(analysisID);
        return String.format("y = %.3E ln(x) + %.3E",
                line.getMultiplicativeTerm().getValue(),
                line.getAdditiveTerm().getValue());
    }
}
