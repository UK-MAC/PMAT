package uk.co.awe.pmat.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.db.series.SeriesGroup;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class GraphLineTest {

    public GraphLineTest() {
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

    // <editor-fold defaultstate="collapsed" desc="creating_a_new_graph_line_creates_a_line_with_no_series_groups_and_a_default_line_type">
    @Test
    public void creating_a_new_graph_line_creates_a_line_with_blank_name_and_a_default_line_type() {
        GraphLine graphLine = new GraphLine();

        assertThat(graphLine.getName(), equalTo(""));

        Collection<String> expNames = Collections.emptyList();
        assertThat(graphLine.getNames(), equalTo(expNames));

        assertThat(graphLine.getLineType(), equalTo(new LineType()));
    }// </editor-fold>

    @Test
    public void adding_a_series_group_to_a_blank_line_gives_a_line_wrapping_that_one_group() {
        GraphLine graphLine = new GraphLine();

        final String groupName = "Test Series Group";

        SeriesGroup seriesGroup = new SeriesGroup() {
            @Override
            public String getName() {
                return groupName;
            }
        };

        graphLine.addSeriesGroup(seriesGroup);

        assertThat(graphLine.getName(), equalTo(groupName));
        assertThat(graphLine.getNames(), equalTo((Collection<String>) Arrays.asList(groupName)));
        assertThat(graphLine.getLineType(), equalTo(new LineType()));
        assertThat(graphLine.getSeriesGroup(0), equalTo(seriesGroup));
    }

    @Test
    public void adding_multiple_series_groups_to_a_line_gives_a_line_wrapping_those_groups() {
        GraphLine graphLine = new GraphLine();

        final String group1Name = "Test Series Group 1";
        final String group2Name = "Test Series Group 2";

        SeriesGroup group1 = new SeriesGroup() {
            @Override
            public String getName() {
                return group1Name;
            }
        };
        SeriesGroup group2 = new SeriesGroup() {
            @Override
            public String getName() {
                return group2Name;
            }
        };

        graphLine.addSeriesGroup(group1);
        graphLine.addSeriesGroup(group2);

        assertThat(graphLine.getName(), equalTo(group1Name + " / " + group2Name));
        assertThat(graphLine.getNames(), equalTo((Collection<String>) Arrays.asList(group1Name, group2Name)));
        assertThat(graphLine.getLineType(), equalTo(new LineType()));
        assertThat(graphLine.getSeriesGroup(0), equalTo(group1));
        assertThat(graphLine.getSeriesGroup(1), equalTo(group2));
    }

    @Test
    public void adding_series_groups_for_a_graph_line_copies_all_the_series_groups() {
        GraphLine graphLine1 = new GraphLine();

        final String group1Name = "Test Series Group 1";
        final String group2Name = "Test Series Group 2";

        SeriesGroup group1 = new SeriesGroup() {
            @Override
            public String getName() {
                return group1Name;
            }
        };
        SeriesGroup group2 = new SeriesGroup() {
            @Override
            public String getName() {
                return group2Name;
            }
        };

        graphLine1.addSeriesGroup(group1);
        graphLine1.addSeriesGroup(group2);

        GraphLine graphLine2 = new GraphLine();
        graphLine2.addSeriesGroups(graphLine1);

        assertThat(graphLine2.getName(), equalTo(group1Name + " / " + group2Name));
        assertThat(graphLine2.getNames(), equalTo((Collection<String>) Arrays.asList(group1Name, group2Name)));
        assertThat(graphLine2.getLineType(), equalTo(new LineType()));
        assertThat(graphLine2.getSeriesGroup(0), equalTo(group1));
        assertThat(graphLine2.getSeriesGroup(1), equalTo(group2));
    }

}