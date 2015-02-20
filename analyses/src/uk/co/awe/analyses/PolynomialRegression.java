package uk.co.awe.analyses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.ac.warwick.dcs.hpsg.warpp.math.PolynomialLine;
import uk.ac.warwick.dcs.hpsg.warpp.math.Pair;
import uk.ac.warwick.dcs.hpsg.warpp.math.Point2D;
import uk.ac.warwick.dcs.hpsg.warpp.math.Scalar;
import uk.ac.warwick.dcs.hpsg.warpp.math.exception.MatrixNotInvertibleException;
import uk.ac.warwick.dcs.hpsg.warpp.math.regression.least.LeastSquaresRegression;
import uk.co.awe.pmat.analysis.AnalysisMethod;
import static uk.co.awe.pmat.analysis.AnalysisMethod.DataPoint;

/**
 * An implementation of the AnalysisMethod interface which performs polynomial
 * regression on the set of data using the polynomial least squares regression
 * tools from the WARPP library.
 * 
 * @author AWE Plc
 */
public class PolynomialRegression implements AnalysisMethod, ChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(PolynomialRegression.class);
    
    private final RegressionImpl<PolynomialLine> regressionImpl;
    private final PolynomialRegressionPanel polynomialRegressionPanel;

    private int order;
    
    /**
     * Create a PolynomialRegression analysis method.
     */
    public PolynomialRegression() {
        this.regressionImpl = new RegressionImpl<PolynomialLine>();
        
        this.polynomialRegressionPanel = new PolynomialRegressionPanel(this);
        this.order = this.polynomialRegressionPanel.getOrder();
    }
    
    @Override
    public String analysisMethodName() {
        return "Polynomial Regression";
    }

    @Override
    public void performAnalysis(int analysisID, List<DataPoint> dataPoints) {
        List<Point2D> data = this.regressionImpl.processDataPoints(analysisID, dataPoints);

        try {
            PolynomialLine analysis = LeastSquaresRegression.polynomialLeastSquaresRegression(data.toArray(new Point2D[0]), this.order);
            LOG.info("Analysis Result: " + analysis);
            
            this.regressionImpl.setAnalysis(analysisID, analysis);
        } catch (MatrixNotInvertibleException ex) {
            this.regressionImpl.setAnalysis(analysisID, new PolynomialLine());
        }
    }

    @Override
    public List<Double> exportAnalysis(int analysisID) {
        List<Double> exportAnalysis = this.regressionImpl.exportMinMax(analysisID);
        
        exportAnalysis.add((double) this.order);
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

        final int numberOfTermsPerOrder = 2;
        final int numberOfNonOrderTerms = 3;
        
        this.order = (int) Math.round(in.get(2));
        if (in.size() != this.order * numberOfTermsPerOrder 
                + numberOfNonOrderTerms) {
            throw new IllegalArgumentException("Invalid argument list to "
                    + "analysis import");
        }
        int inIdx = numberOfNonOrderTerms;
        PolynomialLine line = new PolynomialLine();
        while (inIdx < in.size()) {
            line.setCoefficient(new Scalar(in.get(inIdx)), new Scalar(in.get(inIdx + 1)));
            inIdx += numberOfTermsPerOrder;
        }
        this.regressionImpl.setAnalysis(analysisID, line);
        
        return analysisID;
    }

    @Override
    public boolean hasConfigPanel() {
        return true;
    }

    @Override
    public JPanel getConfigPanel() {
        return this.polynomialRegressionPanel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.order = this.polynomialRegressionPanel.getOrder();
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
        StringBuilder stringBuilder = new StringBuilder("y = ");
        Collection<Pair<Scalar, Scalar>> coeffs = this.regressionImpl.getAnalysis(analysisID).getCoefficients();
        String delim = "";
        for (Pair<Scalar, Scalar> coeff : coeffs) {
            stringBuilder.append(delim)
                    .append(String.format("%.3E", coeff.getElementB().getValue()))
                    .append(" x^")
                    .append(String.format("%d", (int) coeff.getElementA().getValue()));
            delim = " + ";
        }
        return stringBuilder.toString();
    }

}
