package uk.co.awe.pmat.utils;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.hamcrest.core.IsNull;
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
public class ChangeEventSupportTest {

    public ChangeEventSupportTest() {
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

    private static class TestListener implements ChangeListener {
        Object src;

        @Override
        public void stateChanged(ChangeEvent e) {
            src = e.getSource();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="creating_a_change_event_support_with_a_listener_notifies_that_list_when_fired">
    @Test
    public void creating_a_change_event_support_with_a_listener_notifies_that_list_on_the_EDT_when_fired() {
        final Object src = new Object();
        ChangeEventSupport ces = new ChangeEventSupport(src);

        final TestListener cl = new TestListener();

        ces.addChangeListener(cl);
        ces.fireChangeEvent();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertThat(cl.src, is(src));
            }
        });
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="removing_a_listener_means_it_will_not_be_notified_when_the_event_support_is_fired">
    @Test
    public void removing_a_listener_means_it_will_not_be_notified_when_the_event_support_is_fired() {
        final Object src = new Object();
        ChangeEventSupport ces = new ChangeEventSupport(src);

        final TestListener cl = new TestListener();

        ces.addChangeListener(cl);
        ces.removeChangeListener(cl);

        ces.fireChangeEvent();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assertNull(cl.src);
            }
        });
    }// </editor-fold>

}