package uk.co.awe.pmat.db;

import java.util.List;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class RestrictionsTest {
    
    private static DatabaseConnection DB_CONNECTION;
    private static TestDatabase td;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DB_CONNECTION = new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST));
        td = new TestDatabase();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DB_CONNECTION = null;
        td = null;
    }
    
    @Before
    public void setUp() throws Exception {
        td.loadData();
    }
    
    @After
    public void tearDown() throws Exception {
        td.clearData();
    }

    @Test
    public void resticting_by_parameter_returns_correct_ids() throws Exception {
        
        String paramName = "PE Count";
        Value<?> paramValue = new Value<>(paramName, Rank.UNKNOWN, 8);
        Rank paramRank = Rank.UNKNOWN;
        
        String query = String.format("SELECT DISTINCT sr.ID FROM SubRun sr"
                + " JOIN Parameter p ON p.SubRunOwner = sr.ID"
                + " WHERE p.Name = '%s' AND p.IntegerValue = %d", paramName, paramValue.getValue());
        List<Long> expIds = td.executeQueryListLong(query);
        
        Restriction restriction = DB_CONNECTION.newRestriction(Category.PARAMETER, paramName, paramRank, Comparator.EQ, paramValue);

        assertThat(restriction.getMatchingIDs(), equalTo(expIds));
    }
    
    @Test
    public void resticting_by_result_returns_correct_ids() throws Exception {
        
        String resultName = "Application Time";
        Value<?> resultValue = new Value<>(resultName, Rank.UNKNOWN, 1567.954);
        Rank resultRanl = Rank.UNKNOWN;
        
        String query = String.format("SELECT DISTINCT sr.ID FROM SubRun sr"
                + " JOIN Result r ON r.SubRun = sr.ID"
                + " WHERE r.Name = '%s' AND r.Value = %f", resultName, resultValue.getValue());
        List<Long> expIds = td.executeQueryListLong(query);
        
        Restriction restriction = DB_CONNECTION.newRestriction(Category.RESULT, resultName, resultRanl, Comparator.EQ, resultValue);

        assertThat(restriction.getMatchingIDs(), equalTo(expIds));
    }
}
