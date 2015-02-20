package uk.co.awe.pmat.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class ArraysTest {

    public ArraysTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void toString_should_return_the_same_as_toString_on_the_list_of_Class_simpleNames() {

        Class[] classArray = new Class[] { String.class, Integer.class, Double.class, ArrayUtils.class };
        String[] classNameArray = new String[classArray.length];

        for (int idx = 0; idx < classArray.length; ++idx) {
            classNameArray[idx] = classArray[idx].getSimpleName();
        }

        assertThat(ArrayUtils.toString(classArray), equalTo(java.util.Arrays.toString(classNameArray)));

    }

    @Test
    public void arrayContains_should_return_false_if_the_outer_array_is_empty_but_the_inner_is_not() {
        Integer[] outer = new Integer[] {};
        Integer[] inner = new Integer[] {1, 2, 3};

        assertFalse(ArrayUtils.arrayContains(outer, inner));
    }

    @Test
    public void arrayContains_should_return_true_if_the_inner_array_is_empty() {
        Integer[] outer = new Integer[] {};
        Integer[] inner = new Integer[] {};

        assertTrue(ArrayUtils.arrayContains(outer, inner));

        outer = new Integer[] {1, 2, 3};
        inner = new Integer[] {};

        assertTrue(ArrayUtils.arrayContains(outer, inner));
    }

    @Test
    public void arrayContains_should_return_true_if_and_only_if_all_the_items_in_the_inner_array_are_in_the_outer_array() {
        Integer[] outer = new Integer[] {1, 2, 3, 4, 5};

        Integer[] inner = new Integer[] {1};
        assertTrue(ArrayUtils.arrayContains(outer, inner));

        inner = new Integer[] {2};
        assertTrue(ArrayUtils.arrayContains(outer, inner));

        inner = new Integer[] {1, 2};
        assertTrue(ArrayUtils.arrayContains(outer, inner));

        inner = new Integer[] {2, 1};
        assertTrue(ArrayUtils.arrayContains(outer, inner));

        inner = new Integer[] {3, 1, 2, 5, 4};
        assertTrue(ArrayUtils.arrayContains(outer, inner));

        inner = new Integer[] {6};
        assertFalse(ArrayUtils.arrayContains(outer, inner));

        inner = new Integer[] {1, 2, 3, 4, 5, 6};
        assertFalse(ArrayUtils.arrayContains(outer, inner));
    }

}