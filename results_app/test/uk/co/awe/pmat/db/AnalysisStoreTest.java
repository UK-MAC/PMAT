package uk.co.awe.pmat.db;

import uk.co.awe.pmat.db.series.Series;
import java.util.Collection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.LineType;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.utils.Pair;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class AnalysisStoreTest {
    
    private static DatabaseConnection DB_CONNECTION;
    private static TestDatabase TD;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DB_CONNECTION = new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST));
        DatabaseManager.setDatabaseConnection(DB_CONNECTION);
        TD = new TestDatabase();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DB_CONNECTION = null;
        TD = null;
    }
    
    @Before
    public void setUp() throws Exception {
        TD.loadData();
    }
    
    @After
    public void tearDown() throws Exception {
        TD.clearData();
        DB_CONNECTION.close();
    }
    
    final String creator = "Test Creator";
    final Date date = new Date();
    final String notes = "Test Analysis";
    final boolean dataPrivate = false;
    
    @Test
    public void saving_an_empty_analysis_stores_it_in_the_Analysis_table() throws Exception {
        List<Graph> graphs = Collections.emptyList();
        List<DerivedData> dd = Collections.emptyList();
        List<Restriction> restrictions = Collections.emptyList();
        List<Pair<String,String>> renames = Collections.emptyList();
        
        Analysis analysis = new Analysis(creator, date, notes, dataPrivate,
                graphs, dd, restrictions, renames);
        
        long numRows = TD.getRowCount(TestDatabase.Table.Analysis);
        
        DB_CONNECTION.save(analysis);
    
        long newNumRows = TD.getRowCount(TestDatabase.Table.Analysis);
        
        assertThat(newNumRows, equalTo(numRows + 1));
    }
    
    @Test
    public void saving_an_analysis_with_restrictions_stores_the_analysis_and_analysis_criteria() throws Exception {
        List<Graph> graphs = Collections.emptyList();
        List<DerivedData> dd = Collections.emptyList();
        List<Pair<String,String>> renames = Collections.emptyList();

        Restriction restriction1 = DB_CONNECTION.newRestriction(
                Category.MACHINE, "name", Rank.UNKNOWN, Comparator.EQ,
                new Value<>("name", Rank.UNKNOWN, "Test Machine"));
        Restriction restriction2 = DB_CONNECTION.newRestriction(
                Category.RUN, "tag", Rank.UNKNOWN, Comparator.NE,
                new Value<>("tag", Rank.UNKNOWN, null));
        Restriction restriction3 = DB_CONNECTION.newRestriction(
                Category.RUN, "tag", Rank.UNKNOWN, Comparator.NE,
                new Value<>("tag", Rank.UNKNOWN, "Test Tag"));
        List<Restriction> restrictions
                = Arrays.asList(restriction1, restriction2, restriction3);
        
        Analysis analysis = new Analysis(creator, date, notes, dataPrivate,
                graphs, dd, restrictions, renames);
        
        long analysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long criteriaRows = TD.getRowCount(TestDatabase.Table.AnalysisCriteria);
        
        DB_CONNECTION.save(analysis);
    
        long newAnalysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long newCriteriaRows = TD.getRowCount(TestDatabase.Table.AnalysisCriteria);
        
        assertThat(newAnalysisRows, equalTo(analysisRows + 1));
        assertThat(newCriteriaRows, equalTo(criteriaRows + 3));
    }
    
    @Test
    public void saving_an_analysis_with_derived_data_stores_the_analysis_and_data() throws Exception {
        List<Graph> graphs = Collections.emptyList();
        List<Restriction> restrictions = Collections.emptyList();
        List<Pair<String,String>> renames = Collections.emptyList();

        DerivedData dd1 = new DerivedData("New Var 1", "avg( 'Application Time' )");
        DerivedData dd2 = new DerivedData("New Var 2", "'Application Time' / 'PE Count'");
        List<DerivedData> ddList = Arrays.asList(dd1, dd2) ;
        
        Analysis analysis = new Analysis(creator, date, notes, dataPrivate,
                graphs, ddList, restrictions, renames);
        
        long analysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long ddRows = TD.getRowCount(TestDatabase.Table.AnalysisDerivedData);
        
        DB_CONNECTION.save(analysis);
    
        long newAnalysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long newDdRows = TD.getRowCount(TestDatabase.Table.AnalysisDerivedData);
        
        assertThat(newAnalysisRows, equalTo(analysisRows + 1));
        assertThat(newDdRows, equalTo(ddRows + 2));
    }
    
    class MockAxis extends Axis {

        final AxisType axisType;
        final AxisName axisName;
        final Object value;

        public MockAxis(AxisType axisType, AxisName axisName, Object value) {
            this.axisType = axisType;
            this.axisName = axisName;
            this.value = value;
        }

        @Override
        public AxisType getType() {
            return axisType;
        }

        @Override
        public AxisName getAxisName() {
            return axisName;
        }

        @Override
        public Object getSubType() {
            return value;
        }

        @Override
        public String displayName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    @Test
    public void saving_an_analysis_with_a_graph_with_no_series_and_no_methods() throws Exception {
        List<DerivedData> ddList = Collections.emptyList();
        List<Restriction> restrictions = Collections.emptyList();
        List<Pair<String,String>> renames = Collections.emptyList();

        Collection<AnalysisMethodData> methodData = Collections.emptyList();
        Collection<Series> series = Collections.emptyList();
        
        Axis xAxis = new MockAxis(AxisType.PARAMETER, AxisName.X1, "PE Count");
        Axis yAxis = new MockAxis(AxisType.RESULT, AxisName.Y1, "Application Time");
        Graph g1 = new Graph(xAxis, yAxis, AxisValueType.COUNT, Rank.UNKNOWN,
                new LineType(), methodData, series);
        
        xAxis = new MockAxis(AxisType.META_DATA, AxisName.X1, "Machine");
        yAxis = new MockAxis(AxisType.DERIVED, AxisName.Y1, new DerivedData("Avg Application Time", "avg( 'Application Time' )"));
        Graph g2 = new Graph(xAxis, yAxis, AxisValueType.VALUE, Rank.UNKNOWN,
                new LineType(), methodData, series);
        
        List<Graph> graphs = Arrays.asList(g1, g2);
        
        Analysis analysis = new Analysis(creator, date, notes, dataPrivate,
                graphs, ddList, restrictions, renames);
        
        long analysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long graphRows = TD.getRowCount(TestDatabase.Table.AnalysisGraph);
        long seriesRows = TD.getRowCount(TestDatabase.Table.AnalysisSeries);
        
        DB_CONNECTION.save(analysis);
    
        long newAnalysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long newGraphRows = TD.getRowCount(TestDatabase.Table.AnalysisGraph);
        long newSeriesRows = TD.getRowCount(TestDatabase.Table.AnalysisSeries);
        
        assertThat(newAnalysisRows, equalTo(analysisRows + 1));
        assertThat(newGraphRows, equalTo(graphRows + 2));
        assertThat(newSeriesRows, equalTo(seriesRows));
    }
    
    @Test
    public void saving_an_analysis_with_label_renames() throws Exception {
        List<DerivedData> ddList = Collections.emptyList();
        List<Restriction> restrictions = Collections.emptyList();
        List<Graph> graphs = Collections.emptyList();

        Pair<String, String> rename1 = new Pair<>("Application Time", "App Time");
        Pair<String, String> rename2 = new Pair<>("New Variable", "Avg App Time");
        List<Pair<String,String>> renames  = Arrays.asList(rename1, rename2);
        
        Analysis analysis = new Analysis(creator, date, notes, dataPrivate,
                graphs, ddList, restrictions, renames);
        
        long analysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long renameRows = TD.getRowCount(TestDatabase.Table.AnalysisAxisLabel);
        
        DB_CONNECTION.save(analysis);
    
        long newAnalysisRows = TD.getRowCount(TestDatabase.Table.Analysis);
        long newRenameRows = TD.getRowCount(TestDatabase.Table.AnalysisAxisLabel);
        
        assertThat(newAnalysisRows, equalTo(analysisRows + 1));
        assertThat(newRenameRows, equalTo(renameRows + 2));
    }
    
    @Test
    public void loading_an_empty_analysis_returns_an_empty_analysis_object() throws Exception {
        saving_an_empty_analysis_stores_it_in_the_Analysis_table();
        
        List<Analysis> analyses = DB_CONNECTION.getSavedAnalyses(creator);
        
        assertThat(analyses.size(), equalTo(1));
        
        Analysis analysis = analyses.get(0);
        
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
        
        assertThat(analysis.getCreator(), equalTo(creator));
        assertThat(df.format(analysis.getDate()), equalTo(df.format(date)));
        assertThat(analysis.getNotes(), equalTo(notes));
        assertThat(analysis.isDataPrivate(), equalTo(dataPrivate));
        assertThat(analysis.getGraphs().size(), equalTo(0));
        assertThat(analysis.getDerivedData().size(), equalTo(0));
        assertThat(analysis.getRestrictions().size(), equalTo(0));
        assertThat(analysis.getLabelRenames().size(), equalTo(0));
    }
    
    @Test
    public void loading_an_analysis_with_criteria_returns_an_analysis_object_with_restrictions() throws Exception {
        saving_an_analysis_with_restrictions_stores_the_analysis_and_analysis_criteria();
        
        List<Analysis> analyses = DB_CONNECTION.getSavedAnalyses(creator);
        
        assertThat(analyses.size(), equalTo(1));
        
        Analysis analysis = analyses.get(0);
        
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
        
        assertThat(analysis.getCreator(), equalTo(creator));
        assertThat(df.format(analysis.getDate()), equalTo(df.format(date)));
        assertThat(analysis.getNotes(), equalTo(notes));
        assertThat(analysis.isDataPrivate(), equalTo(dataPrivate));
        assertThat(analysis.getGraphs().size(), equalTo(0));
        assertThat(analysis.getDerivedData().size(), equalTo(0));
        assertThat(analysis.getRestrictions().size(), equalTo(3));
        assertThat(analysis.getLabelRenames().size(), equalTo(0));
    }
    
    @Test
    public void loading_an_analysis_with_derived_data_returns_an_analysis_object_with_derived_data() throws Exception {
        saving_an_analysis_with_derived_data_stores_the_analysis_and_data();
        
        List<Analysis> analyses = DB_CONNECTION.getSavedAnalyses(creator);
        
        assertThat(analyses.size(), equalTo(1));
        
        Analysis analysis = analyses.get(0);
        
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
        
        assertThat(analysis.getCreator(), equalTo(creator));
        assertThat(df.format(analysis.getDate()), equalTo(df.format(date)));
        assertThat(analysis.getNotes(), equalTo(notes));
        assertThat(analysis.isDataPrivate(), equalTo(dataPrivate));
        assertThat(analysis.getGraphs().size(), equalTo(0));
        assertThat(analysis.getDerivedData().size(), equalTo(2));
        assertThat(analysis.getRestrictions().size(), equalTo(0));
        assertThat(analysis.getLabelRenames().size(), equalTo(0));
    }
}
