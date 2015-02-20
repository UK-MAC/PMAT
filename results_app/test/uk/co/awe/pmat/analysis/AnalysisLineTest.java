package uk.co.awe.pmat.analysis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineStyle;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import uk.co.awe.pmat.LineType;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class AnalysisLineTest {

    public AnalysisLineTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void an_analysis_line_create_with_a_given_name_and_line_type_will_have_both_but_with_a_solid_line_style() {

        String name = "Test Name";
        LineStyle lineStyle = LineStyle.DOTS;
        GraphColour lineColour = GraphColour.GREEN;
        int lineWidth = 2;
        LineType lineType = new LineType(lineStyle, lineColour, lineWidth);

        LineType expLineType = new LineType(LineStyle.LINE, lineColour, lineWidth);

        AnalysisLine analysisLine = new AnalysisLine(name, lineType);

        assertThat(analysisLine.getName(), equalTo(name));
        assertThat(analysisLine.getLineType(), equalTo(expLineType));
    }
}