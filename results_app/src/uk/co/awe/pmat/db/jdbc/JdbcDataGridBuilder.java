package uk.co.awe.pmat.db.jdbc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.Function;
import uk.co.awe.pmat.deriveddata.ParserValues.Constant;
import uk.co.awe.pmat.deriveddata.ParserValues.Property;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.series.Series;
import uk.co.awe.pmat.utils.StringUtils;

/**
 *
 * @author AWE Plc copyright 2013
 */
class JdbcDataGridBuilder {

    private final JdbcHelper helper;
    private final Axis xAxis;
    private final Axis yAxis;
    private final Collection<Restriction> restrictions;
    private final Collection<Series> seriesColl;
    
    private final Set<String> selects = new HashSet<>();
    // joins needs to be an ordered set in order to removed duplicate joins
    // whilst maintaing the order of the joins.
    private final Set<String> joins = new LinkedHashSet<>();
    private final List<String> clauses = new ArrayList<>();
    private final List<Object> sqlParams = new ArrayList<>();
    private final Map<String, String> derivedDataColumns = new HashMap<>();
    
    JdbcDataGridBuilder(JdbcHelper helper, Axis xAxis, Axis yAxis, Rank rank, Collection<Restriction> restrictions, Collection<Series> seriesColl) {
        this.helper = helper;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.restrictions = restrictions;
        this.seriesColl = seriesColl;
    }
    

    /**
     * Parse a derived axis to find all the Hibernate query fragments used to
     * return the columns needed to create the derived data.
     *
     * @param derivedData the derived axis to parse.
     * @return the Hibernate query fragments.
     */
    private void parseDerivedData(DerivedData derivedData) {

        int ddIdx = 0;

        final Map<Variable.Type, Set<String>> varNames
                = new EnumMap<>(Variable.Type.class);
        for (Variable.Type type : Variable.Type.values()) {
            varNames.put(type, new HashSet<String>());
        }
        
        parseDerivedData(ddIdx, varNames, derivedData);
    }

    /**
     * Recursive function used to build up the derived data select, join, and
     * where clause.
     *
     * @param ddIdx a running index, used to generate names for variables in
     * the select columns.
     * @param select the list of columns to select.
     * @param join the entities that need to be joined for the columns in the
     * selects to be available.
     * @param clause the where clauses used to join and filter the entities
     * given by {@code join}.
     * @param parameters the query parameters to set.
     * @param varNames the list of variable names we are selecting columns for,
     * used so we don't fetch the same variable twice.
     * @param derivedData the derived data to parse.
     * @return the ddIdx, updated if any variables have been parsed.
     */
    private int parseDerivedData(int ddIdx,
            Map<Variable.Type, Set<String>> varNames,
            DerivedData... derivedData) {

        for (DerivedData dd : derivedData) {
            if (dd instanceof Function) {
                final Function func = ((Function) dd);
                ddIdx = parseDerivedData(ddIdx, varNames, func.getArgs());
            } else if (dd instanceof Variable) {
                final Variable var = (Variable) dd;
                // If we have already parsed this variable, continue.
                if (varNames.get(var.getType()).contains(var.getName())) {
                    continue;
                }
                ddIdx = parseVariable(ddIdx, var);
                varNames.get(var.getType()).add(var.getName());
            } else if (dd instanceof Property) {
                final Property prop = (Property) dd;
                ddIdx = parseProperty(ddIdx, prop);
            } else if (!(dd instanceof Constant)) {
                throw new IllegalStateException("Unknown DerivedData type " + dd);
            }
        }

        return ddIdx;
    }

    /**
     * Parse a derived axis property to find all the Hibernate query fragments
     * used to return its column from the database.
     *
     * @param prop the property to parse.
     * @param select the select statements to add to.
     * @param join the join statements to add to.
     */
    private int parseProperty(int ddIdx, Property prop) {
        final String name = prop.getType().asFieldName();
        final String derivedColRef = "derived" + ddIdx;
        selects.add(JdbcTable.RUN.tableName() + "." + name + " AS " + derivedColRef);
        derivedDataColumns.put(name, derivedColRef);
        return (ddIdx + 1);
    }

    Map<String, String> getDerivedDataColumns() {
        return derivedDataColumns;
    }

    /**
     * Parse a derived axis variable to find all the Hibernate query fragments
     * used to return its column from the database.
     *
     * @param ddIdx a running index, used to generate names for variables in
     * the select columns.
     * @param var the variable to parse.
     * @param select the select statements to add to.
     * @param join the join statements to add to.
     * @param clause the where clause statements to add to.
     * @param parameters the Hibernate query parameters to add to.
     * @return the ddIdx value incremented by one.
     */
    private int parseVariable(int ddIdx, Variable var) {

        final Variable.Type varType = var.getType();
        final String varName = var.getName();
        final String derivedTableRef = "derivedTable" + ddIdx;
        final String derivedColRef = "derived" + ddIdx;

        final JdbcTable table;
        switch (varType) {
            case PARAMETER:
                table = JdbcTable.PARAMETER;
                break;
            case RESULT:
                table = JdbcTable.RESULT;
                break;
            default:
                throw new IllegalStateException("Unknown variable type " + varType);
        }
        
        selects.add(JdbcMapping.getTableMap(table).get("value").select(derivedTableRef, derivedColRef));
        joins.add(helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(table, derivedTableRef));
        clauses.add(derivedTableRef + "." + table.equals("name", varName));
        sqlParams.add(varName);
        derivedDataColumns.put(varName, derivedColRef);

        return (ddIdx + 1);
    }

    String generateQuery() throws DatabaseException {       
        final String subRunRestriction = JdbcUtils.subRunRestriction(restrictions, sqlParams);
        
        joins.add(helper.getSchema() + "." + JdbcTable.RUN.joinRight(JdbcTable.SUB_RUN));
        
        parseAxis(xAxis);
        
        if (yAxis.getType() == AxisType.DERIVED) {
            parseDerivedData((DerivedData) yAxis.getSubType());
        } else {
            parseAxis(yAxis);
        }

        parseSeries();
        
        String query = "SELECT {0} FROM {1} JOIN {2} WHERE {3} AND {4}";
        
        query = MessageFormat.format(query,
        /* 0 */ StringUtils.joinStrings(selects, ", "),
        /* 1 */ helper.getSchema() + "." + JdbcTable.RUN.tableSelect(),
        /* 2 */ StringUtils.joinStrings(joins, " JOIN "),
        /* 3 */ subRunRestriction,
        /* 4 */ StringUtils.joinStrings(clauses, " AND "));
        
        return query;
    }
    
    List<Object> getSqlParams() {
        return sqlParams;
    }
    
    /**
     * Generate the SQL selects, joins and clauses needed to return the given
     * axis in an SQL query.
     * 
     * @param axis the axis to generate the SQL for.
     * @param selects the SQL selects to add to.
     * @param joins the SQL joins to add to.
     * @param clauses the SQL where clauses to add to.
     * @param params the SQL parameters to add to (each one matching a wild card
     * "?" in the clauses).
     */
    private void parseAxis(Axis axis) {
        switch (axis.getType()) {
            case PARAMETER:
                selects.add(StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.PARAMETER), ", "));
                joins.add(helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.PARAMETER));
                clauses.add(JdbcTable.PARAMETER.tableReference() + "."
                        + JdbcTable.PARAMETER.equals("name", axis.getSubType()));
                sqlParams.add(axis.getSubType());
                break;
            case RESULT:
                selects.add(StringUtils.joinStrings(JdbcMapping.getSelectColumns(JdbcTable.RESULT), ", "));
                joins.add(helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.RESULT));
                clauses.add(JdbcTable.RESULT.tableReference() + "."
                        + JdbcTable.RESULT.equals("name", axis.getSubType()));
                sqlParams.add(axis.getSubType());
                break;
            case META_DATA:
                final MetaData.Type subType = (MetaData.Type) axis.getSubType();
                selects.add(JdbcUtils.typeToTable(subType).tableReference() + ".Name AS " + axis.getAxisName() + "Axis");
                joins.add(helper.getSchema() + "." + JdbcTable.RUN.joinLeft(JdbcUtils.typeToTable(subType)));
                break;
            default:
                throw new IllegalStateException("Unknown axis type " + axis.getType());
        }
    }
    
    private void parseSeries() {
        int seriesIdx = 0;
        for (Series series : seriesColl) {
            final String seriesColRef = "series" + seriesIdx;
            final String seriesTableRef = "seriesTable" + seriesIdx;
            switch (series.getType()) {
                case PARAMETER:
                    selects.add(JdbcMapping.getTableMap(JdbcTable.PARAMETER).get("value").select(seriesTableRef, seriesColRef));
                    joins.add(helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.PARAMETER, seriesTableRef));
                    clauses.add(seriesTableRef + "."
                            + JdbcTable.PARAMETER.equals("name", series.getSubType()));
                    sqlParams.add(series.getSubType());
                    break;
                case RESULT:
                    selects.add(JdbcMapping.getTableMap(JdbcTable.RESULT).get("value").select(seriesTableRef, seriesColRef));
                    joins.add(helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.RESULT, seriesTableRef));
                    clauses.add(seriesTableRef + "."
                            + JdbcTable.RESULT.equals("name", series.getSubType()));
                    sqlParams.add(series.getSubType());
                    break;
                case META_DATA:
                    final MetaData.Type subType = (MetaData.Type) series.getSubType();
                    final JdbcTable table = JdbcUtils.typeToTable(subType);
                    selects.add(JdbcMapping.getTableMap(table).get("name").select(seriesTableRef, seriesColRef));
                    joins.add(helper.getSchema() + "." + JdbcTable.RUN.joinLeft(table, seriesTableRef));
                    break;
                case TAG:
                    selects.add(JdbcTable.RUN.tableColumn("tag") + " AS " + seriesColRef);
                    break;
                default:
                    throw new IllegalStateException("Unknown series type " + series.getType());
            }
            ++seriesIdx;
        }
    }
    
}
