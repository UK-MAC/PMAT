package uk.co.awe.analyses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import uk.ac.warwick.dcs.hpsg.warpp.math.PolynomialLine;
import uk.ac.warwick.dcs.hpsg.warpp.math.Pair;
import uk.ac.warwick.dcs.hpsg.warpp.math.Point2D;
import uk.ac.warwick.dcs.hpsg.warpp.math.Scalar;
import uk.ac.warwick.dcs.hpsg.warpp.math.regression.least.LeastSquaresRegression;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import static uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;

/**
 * An implementation of the AnalysisMethod interface which performs inverse
 * regression on the set of data using the inverse regression tools from
 * the WARPP library.
 * 
 * @author AWE Plc
 */
public class InverseRegression implements AnalysisMethod {

    private static final Logger LOG = LoggerFactory.getLogger(InverseRegression.class);
    
    private final RegressionImpl<PolynomialLine> regressionImpl;
    
    /**
     * Create an InverseRegression analysis method.
     */
    public InverseRegression() {
        this.regressionImpl = new RegressionImpl<PolynomialLine>();
    }
    
    @Override
    public String analysisMethodName() {
        return "Inverse Regression";
    }

    @Override
    public void performAnalysis(int analysisID, List<DataPoint> dataPoints) {
        List<Point2D> data = this.regressionImpl.processDataPoints(analysisID, dataPoints);

        PolynomialLine analysis = LeastSquaresRegression.inverseRegression(data.toArray(new Point2D[0]));
        LOG.info("Analysis Result: " + analysis);
        
        this.regressionImpl.setAnalysis(analysisID, analysis);
    }

    @Override
    public List<Double> exportAnalysis(int analysisID) {
        List<Double> exportAnalysis = this.regressionImpl.exportMinMax(analysisID);
        
        Collection<Pair<Scalar, Scalar>> coeffs = this.regressionImpl.getAnalysis(analysisID).getCoefficients();
        for (Pair<Scalar, Scalar> coeff : coeffs) {
            exportAnalysis.add(coeff.getElementA().getValue());
            exportAnalysis.add(coeff.getElementB().getValue());
        }
        return exportAnalysis;
    }

    @Override
    public int importAnalysis(List<Double> in) {
        
        final int analysisInputSize = 5;
        
        if (in.size() < analysisInputSize) {
            throw new IllegalArgumentException("Invalid argument list to "
                    + "analysis import");
        }
        int analysisID = this.regressionImpl.getNextID();

        this.regressionImpl.importMinMax(analysisID, in);

        final int analysisStartingIdx = 3;
        
        PolynomialLine line = new PolynomialLine();
        for (int inIdx = analysisStartingIdx; inIdx < in.size(); inIdx += 2) {
            line.setCoefficient(
                    new Scalar(in.get(inIdx)),
                    new Scalar(in.get(inIdx + 1)));
        }
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
        Collection<Pair<Scalar, Scalar>> coeffs = this.regressionImpl
                .getAnalysis(analysisID).getCoefficients();
        double constantTerm = 0;
        double inverseTerm = 0;
        for (Pair<Scalar, Scalar> coeff : coeffs) {
            if (coeff.getElementA().equals(new Scalar(0))) {
                constantTerm = coeff.getElementB().getValue();
            }
            if (coeff.getElementA().equals(new Scalar(-1))) {
                inverseTerm = coeff.getElementB().getValue();
            }
        }
        return String.format("y = %.3E x^-1 + %.3E", inverseTerm, constantTerm);
    }
}
