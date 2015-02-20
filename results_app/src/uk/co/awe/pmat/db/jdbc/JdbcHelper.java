package uk.co.awe.pmat.db.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseObject;
import uk.co.awe.pmat.db.DerivedData;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.jdbc.JdbcMapping.Column;
import uk.co.awe.pmat.utils.Pair;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * A helper class which is used to create and perform the SQL queries needed by
 * the JdbcDatabaseConnection.
 * 
 * @author AWE Plc copyright 2013
 */
final class JdbcHelper {

	private final static Logger LOG = LoggerFactory.getLogger(JdbcHelper.class);
	private final static int MAX_RESULTS_PER_QUERY = 1000;
	private final static int MAX_PARAMS_PER_QUERY = 1000;

	private final String schema;
	private final String userName;
	private final String passWord;
	private final String url;

	private Connection connection;

	/**
	 * Create a new {@code JdbcHelper}.
	 * 
	 * @param schema
	 *            the database schema.
	 * @param userName
	 *            the database username.
	 * @param passWord
	 *            the database password.
	 * @param url
	 *            the connection URL for the database.
	 */
	JdbcHelper(String schema, String userName, String passWord, String url) {
		this.schema = schema;
		this.userName = userName;
		this.passWord = passWord;
		this.url = url;
	}

	/**
	 * Find the ID of the given object in the database using the objects
	 * business key.
	 * 
	 * @param dbObj
	 *            the object for which to get the ID.
	 * @return the ID for the given object.
	 * @throws DatabaseException
	 *             if an error occurs whilst performing the query.
	 */
	Long getId(DatabaseObject dbObj) throws DatabaseException {
        
        final JdbcTable table;

        if (dbObj instanceof MetaData) {
            final MetaData metaData = (MetaData) dbObj;
            table = JdbcUtils.typeToTable(metaData.getType());
        } else if (dbObj instanceof Run) {
            table = JdbcTable.RUN;
        } else if (dbObj instanceof Analysis) {
            table = JdbcTable.ANALYSIS;
        } else {
            throw new IllegalArgumentException("Unknown DatabaseObject type "
                    + dbObj.getClass());
        }
        
        final List<String> clauses = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        
        Collection<String> keys = JdbcMapping.getBusinessKey(table);
        if (table == JdbcTable.RUN && ((Run) dbObj).getRunId() == null) {
            keys = Arrays.asList("runId", "creator", "runDate", "tag", "file");
        }
        
        for (String fieldName : keys) {
            try {
                final Field field = dbObj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                final Object val = field.get(dbObj);
                clauses.add(table.equals(fieldName, val));
                if (val != null) {
                    vals.add(val);
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                throw new DatabaseException("Failed to get id for database object " + dbObj, ex);
            }
        }
        
        final String query = MessageFormat.format("SELECT ID FROM {0} WHERE {1}",
        /* 0 */ schema + "." + table.tableSelect(),
        /* 0 */ StringUtils.joinStrings(clauses, " AND "));

        try {
            final ResultSet resultSet = executeQuery(query, vals);
            
            Long id = null;
            while (resultSet.next()) {
                if (id != null) {
                    throw new DatabaseException("Multiple ids found for DatabaseObject " + dbObj);
                }
                id = resultSet.getLong("ID");
            }
            
            return id;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

	/**
	 * Delete the rows from the given database table with the given IDs.
	 * 
	 * @param table
	 *            the table from which to delete the rows.
	 * @param joinTable
	 *            table to join in order to perform where clause.
	 * @param ids
	 *            the IDs of the tables to delete.
	 * @throws SQLException
	 *             if there is an error in the generated SQL.
	 */
	void deleteFromTable(JdbcTable table, JdbcTable joinTable, List<Long> ids) throws SQLException {
        final List<String> placeHolders = new ArrayList<>(ids.size());
        for (int idx = 0; idx < ids.size(); ++ idx) {
            placeHolders.add("?");
        }
        
        String query = "DELETE FROM {0} WHERE {1} IN ({2})";
        query = MessageFormat.format(query,
        /* 0 */ schema + "." + table.tableName(),
        /* 1 */ JdbcMapping.joins.get(joinTable).get(table).name(),
        /* 2 */ StringUtils.joinStrings(placeHolders, ", "));

        executeUpdate(query, ids);
    }

	/**
	 * Save the flags of the given {@code Run}.
	 * 
	 * @param run
	 *            the run for whose flags we are saving.
	 * @param runId
	 *            the database id of the run.
	 * @throws SQLException
	 *             if an error occurs running the generated SQL.
	 */
	private void saveFlags(final Run run, Long runId) throws SQLException {
        if (run.getFlags().isEmpty()) {
            return;
        }
        
        // Remove flag duplicates as the database cannot handle them currently.
        // TODO: Fix this.
        final List<String> flags = new ArrayList<>(new HashSet<>(run.getFlags()));
        final Map<String, Long> flagIds = new HashMap<>();
        
        final List<String> placeHolders = new ArrayList<>(flags.size());
        for (int i = 0; i < flags.size(); ++i) {
            placeHolders.add("?");
        }
        
        String query = "SELECT {0} AS flag, {1}.ID AS ID FROM {2} WHERE {0} IN ({3})";
        query = MessageFormat.format(query,
        /* 0 */ JdbcTable.FLAGS.tableColumn("flag"),
        /* 1 */ JdbcTable.FLAGS.tableReference(),
        /* 2 */ schema + "." + JdbcTable.FLAGS.tableSelect(),
        /* 3 */ StringUtils.joinStrings(placeHolders, ", "));
        
        ResultSet resultSet = executeQuery(query, flags);
        
        while (resultSet.next()) {
            flagIds.put(resultSet.getString("flag"), resultSet.getLong("ID"));
        }
        
        final List<String> unsavedFlags = new ArrayList<>();
        placeHolders.clear();
        
        for (String flag : flags) {
            if (!flagIds.containsKey(flag)) {
                unsavedFlags.add(flag);
                placeHolders.add("(?)");
            }
        }
        
        if (!unsavedFlags.isEmpty()) {
            query = "INSERT INTO {0} ({1}) VALUES {2}";
            query = MessageFormat.format(query,
            /* 0 */ schema + "." + JdbcTable.FLAGS.tableName(),
            /* 1 */ JdbcMapping.getTableMap(JdbcTable.FLAGS).get("flag").name(),
            /* 2 */ StringUtils.joinStrings(placeHolders, ", "));
            
            executeUpdate(query, unsavedFlags);
            
            placeHolders.clear();
            for (int i = 0; i < unsavedFlags.size(); ++i) {
                placeHolders.add("?");
            }

            query = "SELECT {0} AS flag, {1}.ID AS ID FROM {2} WHERE {0} IN ({3})";
            query = MessageFormat.format(query,
            /* 0 */ JdbcTable.FLAGS.tableColumn("flag"),
            /* 1 */ JdbcTable.FLAGS.tableReference(),
            /* 2 */ schema + "." + JdbcTable.FLAGS.tableSelect(),
            /* 3 */ StringUtils.joinStrings(placeHolders, ", "));

            resultSet = executeQuery(query, unsavedFlags);

            while (resultSet.next()) {
                flagIds.put(resultSet.getString("flag"), resultSet.getLong("ID"));
            }
        }
        
        final List<Object> vals = new ArrayList<>();
        
        for (String flag : flags) {
            vals.add(flagIds.get(flag));
            vals.add(runId);
        }
        
        placeHolders.clear();
        for (int i = 0; i < flags.size(); ++i) {
            placeHolders.add("(?, ?)");
        }
            
        query = "INSERT INTO {0} ({1}, {2}) VALUES {3}";
        query = MessageFormat.format(query,
        /* 0 */ schema + "." + JdbcTable.RUN_FLAGS.tableName(),
        /* 1 */ JdbcMapping.getTableMap(JdbcTable.RUN_FLAGS).get("flagId").name(),
        /* 2 */ JdbcMapping.getTableMap(JdbcTable.RUN_FLAGS).get("runId").name(),
        /* 3 */ StringUtils.joinStrings(placeHolders, ", "));

        executeUpdate(query, vals);
    }

	/**
	 * Save the given meta data into the database.
	 * 
	 * @param metaData
	 *            the meta data to save.
	 * @return the connection used to save the meta data.
	 * @throws SQLException
	 *             if an error occurs performing the generated SQL.
	 */
	Connection saveMetaData(final MetaData metaData) throws SQLException {
        
        final JdbcTable table = JdbcUtils.typeToTable(metaData.getType());
        final List<String> cols = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> placeHolders = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : metaData.getDataMap().entrySet()) {
            placeHolders.add("?");
            vals.add(entry.getValue());
            cols.add(JdbcMapping.getTableMap(table).get(entry.getKey()).name());
        }
        
        String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ getSchema() + "." + table.tableName(),
        /* 1 */ StringUtils.joinStrings(cols, ", "),
        /* 2 */ "(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
        
        return executeUpdate(query, vals);
    }

	/**
	 * Save the given {@code Run} into the database. This involves saving the
	 * run meta data if it does not already exist in the database, and then
	 * saving the run itself before finally saving all the run data associated
	 * with the run.
	 * 
	 * @param run
	 *            the run to save.
	 * @return the connection used to save the run.
	 * @throws SQLException
	 *             if an error occurs performing the generated SQL.
	 * @throws DatabaseException
	 *             if an error occurs saving the meta data.
	 */
	Connection saveRun(final Run run) throws SQLException, DatabaseException {
               
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> fields = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        final Map<MetaData.Type, Long> metaIds = new EnumMap<>(MetaData.Type.class);
        for (MetaData.Type type : MetaData.Type.values()) {
            final MetaData metaData = run.getMetaData(type);
            Long id = getId(metaData);
            if (id == null) {
                saveMetaData(run.getMetaData(type));
                id = getId(metaData);
            }
            if (id == null) {
                throw new DatabaseException("Failed to find meta data ID for " + metaData);
            }
            metaIds.put(type, id);
        }

        for (MetaData.Type type : MetaData.Type.values()) {
            final JdbcTable table = JdbcUtils.typeToTable(type);
            names.add(JdbcMapping.joins.get(table).get(JdbcTable.RUN).insertColumns());
            placeHolders.add(JdbcMapping.joins.get(table).get(JdbcTable.RUN).placeHolder());
            vals.add(metaIds.get(type));
        }
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.RUN).entrySet()) {
            fields.add(entry.getKey());
            names.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        for (String field : fields) {
            switch (field) {
                case "creator": vals.add(run.getCreator()); break;
                case "file": vals.add(run.getFile()); break;
                case "insertionDate": vals.add(new Date()); break;
                case "runDate": vals.add(run.getRunDate()); break;
                case "runId": vals.add(run.getRunId()); break;
                case "tag": vals.add(run.getTag()); break;
                case "restricted": vals.add(run.isRestricted()); break;
                default:
                    throw new IllegalStateException("Unknown run field " + field);
            }
        }
        
        final String runQuery = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ schema + "." + JdbcTable.RUN.tableName(),
        /* 1 */ StringUtils.joinStrings(names, ", "),
        /* 2 */ "(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
        
        final Connection conn = executeUpdate(runQuery, vals);

        Long runId = getId(run);
        saveFlags(run, runId);

        if (!run.getDataSets().isEmpty()) {
            saveDataSets(run, runId);
        }
        
        return conn;
    }

	private void saveResults(Map<Long, List<Value<Double>>> subRunResults) throws SQLException {
        final List<String> names = new ArrayList<>();
        final List<String> columns = new ArrayList<>();
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.RESULT).entrySet()) {
            names.add(entry.getKey());
            columns.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        names.add("subRun");
        columns.add(JdbcMapping.joins.get(JdbcTable.SUB_RUN).get(JdbcTable.RESULT).insertColumns());
        placeHolders.add(JdbcMapping.joins.get(JdbcTable.SUB_RUN).get(JdbcTable.RESULT).placeHolder());

        final String resultPlaceHolder = "(" + StringUtils.joinStrings(placeHolders, ", ") + ")";
        placeHolders.clear();
        
        for (Map.Entry<Long, List<Value<Double>>> entry : subRunResults.entrySet()) {
            final Long subRunId = entry.getKey();
            final List<Value<Double>> results = entry.getValue();
            for (Value<?> result : results) {
                placeHolders.add(resultPlaceHolder);
                for (String name : names) {
                    switch (name) {
                        case "name":
                            vals.add(result.getName());
                            break;
                        case "rank":
                            vals.add(result.getRank().asInteger());
                            break;
                        case "value":
                            vals.add(result.getValue());
                            break;
                        case "error":
                            vals.add(result.getError());
                            break;
                        case "errorType":
                            vals.add(result.getErrorType().name());
                            break;
                        case "count":
                            vals.add(result.getCount());
                            break;
                        case "pauseCount":
                            vals.add(result.getPauseCount());
                            break;
                        case "subRun":
                            vals.add(subRunId);
                            break;
                        default:
                            throw new IllegalStateException("Unknown parameter field " + name);
                    }
                }
            }
        }
        
        // Insert results MAX_RESULTS_PER_QUERY at a time to avoid overfilling
        // the query buffer.
        int fromIdx = 0;
        final int numHolders = resultPlaceHolder.split("\\?").length - 1;

        do {
            int toIdx = Math.min(fromIdx + MAX_RESULTS_PER_QUERY * numHolders, vals.size());
            
            String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
            /* 0 */ schema + "." + JdbcTable.RESULT.tableName(),
            /* 1 */ StringUtils.joinStrings(columns, ", "),
            /* 2 */ StringUtils.joinStrings(placeHolders.subList(fromIdx / numHolders, toIdx / numHolders), ", "));

            executeUpdate(query, vals.subList(fromIdx, toIdx));
        
            fromIdx = toIdx;
            
        } while (vals.size() - fromIdx > 0);
    }

	private void saveParameters(Map<Long, List<Value<?>>> subRunParams) throws SQLException {
        final List<String> names = new ArrayList<>();
        final List<String> columns = new ArrayList<>();
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.PARAMETER).entrySet()) {
            names.add(entry.getKey());
            columns.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        names.add("subRun");
        columns.add(JdbcMapping.joins.get(JdbcTable.SUB_RUN).get(JdbcTable.PARAMETER).insertColumns());
        placeHolders.add(JdbcMapping.joins.get(JdbcTable.SUB_RUN).get(JdbcTable.PARAMETER).placeHolder());

        final String paramPlaceHolder = "(" + StringUtils.joinStrings(placeHolders, ", ") + ")";
        placeHolders.clear();
        
        for (Map.Entry<Long, List<Value<?>>> entry : subRunParams.entrySet()) {
            final Long subRunId = entry.getKey();
            final List<Value<?>> params = entry.getValue();
            for (Value<?> param : params) {
                placeHolders.add(paramPlaceHolder);
                for (String name : names) {
                    switch (name) {
                        case "name":
                            vals.add(param.getName());
                            break;
                        case "rank":
                            vals.add(param.getRank().asInteger());
                            break;
                        case "value":
                            Object value = param.getValue();
                            vals.add(value.getClass().getSimpleName());
                            vals.add(value instanceof Integer ? value : null);
                            vals.add(value instanceof Double ? value : null);
                            vals.add(value instanceof String ? value : null);
                            break;
                        case "subRun":
                            vals.add(subRunId);
                            break;
                        default:
                            throw new IllegalStateException("Unknown parameter field " + name);
                    }
                }
            }
        }
        
        // Insert parameters MAX_PARAMS_PER_QUERY at a time to avoid overfilling
        // the query buffer.
        int fromIdx = 0;
        final int numHolders = paramPlaceHolder.split("\\?").length - 1;
        
        do {
            int toIdx = Math.min(fromIdx + MAX_PARAMS_PER_QUERY * numHolders, vals.size());
            
            String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
            /* 0 */ schema + "." + JdbcTable.PARAMETER.tableName(),
            /* 1 */ StringUtils.joinStrings(columns, ", "),
            /* 2 */ StringUtils.joinStrings(placeHolders.subList(fromIdx / numHolders, toIdx / numHolders), ", "));

            executeUpdate(query, vals.subList(fromIdx, toIdx));
        
            fromIdx = toIdx;
            
        } while (vals.size() - fromIdx > 0);
    }

	private void saveDataSets(final Run run, Long runId) throws SQLException {
        
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> subRunVals = new ArrayList<>();
        
        final Map<Long, List<Value<?>>> params = new HashMap<>();
        final Map<Long, List<Value<Double>>> results = new HashMap<>();
        
        final List<RunData> dataSets = run.getDataSets();
        for (int sequence = 0; sequence < dataSets.size(); ++sequence) {
            subRunVals.add(runId);
            subRunVals.add(sequence);
            placeHolders.add("(?, ?)");
            
            params.put((long) sequence, dataSets.get(sequence).getParameters());
            results.put((long) sequence, dataSets.get(sequence).getResults());
        }
        
        String query = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES {3}",
        /* 0 */ schema + "." + JdbcTable.SUB_RUN.tableName(),
        /* 1 */ JdbcMapping.getTableMap(JdbcTable.SUB_RUN).get("run").name(),
        /* 2 */ JdbcMapping.getTableMap(JdbcTable.SUB_RUN).get("sequence").name(),
        /* 3 */ StringUtils.joinStrings(placeHolders, ", "));
        
        executeUpdate(query, subRunVals);
        
        query = MessageFormat.format("SELECT {0}.ID AS id, {1} AS sequence FROM {2} WHERE {3}",
                /* 0 */ JdbcTable.SUB_RUN.tableReference(),
                /* 1 */ JdbcTable.SUB_RUN.tableColumn("sequence"),
                /* 2 */ schema + "." + JdbcTable.SUB_RUN.tableSelect(),
                /* 1 */ JdbcTable.SUB_RUN.equals("run", runId));
        
        Map<Long, List<Value<?>>> subRunParams = new HashMap<>();
        Map<Long, List<Value<Double>>> subRunResults = new HashMap<>();
        
        ResultSet resultSet = executeQuery(query, runId);
        
        while (resultSet.next()) {
            Long subRunId = resultSet.getLong("id");
            Long sequence = resultSet.getLong("sequence");
            
            subRunParams.put(subRunId, params.get(sequence));
            subRunResults.put(subRunId, results.get(sequence));
        }
        
        saveParameters(subRunParams);
        saveResults(subRunResults);
    }

	private Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(url, userName, passWord);
			connection.setAutoCommit(false);
		}
		return connection;
	}

	String getSchema() {
		return schema;
	}

	ResultSet executeQuery(String query, Object... params) throws SQLException {
		return executeQuery(query, Arrays.asList(params));
	}

	ResultSet executeQuery(String query, Collection<?> params)
			throws SQLException {
		LOG.debug("SQL:> " + query);

		final Connection conn = getConnection();
		final PreparedStatement statement = conn.prepareStatement(query);
		statement.setFetchSize(10000);

		int idx = 1;
		for (Object param : params) {
			statement.setObject(idx, param);
			++idx;
		}

		final ResultSet resultSet = statement.executeQuery();

		// final ClassLoader classLoader = this.getClass().getClassLoader();
		// Create a proxy that cleans up the statement and connection after it
		// is closed.
		return resultSet;
		// return (ResultSet) Proxy.newProxyInstance(classLoader,
		// new Class<?>[] {ResultSet.class},
		// new InvocationHandler() {
		// @Override
		// public Object invoke(Object proxy, Method method, Object[] args)
		// throws Throwable {
		// switch (method.getName()) {
		// case "close":
		// method.invoke(resultSet, args);
		// statement.close();
		// conn.close();
		// return null;
		// default:
		// return method.invoke(resultSet, args);
		// }
		// }
		// });
	}

	Connection executeUpdate(String query, Object... params)
			throws SQLException {
		return executeUpdate(query, Arrays.asList(params));
	}

	Connection executeUpdate(String query, Collection<?> params) throws SQLException {
        LOG.debug("SQL:> " + query);
        
        Connection conn = getConnection();
        try (final PreparedStatement statement = conn.prepareStatement(query)) {
            int idx = 1;
            for (Object param : params) {
                statement.setObject(idx, param);
                ++idx;
            }
            statement.executeUpdate();
        } catch (SQLException ex) {
            if (conn != null) { conn.rollback(); }
            throw ex;
        }
        
        return conn;
    }

	void closeConnection() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	Connection saveAnalysis(Analysis analysis) throws SQLException, DatabaseException {
        
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> fields = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.ANALYSIS).entrySet()) {
            fields.add(entry.getKey());
            names.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        for (String field : fields) {
            switch (field) {
                case "creator": vals.add(analysis.getCreator()); break;
                case "notes": vals.add(analysis.getNotes()); break;
                case "date": vals.add(analysis.getDate()); break;
                case "dataPrivate": vals.add(analysis.isDataPrivate()); break;
                default:
                    throw new IllegalStateException("Unknown analysis field " + field);
            }
        }
        
        final String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ schema + "." + JdbcTable.ANALYSIS.tableName(),
        /* 1 */ StringUtils.joinStrings(names, ", "),
        /* 2 */ "(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
        
        final Connection conn = executeUpdate(query, vals);
        
        Long analysisId = getId(analysis);

        if (!analysis.getRestrictions().isEmpty()) {
            saveRestrictions(analysis, analysisId);
        }
        
        if (!analysis.getDerivedData().isEmpty()) {
            saveDerivedData(analysis, analysisId);
        }
        
        if (!analysis.getGraphs().isEmpty()) {
            saveAnalysisGraph(analysis, analysisId);
        }
        
        if (!analysis.getLabelRenames().isEmpty()) {
            saveAnalysisRenames(analysis, analysisId);
        }
        
        return conn;
    }

	private void saveRestrictions(Analysis analysis, Long analysisId) throws SQLException, DatabaseException {
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> fields = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.ANALYSIS_CRITERA).entrySet()) {
            fields.add(entry.getKey());
            names.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        final List<String> retrictPlaceHolders = new ArrayList<>();
        long sequence = 0;
        
        for (Restriction restriction : analysis.getRestrictions()) {
            for (String field : fields) {
                final String value;
                final String valueType;
                if (restriction.getValue().getValue() == null) {
                    value = null;
                    valueType = Void.class.getSimpleName();
                } else {
                    value = restriction.getValue().getValue().toString();
                    valueType = restriction.getValue().getValue().getClass().getSimpleName();
                }
                switch (field) {
                    case "analysisId": vals.add(analysisId); break;
                    case "sequence": vals.add(sequence); break;
                    case "category": vals.add(restriction.getCategory().name()); break;
                    case "field": vals.add(restriction.getField()); break;
                    case "rank": vals.add(restriction.getRank().asInteger()); break;
                    case "comparator": vals.add(restriction.getComparator().name()); break;
                    case "value": vals.add(value); break;
                    case "valueType": vals.add(valueType); break;
                    default:
                        throw new IllegalStateException("Unknown analysis criteria field " + field);
                }
            }
            retrictPlaceHolders.add("(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
            ++sequence;
        }
        
        final String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ schema + "." + JdbcTable.ANALYSIS_CRITERA.tableName(),
        /* 1 */ StringUtils.joinStrings(names, ", "),
        /* 2 */ StringUtils.joinStrings(retrictPlaceHolders, ", "));
        
        executeUpdate(query, vals);
    }

	private void saveDerivedData(Analysis analysis, Long analysisId) throws SQLException {
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> fields = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.ANALYSIS_DERIVED_DATA).entrySet()) {
            fields.add(entry.getKey());
            names.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        final List<String> ddPlaceHolders = new ArrayList<>();
        
        for (DerivedData dd : analysis.getDerivedData()) {
            for (String field : fields) {
                switch (field) {
                    case "analysisId": vals.add(analysisId); break;
                    case "name": vals.add(dd.getName()); break;
                    case "equation": vals.add(dd.getEquation()); break;
                    default:
                        throw new IllegalStateException("Unknown analysis criteria field " + field);
                }
            }
            ddPlaceHolders.add("(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
        }
        
        final String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ schema + "." + JdbcTable.ANALYSIS_DERIVED_DATA.tableName(),
        /* 1 */ StringUtils.joinStrings(names, ", "),
        /* 2 */ StringUtils.joinStrings(ddPlaceHolders, ", "));
        
        executeUpdate(query, vals);
    }

	private void saveAnalysisGraph(Analysis analysis, Long analysisId) throws SQLException {
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> fields = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.ANALYSIS_GRAPH).entrySet()) {
            fields.add(entry.getKey());
            names.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        final List<String> graphPlaceHolders = new ArrayList<>();
        
        for (Graph graph : analysis.getGraphs()) {
            for (String field : fields) {
                switch (field) {
                    case "analysisId": vals.add(analysisId); break;
                    case "xAxisName": vals.add(graph.getxAxis().getAxisName().name()); break;
                    case "yAxisName": vals.add(graph.getyAxis().getAxisName().name()); break;
                    case "xAxisType": vals.add(graph.getxAxis().getType().name()); break;
                    case "yAxisType": vals.add(graph.getxAxis().getType().name()); break;
                    case "axisValueType": vals.add(graph.getyAxisType().name()); break;
                    case "rank": vals.add(Rank.UNKNOWN.asInteger()); break;
                    case "lineStyle": vals.add(graph.getLineType().getStyle().name()); break;
                    case "lineColour": vals.add(graph.getLineType().getColour().name()); break;
                    case "lineWidth": vals.add(graph.getLineType().getWidth()); break;
                    default:
                        throw new IllegalStateException("Unknown analysis criteria field " + field);
                }
            }
            graphPlaceHolders.add("(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
        }
        
        final String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ schema + "." + JdbcTable.ANALYSIS_GRAPH.tableName(),
        /* 1 */ StringUtils.joinStrings(names, ", "),
        /* 2 */ StringUtils.joinStrings(graphPlaceHolders, ", "));
        
        executeUpdate(query, vals);
    }

	private void saveAnalysisRenames(Analysis analysis, Long analysisId) throws SQLException {
        final List<String> placeHolders = new ArrayList<>();
        final List<Object> vals = new ArrayList<>();
        final List<String> fields = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : JdbcMapping.getTableMap(JdbcTable.ANALYSIS_AXIS_LABEL).entrySet()) {
            fields.add(entry.getKey());
            names.add(entry.getValue().insertColumns());
            placeHolders.add(entry.getValue().placeHolder());
        }
        
        final List<String> renamePlaceHolders = new ArrayList<>();
        
        for (Pair<String, String> rename : analysis.getLabelRenames()) {
            for (String field : fields) {
                switch (field) {
                    case "analysisId": vals.add(analysisId); break;
                    case "oldText": vals.add(rename.getFirst()); break;
                    case "newText": vals.add(rename.getSecond()); break;
                    default:
                        throw new IllegalStateException("Unknown analysis criteria field " + field);
                }
            }
            renamePlaceHolders.add("(" + StringUtils.joinStrings(placeHolders, ", ") + ")");
        }
        
        final String query = MessageFormat.format("INSERT INTO {0} ({1}) VALUES {2}",
        /* 0 */ schema + "." + JdbcTable.ANALYSIS_AXIS_LABEL.tableName(),
        /* 1 */ StringUtils.joinStrings(names, ", "),
        /* 2 */ StringUtils.joinStrings(renamePlaceHolders, ", "));
        
        executeUpdate(query, vals);
    }
}
