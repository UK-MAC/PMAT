package uk.co.awe.pmat.testUtils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class Matchers {
    
    public static Matcher<String> contains(final String operand) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof String) {
                    return ((String) o).contains(operand);
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description d) {
                d.appendText("Should contain: ");
                d.appendValue(operand);
            }
        };
    }
    
}
