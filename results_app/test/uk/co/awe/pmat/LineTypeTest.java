package uk.co.awe.pmat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class LineTypeTest {

    public LineTypeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void creating_a_linetype_with_given_attributes_gives_a_linetype_object_with_those_attributes() {
        LineStyle expLineStyle = LineStyle.LINE;
        GraphColour expGraphColour = GraphColour.GREEN;
        int expLineWidth = 1;
        
        LineType lineType = new LineType(expLineStyle, expGraphColour, expLineWidth);
        
        assertThat(lineType.getStyle(), equalTo(expLineStyle));
        assertThat(lineType.getColour(), equalTo(expGraphColour));
        assertThat(lineType.getWidth(), equalTo(expLineWidth));
    }
    
    @Test
    public void creating_a_linetype_with_no_attributes_gives_a_linetype_with_default_attributes() {
        LineType lineType = new LineType();
        
        assertThat(lineType.getStyle(), notNullValue());
        assertThat(lineType.getColour(), notNullValue());
        assertThat(lineType.getWidth(), notNullValue());
    }
    
    @Test
    public void setting_the_line_color_correctly_updates_the_linetype_object() {
        LineType lineType = new LineType();
        
        GraphColour oldColour = lineType.getColour();
        GraphColour[] colours = GraphColour.values();
        
        int oldColourIdx = 0;
        for (GraphColour colour : colours) {
            if (colour.equals(oldColour)) { break; }
            ++oldColourIdx;
        }
        
        GraphColour newColour = colours[(oldColourIdx + 1) % colours.length];
        assertThat(newColour, not(equalTo(oldColour)));
        
        lineType.setColour(newColour);
        assertThat(lineType.getColour(), equalTo(newColour));
    }
    
    @Test
    public void setting_the_line_style_correctly_updates_the_linetype_object() {
        LineType lineType = new LineType();
        
        LineStyle oldStyle = lineType.getStyle();
        LineStyle[] styles = LineStyle.values();
        
        int oldStyleIdx = 0;
        for (LineStyle style : styles) {
            if (style.equals(oldStyle)) { break; }
            ++oldStyleIdx;
        }
        
        LineStyle newStyle = styles[(oldStyleIdx + 1) % styles.length];
        assertThat(newStyle, not(equalTo(oldStyle)));
        
        lineType.setStyle(newStyle);
        assertThat(lineType.getStyle(), equalTo(newStyle));
    }
    
    @Test
    public void setting_the_line_width_correctly_updates_the_linetype_object() {
        LineType lineType = new LineType();
        
        int oldWidth = lineType.getWidth();
        int newWidth = oldWidth + 1;
        
        lineType.setWidth(newWidth);
        assertThat(lineType.getWidth(), equalTo(newWidth));
    }
    
    @Test
    public void creating_a_copy_of_a_linetype_copies_the_attributes_to_the_copy() {
        
        GraphColour colour = GraphColour.GREEN;
        LineStyle style = LineStyle.BARS;
        int width = 1;
        
        LineType lineType = new LineType(style, colour, width);
        LineType copy = lineType.copy();
        
        assertThat(copy.getColour(), equalTo(lineType.getColour()));
        assertThat(copy.getStyle(), equalTo(lineType.getStyle()));
        assertThat(copy.getWidth(), equalTo(lineType.getWidth()));
        
        colour = GraphColour.BLUE;
        style = LineStyle.DOTS;
        width = 2;
        
        lineType = new LineType(style, colour, width);
        copy = lineType.copy();
        
        assertThat(copy.getColour(), equalTo(lineType.getColour()));
        assertThat(copy.getStyle(), equalTo(lineType.getStyle()));
        assertThat(copy.getWidth(), equalTo(lineType.getWidth()));
    }

}