package uk.co.awe.pmat.db.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.DatabaseConnection;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseObject;
import uk.co.awe.pmat.db.DerivedData;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.MetaData.Type;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.db.jdbc.JdbcMapping.Column;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.db.series.SeriesType;
import uk.co.awe.pmat.utils.DefaultHashMap;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public final class JdbcDatabaseConnection implements DatabaseConnection {

	private final static Logger LOG = LoggerFactory
			.getLogger(JdbcDatabaseConnection.class);

	private final JdbcCache cache = new JdbcCache();
	private final JdbcHelper helper;

	public JdbcDatabaseConnection(Configuration configuration) {
		helper = new JdbcHelper(configuration
				.getProperty(Configuration.Key.DB_SCHEMA), configuration
				.getProperty(Configuration.Key.DB_USERNAME), configuration
				.getProperty(Configuration.Key.DB_PASSWORD), configuration
				.getProperty(Configuration.Key.DB_CONNECTION_URL));
	}

	@Override
	public void close() {
		try {
			helper.closeConnection();
		} catch (SQLException ex) {
			LOG.error("Error closing connection", ex);
		}
	}

	@Override
	public Restriction newRestriction(Category category, String field,
			Rank rank, Comparator comparator, Value<?> value) {
		return new JdbcRestriction(helper, category, field, rank, comparator,
				value);
	}

	@Override
    public List<String> getFields(Collection<Restriction> restrictions, Category category) throws DatabaseException {
        
        final List<String> fields = new ArrayList<>();
        JdbcTable table = null;

        switch (category) {
            case APPLICATION: // Fall through
            case COMPILER: // Fall through
            case MACHINE: // Fall through
            case MPI: // Fall through
            case OPERATING_SYSTEM: // Fall through
            case PROCESSOR: // Fall through
            case RUN:
                fields.addAll(JdbcMapping.getTableMap(JdbcUtils.categoryToTable(category)).keySet());
                break;
            case PARAMETER:
                table = JdbcTable.PARAMETER;
                break;
            case RESULT:
                table = JdbcTable.RESULT;
                break;
        }

        if (table != null) {
            String query = "SELECT DISTINCT {0}.Name AS name FROM {1}";
            query = MessageFormat.format(query,
                    table.tableReference(),
                    helper.getSchema() + "." + table.tableSelect());
            
            final List<Object> sqlParams = new ArrayList<>();
            if (!restrictions.isEmpty()) {
                query += " JOIN " + helper.getSchema() + "." + table.joinLeft(JdbcTable.SUB_RUN)
                        + " JOIN " + helper.getSchema() + "." + JdbcTable.SUB_RUN.joinLeft(JdbcTable.RUN)
                        + " WHERE " + JdbcUtils.subRunRestriction(restrictions, sqlParams);
            }

            if (cache.contains(query, sqlParams)) {
                @SuppressWarnings("unchecked")
                final List<String> cachedResults = (List<String>) cache.get(query, sqlParams);
                return cachedResults;
            }
            
            try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
                while (resultSet.next()) {
                    fields.add(resultSet.getString("name"));
                }
                cache.put(query, sqlParams, fields);
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }
        
        return fields;
    }

	@Override
    public List<Rank> getRanks(List<Restriction> restrictions, Category category, String field) throws DatabaseException {
        
        final List<Rank> ranks = new ArrayList<>();
        final List<Object> sqlParams = new ArrayList<>();
        
        JdbcTable table = null;
            
        switch (category) {
            case APPLICATION: // Fall through
            case COMPILER: // Fall through
            case MACHINE: // Fall through
            case MPI: // Fall through
            case OPERATING_SYSTEM: // Fall through
            case PROCESSOR: // Fall through
            case RUN:
                ranks.add(Rank.ANY_RANK);
                break;
            case PARAMETER:
                table = JdbcTable.PARAMETER;
                break;
            case RESULT:
                table = JdbcTable.RESULT;
                break;
            default:
                throw new IllegalArgumentException("Uknown category " + category);
        }

        if (table != null) {
            String query = "SELECT DISTINCT {0}.Rank AS rank FROM {1} JOIN {2} WHERE {3} AND {0}.Name = ?";
            query = MessageFormat.format(query,
            /* 0 */ table.tableReference(),
            /* 1 */ helper.getSchema() + "." + table.tableSelect(),
            /* 2 */ helper.getSchema() + "." + table.joinLeft(JdbcTable.SUB_RUN),
            /* 3 */ JdbcUtils.subRunRestriction(restrictions, sqlParams));
            sqlParams.add(field);
            
            if (cache.contains(query, sqlParams)) {
                @SuppressWarnings("unchecked")
                final List<Rank> cachedResults = (List<Rank>) cache.get(query, sqlParams);
                return cachedResults;
            }
            
            try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
                while (resultSet.next()) {
                    ranks.add(Rank.fromInteger(resultSet.getInt("rank")));
                }
                
                cache.put(query, sqlParams, ranks);
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }

        return ranks;
    }

	@Override
    public List<Value<?>> getValues(Collection<Restriction> restrictions, Category category, String field, Rank rank) throws DatabaseException {
        final List<Value<?>> values = new ArrayList<>();
        final List<Object> sqlParams = new ArrayList<>();
        
        final JdbcTable table = JdbcUtils.categoryToTable(category);
        
        String query = "SELECT DISTINCT {0} FROM {1}";
        switch (category) {
            case APPLICATION:
            case COMPILER:
            case MACHINE:
            case MPI:
            case OPERATING_SYSTEM:
            case PROCESSOR:
                query = MessageFormat.format(query,
                /* 0 */ JdbcMapping.getTableMap(table).get(field).select(),
                /* 1 */ helper.getSchema() + "." + table.tableSelect());
                if (!restrictions.isEmpty()) {
                    query += " JOIN " + helper.getSchema() + "." + table.joinRight(JdbcTable.RUN);
                    query += " JOIN " + helper.getSchema() + "." + JdbcTable.RUN.joinRight(JdbcTable.SUB_RUN);
                    query += " WHERE " + JdbcUtils.subRunRestriction(restrictions, sqlParams);
                }
                break;
            case RUN:
                query = MessageFormat.format(query,
                /* 0 */ JdbcMapping.getTableMap(table).get(field).select(),
                /* 1 */ helper.getSchema() + "." + table.tableSelect());
                if (!restrictions.isEmpty()) {
                    query += " JOIN " + helper.getSchema() + "." + JdbcTable.RUN.joinRight(JdbcTable.SUB_RUN);
                    query += " WHERE " + JdbcUtils.subRunRestriction(restrictions, sqlParams);
                }
                break;
            case PARAMETER:
            case RESULT:
                query = MessageFormat.format(query,
                /* 0 */ JdbcMapping.getTableMap(table).get("value").select(),
                /* 1 */ helper.getSchema() + "." + table.tableSelect());
                if (!restrictions.isEmpty()) {
                    query += " JOIN " + helper.getSchema() + "." + table.joinLeft(JdbcTable.SUB_RUN);
                    query += " WHERE " + JdbcUtils.subRunRestriction(restrictions, sqlParams);
                    query += " AND " + table.equals("name", field);
                } else {
                    query += " WHERE " + table.equals("name", field);
                }
                sqlParams.add(field);
                if (rank != Rank.ANY_RANK) {
                    query += " AND " + table.equals("rank", rank);
                    sqlParams.add(rank.asInteger());
                }
                break;
            default:
                throw new IllegalArgumentException("Uknown category " + category);
        }
        
        if (cache.contains(query, sqlParams)) {
                @SuppressWarnings("unchecked")
                final List<Value<?>> cachedResults = (List<Value<?>>) cache.get(query, sqlParams);
                return cachedResults;
        }
        
        try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
            while (resultSet.next()) {
                if (table == JdbcTable.PARAMETER || table == JdbcTable.RESULT) {
                    final Column column = JdbcMapping.getTableMap(table).get("value");
                    values.add(new Value<>(field, rank, column.fromDB(resultSet)));
                } else {
                    final Column column = JdbcMapping.getTableMap(table).get(field);
                    values.add(new Value<>(field, rank, column.fromDB(resultSet)));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        cache.put(query, sqlParams, values);
        
        return values;
    }

	@Override
	public List<MetaData> getAllMetaData(Type type) throws DatabaseException {
		return getMetaData(null, type);
	}

	@Override
    public List<MetaData> getMetaData(Collection<Restriction> restrictions, Type type) throws DatabaseException {
        
        final List<MetaData> metaData = new ArrayList<>();
        final JdbcTable table = JdbcUtils.typeToTable(type);
        
        final List<String> colNames = new ArrayList<>();
        colNames.addAll(JdbcMapping.getSelectColumns(table));
        
        final List<Object> sqlParams = new ArrayList<>();
        
        String query = "SELECT {0} FROM {1} WHERE {2}";
        query = MessageFormat.format(query,
        /* 0 */ StringUtils.joinStrings(colNames, ", "),
        /* 1 */ helper.getSchema() + "." + table.tableSelect(),
        /* 2 */ JdbcUtils.subRunRestriction(restrictions, sqlParams));
        
        if (cache.contains(query, sqlParams)) {
            @SuppressWarnings("unchecked")
            final List<MetaData> cachedResults = (List<MetaData>) cache.get(query, sqlParams);
            return cachedResults;
        }
        
        try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
            while (resultSet.next()) {
                metaData.add(JdbcUtils.objectFromRow(table, resultSet, MetaData.class));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        cache.put(query, sqlParams, metaData);
        
        return metaData;
    }

	@Override
    public List<String> getMetaDataVendors(Type type) throws DatabaseException {
        final List<String> vendors = new ArrayList<>();
        
        final String query = "SELECT DISTINCT Vendor AS vendor"
                + " FROM " + helper.getSchema() + "." + JdbcUtils.typeToTable(type).tableName();
        
        try (final ResultSet resultSet = helper.executeQuery(query)) {
            while (resultSet.next()) {
                vendors.add(resultSet.getString("vendor"));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        return vendors;
    }

	@Override
    public List<Analysis> getSavedAnalyses(String creatorName) throws DatabaseException {
        final List<Object> sqlParams = new ArrayList<>();
        
        String query = "SELECT {0}.ID AS ID FROM {1}";
        query = MessageFormat.format(query,
        /* 0 */ JdbcTable.ANALYSIS.tableReference(),
        /* 1 */ helper.getSchema() + "." + JdbcTable.ANALYSIS.tableSelect());
        if (creatorName != null) {
            query += " WHERE " + JdbcTable.ANALYSIS.tableColumn("creator") + " = ?";
            sqlParams.add(creatorName);
        }
        
        final List<Long> ids = new ArrayList<>();
        try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
            while (resultSet.next()) {
                ids.add(resultSet.getLong("ID"));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        final List<Analysis> analyses = new ArrayList<>();
        
        for (Long id : ids) {
            sqlParams.clear();
            sqlParams.add(id);
            
            query = "SELECT {0} FROM {1} WHERE {2} = ?";
            query = MessageFormat.format(query,
            /* 0 */ StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.ANALYSIS_CRITERA), ", "),
            /* 1 */ helper.getSchema() + "." + JdbcTable.ANALYSIS_CRITERA.tableSelect(),
            /* 2 */ JdbcMapping.joins.get(JdbcTable.ANALYSIS).get(JdbcTable.ANALYSIS_CRITERA).name());
            
            final List<Restriction> restrictions = new ArrayList<>();
            try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
                while (resultSet.next()) {
                    restrictions.add(restrictionFromRow(resultSet));
                }
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
            
            query = "SELECT {0} FROM {1} WHERE {2} = ?";
            query = MessageFormat.format(query,
            /* 0 */ StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.ANALYSIS_DERIVED_DATA), ", "),
            /* 1 */ helper.getSchema() + "." + JdbcTable.ANALYSIS_DERIVED_DATA.tableSelect(),
            /* 2 */ JdbcMapping.joins.get(JdbcTable.ANALYSIS).get(JdbcTable.ANALYSIS_DERIVED_DATA).name());
            
            final List<DerivedData> dd = new ArrayList<>();
            try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
                while (resultSet.next()) {
                    dd.add(derivedDataFromRow(resultSet));
                }
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        
            query = "SELECT {0} FROM {1} WHERE {2}.ID = ?";
            query = MessageFormat.format(query,
            /* 0 */ StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.ANALYSIS), ", "),
            /* 1 */ helper.getSchema() + "." + JdbcTable.ANALYSIS.tableSelect(),
            /* 2 */ JdbcTable.ANALYSIS.tableReference());
            
            try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
                while (resultSet.next()) {
                    analyses.add(JdbcUtils.analysisFromRow(resultSet, restrictions, dd));
                }
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }
        
        return analyses;
    }

	private Restriction restrictionFromRow(ResultSet row) throws SQLException {
        final Map<String, Column> columns = JdbcMapping.getTableMap(JdbcTable.ANALYSIS_CRITERA);
        
        final Map<String, Object> fields = new HashMap<>();
        for (Map.Entry<String, Column> entry : columns.entrySet()) {
            fields.put(entry.getKey(), entry.getValue().fromDB(row));
        }
        
        final Category category     = (Category) fields.get("category");
        final String field          = (String) fields.get("field");
        final Rank rank             = (Rank) fields.get("rank");
        final Comparator comparator = (Comparator) fields.get("comparator");
        final String valueType      = (String) fields.get("valueType");
        final String valueString    = (String) fields.get("value");
        
        final Value<?> value;
        switch (valueType) {
            case "Integer":
                value = new Value<>(field, rank, Integer.parseInt(valueString));
                break;
            case "Double":
                value = new Value<>(field, rank, Double.parseDouble(valueString));
                break;
            case "String":
                value = new Value<>(field, rank, valueString);
                break;
            case "Void":
                value = new Value<>(field, rank, null);
                break;
            default:
                throw new IllegalStateException("Unknown value type " + valueType);
        }
        
        return newRestriction(category, field, rank, comparator, value);
    }

	private DerivedData derivedDataFromRow(ResultSet row) throws SQLException {
        final Map<String, Column> columns = JdbcMapping.getTableMap(JdbcTable.ANALYSIS_DERIVED_DATA);
        
        final Map<String, Object> fields = new HashMap<>();
        for (Map.Entry<String, Column> entry : columns.entrySet()) {
            fields.put(entry.getKey(), entry.getValue().fromDB(row));
        }
        
        final String name        = (String) fields.get("name");
        final String equation    = (String) fields.get("equation");
        
        return new DerivedData(name, equation);
    }

	@Override
    public DataGrid getDataGrid(Axis xAxis, Axis yAxis, Rank rank, Collection<Restriction> restrictions, Collection<Series> seriesColl) throws DatabaseException {
        
        final List<DataGrid.Row> rows = new ArrayList<>();
        
        final String xAxisName = xAxis.getSubType().toString();
        final String yAxisName = yAxis.getSubType().toString();
        
        JdbcDataGridBuilder gridBuilder = new JdbcDataGridBuilder(helper, xAxis, yAxis, rank, restrictions, seriesColl);
        
        final String query = gridBuilder.generateQuery();
        final List<Object> sqlParams = gridBuilder.getSqlParams();
        
        if (cache.contains(query, sqlParams)) {
            return (DataGrid) cache.get(query, sqlParams);
        }
        
        try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
            while (resultSet.next()) {
                
                final Value<?> xValue;
                switch (xAxis.getType()) {
                    case META_DATA:
                        xValue = new Value<>(xAxis.displayName(), Rank.UNKNOWN, resultSet.getString("xAxis"));
                        break;
                    case PARAMETER:
                        xValue = JdbcUtils.objectFromRow(JdbcTable.PARAMETER, resultSet, Value.class);
                        break;
                    case RESULT:
                        xValue = JdbcUtils.objectFromRow(JdbcTable.RESULT, resultSet, Value.class);
                        break;
                    default:
                        throw new IllegalStateException("Unknown axis type " + xAxis.getType()); 
                }
                
                final Map<String, Value<?>> yValues = new HashMap<>();
                
                switch (yAxis.getType()) {
                    case META_DATA:
                        yValues.put(yAxisName, new Value<>(xAxis.displayName(), Rank.UNKNOWN, resultSet.getString("meta")));
                        break;
                    case PARAMETER:
                        yValues.put(yAxisName, JdbcUtils.objectFromRow(JdbcTable.PARAMETER, resultSet, Value.class));
                        break;
                    case RESULT:
                        yValues.put(yAxisName, JdbcUtils.objectFromRow(JdbcTable.RESULT, resultSet, Value.class));
                        break;
                    case DERIVED:
                        yValues.putAll(JdbcUtils.derivedDataFromRow(gridBuilder.getDerivedDataColumns(), resultSet));
                        break;
                    default:
                        throw new IllegalStateException("Unknown axis type " + yAxis.getType()); 
                }
                
                int seriesIdx = 0;
                List<Object> seriesVals = new ArrayList<>();
                for (Series series : seriesColl) {
                    String seriesRef = "series" + seriesIdx;
                    if (series.getType() == SeriesType.PARAMETER) {
                        String type = resultSet.getString("seriesTable" + seriesIdx + ".Type");
                        switch (type) {
                            case "String":
                                seriesVals.add(resultSet.getString(seriesRef));
                                break;
                            case "Integer":
                                seriesVals.add(resultSet.getInt(seriesRef));
                                break;
                            case "Double":
                                seriesVals.add(resultSet.getDouble(seriesRef));
                                break;
                            default:
                                throw new IllegalStateException("Unknown parameter series type " + type);
                        }
                    } else if (series.getType() == SeriesType.RESULT) {
                        seriesVals.add(resultSet.getDouble(seriesRef));
                    } else {
                        seriesVals.add(resultSet.getString(seriesRef));
                    }
                    ++seriesIdx;
                }
                
                if (seriesColl.isEmpty()) {
                    rows.add(new DataGrid.Row(xValue, yValues, DataGrid.NULL_SERIES_GROUP));
                } else {
                    DataGrid.RowSeriesGroup seriesGroup = new DataGrid.RowSeriesGroup(seriesVals.toArray());
                    rows.add(new DataGrid.Row(xValue, yValues, seriesGroup));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        final DataGrid dataGrid = new DataGrid(xAxisName, yAxisName, rows);
        
        cache.put(query, sqlParams, dataGrid);
        
        return dataGrid;
    }

	@Override
    public List<Run> getRuns() throws DatabaseException {
        final List<Run> runs = new ArrayList<>();
        
        final List<String> colNames = new ArrayList<>();
        colNames.addAll(JdbcMapping.getSelectColumns(JdbcTable.RUN));
        for (MetaData.Type type : MetaData.Type.values()) {
            colNames.addAll(JdbcMapping.getSelectColumns(JdbcUtils.typeToTable(type)));
        }
        
        String query = "SELECT {0} FROM {1}";
        query = MessageFormat.format(query,
        /* 0 */ StringUtils.joinStrings(colNames, ", "),
        /* 1 */ helper.getSchema() + "." + JdbcTable.RUN.tableSelect());
        
        StringBuilder joins = new StringBuilder();
        for (MetaData.Type type : MetaData.Type.values()) {
            JdbcTable table = JdbcUtils.typeToTable(type);
            joins.append(" JOIN ")
                    .append(helper.getSchema())
                    .append(".")
                    .append(JdbcTable.RUN.joinLeft(table));
        }
        query += joins.toString();
        
        try (final ResultSet resultSet = helper.executeQuery(query)) {
            while (resultSet.next()) {
                runs.add(JdbcUtils.objectFromRow(JdbcTable.RUN, resultSet, Run.class));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        return runs;
    }

	@Override
    @SuppressWarnings("unchecked")
    public Collection<RunData> getDataSets(Run run) throws DatabaseException {
        
        final Long runId = helper.getId(run);
        
        String query = "SELECT {0}.ID AS id, {1} FROM {2} JOIN {3} WHERE {4}";
        
        query = MessageFormat.format(query,
        /* 0 */ JdbcTable.SUB_RUN.tableReference(),
        /* 1 */ StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.PARAMETER), ", "),
        /* 2 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.tableSelect(),
        /* 3 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.PARAMETER),
        /* 4 */ JdbcTable.SUB_RUN.equals("run", runId));
        
        final Map<Long, List<Value<?>>> paramsMap = DefaultHashMap.mapOfLists();
        
        try (final ResultSet resultSet = helper.executeQuery(query, runId)) {
            while (resultSet.next()) {
                final Long id = resultSet.getLong("id");
                paramsMap.get(id).add(JdbcUtils.objectFromRow(JdbcTable.PARAMETER, resultSet, Value.class));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        query = "SELECT {0}.ID AS id, {1} FROM {2} JOIN {3} WHERE {4}";
        
        query = MessageFormat.format(query,
        /* 0 */ JdbcTable.SUB_RUN.tableReference(),
        /* 1 */ StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.RESULT), ", "),
        /* 2 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.tableSelect(),
        /* 3 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.RESULT),
        /* 4 */ JdbcTable.SUB_RUN.equals("run", runId));
        
        final Map<Long, List<Value<Double>>> resultMap = DefaultHashMap.mapOfLists();
        
        try (final ResultSet resultSet = helper.executeQuery(query, runId)) {
            while (resultSet.next()) {
                final Long id = resultSet.getLong("id");
                resultMap.get(id).add(JdbcUtils.objectFromRow(JdbcTable.RESULT, resultSet, Value.class));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
        
        final List<RunData> runData = new ArrayList<>();
        for (Map.Entry<Long, List<Value<?>>> entry : paramsMap.entrySet()) {
            final List<Value<?>> params = entry.getValue();
            final List<Value<Double>> results = resultMap.get(entry.getKey());
            if (params == null || results == null) {
                throw new IllegalStateException("SubRun with ID " + entry.getKey()
                        + " found with no params or results");
            }
            runData.add(new RunData(params, results));
        }
        
        return runData;
    }

	@Override
	public void save(DatabaseObject dbObject) throws DatabaseException {
		if (helper.getId(dbObject) != null) {
			throw new DatabaseException("Cannot save " + dbObject
					+ ": Entity already exists in database");
		}

		try {
			if (dbObject instanceof MetaData) {
				helper.saveMetaData((MetaData) dbObject).commit();
			} else if (dbObject instanceof Run) {
				helper.saveRun((Run) dbObject).commit();
			} else if (dbObject instanceof Analysis) {
				helper.saveAnalysis((Analysis) dbObject).commit();
			} else {
				throw new UnsupportedOperationException("Not supported yet.");
			}
			cache.dirty();
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
    public void delete(DatabaseObject dbObject) throws DatabaseException {
        final Long id = helper.getId(dbObject);
        if (id == null) {
            throw new DatabaseException("Cannot delete non persistant entity: "
                    + dbObject);
        }
        
        String query;
        List<Object> sqlParams = new ArrayList<>();
        
        if (dbObject instanceof MetaData) {
            final MetaData metaData = (MetaData) dbObject;
            final JdbcTable table = JdbcUtils.typeToTable(metaData.getType());
                
            query = "DELETE FROM {0} WHERE ID = ?";
            query = MessageFormat.format(query,
            /* 0 */ helper.getSchema() + "." + table.tableName());
            
            try (Connection conn = helper.executeUpdate(query, sqlParams)) {
                conn.commit();
                cache.dirty();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        } else if (dbObject instanceof Run)  {
            final List<Long> subRunIds = new ArrayList<>();

            query = "SELECT {0}.ID AS id FROM {1} WHERE {2} = ?";
            query = MessageFormat.format(query,
            /* 0 */ JdbcTable.SUB_RUN.tableReference(),
            /* 1 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.tableSelect(),
            /* 2 */ JdbcMapping.joins.get(JdbcTable.RUN).get(JdbcTable.SUB_RUN).select());
            
            try {
                ResultSet resultSet = helper.executeQuery(query, id);
                while (resultSet.next()) {
                    subRunIds.add(resultSet.getLong("id"));
                }

                if (!subRunIds.isEmpty()) {
                    helper.deleteFromTable(JdbcTable.PARAMETER, JdbcTable.SUB_RUN, subRunIds);
                    helper.deleteFromTable(JdbcTable.RESULT, JdbcTable.SUB_RUN, subRunIds);
                }
                helper.deleteFromTable(JdbcTable.SUB_RUN, JdbcTable.RUN, Arrays.asList(id));
                helper.deleteFromTable(JdbcTable.RUN_FLAGS, JdbcTable.RUN, Arrays.asList(id));
                
                query = "DELETE FROM {0} WHERE ID = ?";
                query = MessageFormat.format(query,
                /* 0 */ helper.getSchema() + "." + JdbcTable.RUN.tableName());
                
                helper.executeUpdate(query, id).commit();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        } else if (dbObject instanceof Analysis) {
            final List<Long> criteriaIds = new ArrayList<>();
            
            query = "SELECT {0}.ID AS id FROM {1} WHERE {2} = ?";
            query = MessageFormat.format(query,
            /* 0 */ JdbcTable.ANALYSIS_CRITERA.tableReference(),
            /* 1 */ helper.getSchema() + "." + JdbcTable.ANALYSIS_CRITERA.tableSelect(),
            /* 2 */ JdbcMapping.joins.get(JdbcTable.ANALYSIS).get(JdbcTable.ANALYSIS_CRITERA).select());
            
            try {
                ResultSet resultSet = helper.executeQuery(query, id);
                while (resultSet.next()) {
                    criteriaIds.add(resultSet.getLong("id"));
                }
                
                if (!criteriaIds.isEmpty()) {
                    helper.deleteFromTable(JdbcTable.ANALYSIS_CRITERA, JdbcTable.ANALYSIS, criteriaIds);
                }
                
                query = "DELETE FROM {0} WHERE ID = ?";
                query = MessageFormat.format(query,
                /* 0 */ helper.getSchema() + "." + JdbcTable.ANALYSIS.tableName());

                helper.executeUpdate(query, id).commit();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

	@Override
    public boolean tryConnection(Configuration config) {
        final String query = "SELECT COUNT(*) AS count FROM "
                + helper.getSchema() + "." + JdbcTable.RUN.tableName();
        
        try (final ResultSet resultSet = helper.executeQuery(query)) {            
            return true;
        } catch (SQLException ex) {
            LOG.error("Connection attempt failed", ex);
            return false;
        }
    }

	@Override
    public void update(DatabaseObject oldObject, DatabaseObject newObject) throws DatabaseException {
        final Long id = helper.getId(oldObject);
        if (id == null) {
            throw new DatabaseException("Cannot update non persistant entity: "
                    + oldObject);
        }
        
        if (newObject instanceof MetaData) {
            final MetaData metaData = (MetaData) newObject;
            final JdbcTable table = JdbcUtils.typeToTable(metaData.getType());
            
            final List<String> updates = new ArrayList<>();
            final List<Object> vals = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : metaData.getDataMap().entrySet()) {
                updates.add(table.equals(entry.getKey(), entry.getValue()));
                vals.add(entry.getValue());
            }
            
            String query = "UPDATE {0}.{1} SET {2} WHERE ID = ?";
            query = MessageFormat.format(query,
            /* 0 */ helper.getSchema(),
            /* 1 */ table.tableName(),
            /* 2 */ StringUtils.joinStrings(updates, ", "));

            vals.add(id);
            try (Connection conn = helper.executeUpdate(query, vals)) {
                conn.commit();
                cache.dirty();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

	@Override
    public Collection<String> getFlags(Run run) throws DatabaseException {
        final Long id = helper.getId(run);
        if (id == null) {
            throw new DatabaseException("Cannot find run in database: " + run);
        }
        
        String query = "SELECT {0} AS flag FROM {1}"
                + " JOIN {2}"
                + " JOIN {3}"
                + " WHERE {4} = ?";
        
        query = MessageFormat.format(query,
        /* 0 */ JdbcTable.FLAGS.tableColumn("flag"),
        /* 1 */ helper.getSchema() + "." + JdbcTable.RUN_FLAGS.tableSelect(),
        /* 2 */ helper.getSchema() + "." + JdbcTable.RUN_FLAGS.joinLeft(JdbcTable.RUN),
        /* 3 */ helper.getSchema() + "." + JdbcTable.RUN_FLAGS.joinLeft(JdbcTable.FLAGS),
        /* 4 */ JdbcMapping.joins.get(JdbcTable.RUN).get(JdbcTable.RUN_FLAGS).select());

        if (cache.contains(query, Arrays.asList(id))) {
            @SuppressWarnings("unchecked")
            final Collection<String> cachedResults = (Collection<String>) cache.get(query, Arrays.asList(id));
            return cachedResults;
        }

        try (final ResultSet resultSet = helper.executeQuery(query, id)) {
            final List<String> flags = new ArrayList<>();
            while (resultSet.next()) {
                flags.add(resultSet.getString("Flag"));
            }
            
            cache.put(query, Arrays.asList(id), flags);
            
            return flags;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }
}
