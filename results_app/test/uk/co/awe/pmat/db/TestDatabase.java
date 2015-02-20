package uk.co.awe.pmat.db;

import java.sql.Statement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class TestDatabase {
 
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");

    public enum Table {
        Analysis,
        Run,
        SubRun,
        Parameter,
        Result,
        Application,
        Compiler,
        Machine,
        MPI,
        OperatingSystem,
        Processor,
        Flags,
        RunFlags,
        AnalysisCriteria,
        AnalysisDerivedData,
        AnalysisGraph,
        AnalysisSeries,
        AnalysisAxisLabel,
    }

    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://%MYSQL_ADDRESS%", "%TEST_USERNAME%", "%TEST_PASSWORD%");
        connection.setAutoCommit(false);
        return connection;
    }
    
    private Object[] getRow(ResultSet resultSet, int rowNum) throws SQLException {
        resultSet.absolute(rowNum + 1);
        
        List<Object> cols = new ArrayList<>();
        int colIdx = 1;
        while (true) {
            try {
                cols.add(resultSet.getObject(colIdx));
                ++colIdx;
            } catch (SQLException ex) {
                break;
            }
        }
        
        return cols.toArray();
    }
    
    public void loadData() throws SQLException, URISyntaxException, FileNotFoundException, IOException {
  
        URL url = ClassLoader.getSystemResource("insertTestData.sql");
        File sqlFile = new File(url.toURI());
        
        BufferedReader in = new BufferedReader(new FileReader(sqlFile));
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("use performance_test");
            
            StringBuilder sql = new StringBuilder();
            while (in.ready()) {
                String line = in.readLine().trim();
                sql.append(line);
                if (line.contains(";")) {
                    statement.executeUpdate(sql.toString());
                    sql.setLength(0);
                }
            }
            
            connection.commit();
        }
    }
    
    public void clearData() throws SQLException, URISyntaxException, FileNotFoundException, IOException {
        
        URL url = ClassLoader.getSystemResource("dropTestData.sql");
        File sqlFile = new File(url.toURI());
        
        BufferedReader in = new BufferedReader(new FileReader(sqlFile));
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("use performance_test");
            
            StringBuilder sql = new StringBuilder();
            while (in.ready()) {
                String line = in.readLine().trim();
                sql.append(line);
                if (line.contains(";")) {
                    statement.executeUpdate(sql.toString());
                    sql.setLength(0);
                }
            }
            
            connection.commit();
        }
    }
    
    public List<Long> executeQueryListLong(String sql) throws SQLException {
        List<Long> list;
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("use performance_test");
            ResultSet resultSet = statement.executeQuery(sql);
            list = new ArrayList<>();
            if (resultSet.first()) {
                do {
                    list.add(resultSet.getLong(1));
                } while (resultSet.next());
            }
            connection.commit();
        }
        
        return list;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T executeQuery(String sql, Class<T> clz) throws SQLException {
        T result;
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("use performance_test");
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.first();
            result = (T) resultSet.getObject(1);
            connection.commit();
        }
        
        return result;
    }
    
    public int getRowCount(Table table) throws SQLException {
        return executeQuery("SELECT COUNT(*) FROM " + table, Long.class).intValue();
    }
    
    @Test
    public void testLoadAndClear() throws Exception {
        TestDatabase testDB = new TestDatabase();
        
        try {
            testDB.loadData();
        } finally {
            testDB.clearData();
            for (Table table : Table.values()) {
                assertThat(getRowCount(table), equalTo(0));
            }
        }
    }

}
