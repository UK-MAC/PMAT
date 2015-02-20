package uk.co.awe.pmat.graph;

import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import java.util.Collection;
import java.util.Collections;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.DatabaseManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.awe.pmat.analysis.RestrictionCollection;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.DatabaseException;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class GraphDataTest {

    public GraphDataTest() {
        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST)));
    }

    @BeforeClass
    public static void setUpClass() {
        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST)));
    }
    
    @AfterClass
    public static void tearDownClass() {
        DatabaseManager.setDatabaseConnection(null);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // <editor-fold defaultstate="collapsed" desc="a_blank_graph_data_is_not_ready_to_plot">
    @Test
    public void a_blank_graph_data_is_not_ready_to_plot() {
        GraphData graphData = new GraphData(null);
        
        assertThat(graphData.readyToPlot(), equalTo(false));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="a_blank_graph_data_contains_a_single_line">
    @Test
    public void a_blank_graph_data_contains_a_single_line() {
        GraphData graphData = new GraphData(null);

        assertThat(graphData.getPlottableLines().size(), equalTo(1));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="a_blank_graph_data_returns_an_empty_data_grid">
    @Test
    public void a_blank_graph_data_returns_an_empty_data_grid() {
        GraphData graphData = new GraphData(null);

        assertThat(graphData.getTableData(), equalTo(new Object[][] {}));
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="setting_the_graph_axes_sets_the_graph_data_as_ready_to_plot">
    @Test
    public void setting_the_graph_axes_sets_the_graph_data_as_ready_to_plot()
            throws DatabaseException, GraphDataException, DerivedDataException {
        
        GraphData graphData = new GraphData(new RestrictionCollection() {
            @Override
            public Collection<Restriction> getRestrictions() {
                return Collections.emptyList();
            }
        });

        graphData.setXAxis(new Axis() {
            @Override public AxisType getType() { return AxisType.PARAMETER; }
            @Override public AxisName getAxisName() { return AxisName.X1; }
            @Override public Object getSubType() { return ""; }
            @Override public String displayName() { return "Test X Axis"; }
        });
        
        assertThat(graphData.readyToPlot(), equalTo(false));
        
        graphData.setYAxis(new Axis() {
            @Override public AxisType getType() { return AxisType.PARAMETER; }
            @Override public AxisName getAxisName() { return AxisName.Y1; }
            @Override public Object getSubType() { return ""; }
            @Override public String displayName() { return "Test X Axis"; }
        });
        
        assertThat(graphData.readyToPlot(), equalTo(false));
        
        graphData.setRank(Rank.UNKNOWN);
        
        assertThat(graphData.readyToPlot(), equalTo(false));
        
        graphData.setYAxisType(AxisValueType.VALUE);

        assertThat(graphData.readyToPlot(), equalTo(true));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="a_ready_to_plot_graph_data_should_return_a_non_empty_data_grid">
    @Ignore @Test
    public void a_ready_to_plot_graph_data_should_return_a_non_empty_data_grid() {
        assertThat(true, equalTo(false));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="adding_a_single_series_will_give_a_graph_data_with_lines_for_each_series_group">
    @Ignore @Test
    public void adding_a_single_series_will_give_a_graph_data_with_lines_for_each_series_group() {
        assertThat(true, equalTo(false));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="adding_two_series_will_give_a_graph_data_with_lines_for_the_cartesian_join_of_the_series_groups">
    @Ignore @Test
    public void adding_two_series_will_give_a_graph_data_with_lines_for_the_cartesian_join_of_the_series_groups() {
        assertThat(true, equalTo(false));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="removing_a_series_from_a_graph_data_with_two_series_will_leave_lines_just_for_one_series">
    @Ignore @Test
    public void removing_a_series_from_a_graph_data_with_two_series_will_leave_lines_just_for_one_series() {
        assertThat(true, equalTo(false));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="removing_a_series_line_will_leave_lines_for_all_other_series_groups">
    @Ignore @Test
    public void removing_a_series_line_will_leave_lines_for_all_other_series_groups() {
        assertThat(true, equalTo(false));
    }// </editor-fold>

}