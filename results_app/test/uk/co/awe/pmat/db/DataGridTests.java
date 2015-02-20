package uk.co.awe.pmat.db;

import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.db.series.SeriesGroup;
import uk.co.awe.pmat.db.series.SeriesParam;
import uk.co.awe.pmat.db.series.SeriesMetaData;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class DataGridTests {

    public DataGridTests() {
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

    // <editor-fold defaultstate="collapsed" desc="test_get_data_grid_without_restrictions_or_series">
    @Test
    public void test_get_data_grid_without_restrictions_or_series() throws DatabaseException {

        DatabaseConnection dbMapping = new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST));

        Axis xAxis = AxisType.PARAMETER.newAxis(AxisName.X1, "PE Count");
        Axis yAxis = AxisType.RESULT.newAxis(AxisName.Y1, "Application Time");

        DataGrid dataGrid = dbMapping.getDataGrid(xAxis, yAxis, Rank.ANY_RANK, null, null);

        assertThat(dataGrid.getRows().size(), equalTo(380));

        assertThat(dataGrid.getxAxisName(), equalTo("PE Count"));
        assertThat(dataGrid.getyAxisName(), equalTo("Application Time"));

        final SeriesGroup expSeriesGroup = new SeriesGroup() {
            @Override
            public String getName() {
                return "";
            }
        };
        
        assertThat(dataGrid.getRows().get(0).getSeriesGroup(), equalTo(expSeriesGroup));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="test_get_data_grid_without_restrictions_but_with_single_series">
    @Test
    public void test_get_data_grid_without_restrictions_but_with_single_series() throws DatabaseException {

        DatabaseConnection dbMapping = new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST));

        Axis xAxis = AxisType.PARAMETER.newAxis(AxisName.X1, "PE Count");
        Axis yAxis = AxisType.RESULT.newAxis(AxisName.Y1, "Application Time");
        Series series = new SeriesMetaData(MetaData.Type.MACHINE);

        DataGrid dataGrid = dbMapping.getDataGrid(xAxis, yAxis, Rank.ANY_RANK, null, Arrays.asList(series));

        assertThat(dataGrid.getRows().size(), equalTo(380));

        assertThat(dataGrid.getxAxisName(), equalTo("PE Count"));
        assertThat(dataGrid.getyAxisName(), equalTo("Application Time"));
        
        final SeriesGroup expSeriesGroup = new SeriesGroup() {
            @Override
            public String getName() {
                return "Willow";
            }
        };

        assertThat(dataGrid.getRows().get(0).getSeriesGroup(), equalTo(expSeriesGroup));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="test_get_data_grid_without_restrictions_with_two_series">
    @Test
    public void test_get_data_grid_without_restrictions_with_two_series() throws DatabaseException {

        DatabaseConnection dbMapping = new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST));

        Axis xAxis = AxisType.PARAMETER.newAxis(AxisName.X1, "PE Count");
        Axis yAxis = AxisType.RESULT.newAxis(AxisName.Y1, "Application Time");
        Series series1 = new SeriesMetaData(MetaData.Type.MACHINE);
        Series series2 = new SeriesParam("meshx");

        DataGrid dataGrid = dbMapping.getDataGrid(xAxis, yAxis, Rank.ANY_RANK, null, Arrays.asList(series1, series2));

        assertThat(dataGrid.getRows().size(), equalTo(348));

        assertThat(dataGrid.getxAxisName(), equalTo("PE Count"));
        assertThat(dataGrid.getyAxisName(), equalTo("Application Time"));

        final SeriesGroup expSeriesGroup = new SeriesGroup() {
            @Override
            public String getName() {
                return "Willow / 120";
            }
        };

        assertThat(dataGrid.getRows().get(0).getSeriesGroup(), equalTo(expSeriesGroup));
    }// </editor-fold>


}