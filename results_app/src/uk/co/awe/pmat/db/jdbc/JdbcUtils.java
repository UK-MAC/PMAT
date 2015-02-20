package uk.co.awe.pmat.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.Application;
import uk.co.awe.pmat.db.Compiler;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DerivedData;
import uk.co.awe.pmat.db.ErrorType;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.Machine;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.MetaData.Type;
import uk.co.awe.pmat.db.Mpi;
import uk.co.awe.pmat.db.OperatingSystem;
import uk.co.awe.pmat.db.Processor;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.jdbc.JdbcMapping.Column;
import uk.co.awe.pmat.utils.Pair;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * 
 * @author AWE Plc copyright 2013
 */
final class JdbcUtils {

	static Map<String, Value<?>> derivedDataFromRow(Map<String, String> derivedDataColumns, ResultSet resultSet) throws SQLException {
        Map<String, Value<?>> values = new HashMap<>();
        for (Map.Entry<String, String> entry : derivedDataColumns.entrySet()) {
            final String ddName = entry.getKey();
            final String colName = entry.getValue();
            final Object value = resultSet.getObject(colName);
            if (value instanceof Integer) {
                values.put(ddName, new Value<>(ddName, Rank.UNKNOWN, (Integer) value));
            } else if (value instanceof Double) {
                values.put(ddName, new Value<>(ddName, Rank.UNKNOWN, (Double) value));
            } else if (value instanceof String) {
                values.put(ddName, new Value<>(ddName, Rank.UNKNOWN, (String) value));
            } else {
                throw new IllegalStateException("Unknown value type " + value);
            }
        }
        return values;
    }

	private JdbcUtils() {
	}

	static JdbcTable typeToTable(Type type) {
		switch (type) {
		case APPLICATION:
			return JdbcTable.APPLICATION;
		case COMPILER:
			return JdbcTable.COMPILER;
		case MACHINE:
			return JdbcTable.MACHINE;
		case MPI:
			return JdbcTable.MPI;
		case OPERATING_SYSTEM:
			return JdbcTable.OPERATING_SYSTEM;
		case PROCESSOR:
			return JdbcTable.PROCESSOR;
		default:
			throw new IllegalArgumentException("Uknown type " + type);
		}
	}

	static JdbcTable categoryToTable(Category category) {
		switch (category) {
		case APPLICATION:
			return JdbcTable.APPLICATION;
		case COMPILER:
			return JdbcTable.COMPILER;
		case MACHINE:
			return JdbcTable.MACHINE;
		case MPI:
			return JdbcTable.MPI;
		case OPERATING_SYSTEM:
			return JdbcTable.OPERATING_SYSTEM;
		case PARAMETER:
			return JdbcTable.PARAMETER;
		case PROCESSOR:
			return JdbcTable.PROCESSOR;
		case RESULT:
			return JdbcTable.RESULT;
		case RUN:
			return JdbcTable.RUN;
		default:
			throw new IllegalArgumentException("Uknown category " + category);
		}
	}

	static Analysis analysisFromRow(ResultSet resultSet,
            List<Restriction> restrictions,
            List<DerivedData> dd) throws SQLException {
        final Map<String, Column> columns = JdbcMapping.getTableMap(JdbcTable.ANALYSIS);
        
        final Map<String, Object> fields = new HashMap<>();
        for (Map.Entry<String, Column> entry : columns.entrySet()) {
            fields.put(entry.getKey(), entry.getValue().fromDB(resultSet));
        }
        
        String creator     = (String) fields.get("creator");
        Date date          = (Date) fields.get("date");
        String notes       = (String) fields.get("notes");
        Boolean restricted = (Boolean) fields.get("dataPrivate");
        
        List<Graph> graphs = Collections.emptyList();
        List<Pair<String,String>> renames = Collections.emptyList();
        
        return new Analysis(creator, date, notes, restricted, graphs,
                dd, restrictions, renames);
    }

	@SuppressWarnings("unchecked")
    static <T> T objectFromRow(JdbcTable table, ResultSet row, Class<T> clz) throws SQLException {
        
        final Map<String, Column> columns = JdbcMapping.getTableMap(table);
        
        final Map<String, Object> fields = new HashMap<>();
        for (Map.Entry<String, Column> entry : columns.entrySet()) {
            fields.put(entry.getKey(), entry.getValue().fromDB(row));
        }
                
        switch (table) {
            case APPLICATION:
                return (T) new Application(fields);
            case COMPILER:
                return (T) new Compiler(fields);
            case MACHINE:
                return (T) new Machine(fields);
            case MPI:
                return (T) new Mpi(fields);
            case OPERATING_SYSTEM:
                return (T) new OperatingSystem(fields);
            case PARAMETER:
                String pName      = (String) fields.get("name");
                Rank pRank        = (Rank) fields.get("rank");
                Comparable pValue = (Comparable) fields.get("value");
                return (T) new Value<>(pName, pRank, pValue);
            case PROCESSOR:
                return (T) new Processor(fields);
            case RESULT:
                String rName       = (String) fields.get("name");
                Rank rRank         = (Rank) fields.get("rank");
                Double rValue      = (Double) fields.get("value");
                Double rError      = (Double) fields.get("error");
                ErrorType rErrType = (ErrorType) fields.get("errorType");
                Long count         = (Long) fields.get("count");
                Long pauseCount    = (Long) fields.get("pauseCount");
                return (T) new Value<>(rName, rValue, rError, rErrType, rRank, count, pauseCount);
            case RUN:
                List<MetaData> metaData = new ArrayList<>();
                for (MetaData.Type type : MetaData.Type.values()) {
                    metaData.add((MetaData) objectFromRow(typeToTable(type), row, MetaData.class));
                }
                String runId       = (String) fields.get("runId");
                Date runDate       = (Date) fields.get("runDate");
                Date insertionDate = (Date) fields.get("runDate");
                String creator     = (String) fields.get("creator");
                Boolean restricted = (Boolean) fields.get("restricted");
                String file        = (String) fields.get("file");
                String tag         = (String) fields.get("tag");
                return (T) new Run(runId, runDate, insertionDate, metaData, creator, restricted, file, tag);
            default:
                throw new IllegalArgumentException("Uknown category " + table);
        }
    }

	static String subRunRestriction(Collection<Restriction> restrictions, List<Object> sqlParams) throws DatabaseException {
        if (restrictions == null) {
            return "1 = 1";
        }
        
        Set<Long> ids = Collections.emptySet();
        boolean first = true;
        for (Restriction restriction : restrictions) {
            if (first) {
                ids = new HashSet<>(restriction.getMatchingIDs());
            } else {
                ids.retainAll(restriction.getMatchingIDs());
            }
            first = false;
        }
        
        if (ids.isEmpty()) {
            return "1 = 1";
        }
        
        final List<String> placeHolders = new ArrayList<>(ids.size());
        for (Long id : ids) {
            placeHolders.add("?");
            sqlParams.add(id);
        }
        
        final String query = "{0}.ID IN (" + StringUtils.joinStrings(placeHolders, ", ") + ")";
        return MessageFormat.format(query, JdbcTable.SUB_RUN.tableReference());
    }
}
