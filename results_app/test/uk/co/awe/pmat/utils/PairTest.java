package uk.co.awe.pmat.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class PairTest {

    public PairTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="a_pair_encapulates_two_objects">
    @Test
    public void a_pair_encapulates_two_objects() {
        Double a = new Double(10.);
        String b = "abc";

        Pair<Double, String> pair = new Pair<Double, String>(a, b);
        
        assertThat(pair.getFirst(), is(a));
        assertThat(pair.getSecond(), is(b));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="equality_of_a_pair_depends_upon_equality_of_contained_objects">
    @Test
    public void equality_of_a_pair_depends_upon_equality_of_contained_objects() {
        Double a = new Double(10.);
        Double b = new Double(1.);
        String c = "abc";

        Pair<Double, String> pair1 = new Pair<Double, String>(a, c);
        Pair<Double, String> pair2 = new Pair<Double, String>(b, c);
        
        assertThat(pair1, equalTo(pair1));
        assertThat(pair1, not(equalTo(pair2)));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hashcode_of_a_pair_depends_upon_hashcodes_of_contained_objects">
    @Test
    public void hashcode_of_a_pair_depends_upon_hashcodes_of_contained_objects() {
        Double a = new Double(10.);
        String b = "abc";
        String c = "abd";

        Pair<Double, String> pair1 = new Pair<Double, String>(a, b);
        Pair<Double, String> pair2 = new Pair<Double, String>(a, c);

        assertThat(pair1.hashCode(), not(equalTo(pair2.hashCode())));
    }// </editor-fold>

}