package uk.co.awe.pmat.graph;

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
public class GraphDataExceptionTest {

    public GraphDataExceptionTest() {
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

    // <editor-fold defaultstate="collapsed" desc="graph_data_exceptions_can_be_thrown_with_an_accompanying_message">
    @Test
    public void graph_data_exceptions_can_be_thrown_with_an_accompanying_message() {
        String msg = "Exception message";

        try {
            throw new GraphDataException(msg);
        } catch (GraphDataException ex) {
            assertThat(ex.getMessage(), equalTo(msg));
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="graph_data_exceptions_can_be_thrown_with_an_accompanying_message_and_cause">
    @Test
    public void graph_data_exceptions_can_be_thrown_with_an_accompanying_message_and_cause() {
        String msg = "Exception message";
        Throwable cause = new NullPointerException();

        try {
            throw new GraphDataException(msg, cause);
        } catch (GraphDataException ex) {
            assertThat(ex.getMessage(), equalTo(msg));
            assertThat(ex.getCause(), equalTo(cause));
        }
    }// </editor-fold>

}