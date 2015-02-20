package uk.co.awe.pmat.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import uk.co.awe.pmat.GraphColour;
import uk.co.awe.pmat.LineStyle;
import uk.co.awe.pmat.db.ErrorType;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.axis.AxisValueType;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.utils.DefaultHashMap;

final class JdbcMapping {

    final static class Column {
        private final JdbcTable table;
        private final String name;
        private final Type type;

        public Column(JdbcTable table, String name, Type type) {
            this.table = table;
            this.name = name;
            this.type = type;
        }
        
        Comparable fromDB(ResultSet row) throws SQLException {
            final String tableName = table.toString().toLowerCase() + ".";
            if (table == JdbcTable.PARAMETER && name.equals("Value")) {
                switch (row.getString(tableName + "Type")) {
                    case "Integer": return row.getInt("Value");
                    case "Double": return row.getDouble("Value");
                    case "String": return row.getString("Value");
                    default:
                        throw new IllegalStateException("Unknown parameter type "
                                + row.getString(tableName + "Type"));
                }
            } else {
                switch (type) {
                    case BOOLEAN: return row.getBoolean(tableName + name);
                    case CATEGORY: return Category.valueOf(row.getString(tableName + name));
                    case COMPARATOR: return Comparator.valueOf(row.getString(tableName + name));
                    case DATE: return row.getTimestamp(tableName + name);
                    case DOUBLE: return row.getDouble(tableName + name);
                    case INTEGER: return row.getInt(tableName + name);
                    case STRING: return row.getString(tableName + name);
                    case RANK: return Rank.fromInteger(row.getInt(tableName + name));
                    case LONG: return row.getLong(tableName + name);
                    case ERROR_TYPE: return ErrorType.valueOf(row.getString(tableName + name));
                    case AXIS_NAME: return AxisName.valueOf(row.getString(tableName + name));
                    case AXIS_TYPE: return AxisType.valueOf(row.getString(tableName + name));
                    case AXIS_VALUE_TYPE: return AxisValueType.valueOf(row.getString(tableName + name));
                    case LINE_STYLE: return LineStyle.valueOf(row.getString(tableName + name));
                    case LINE_COLOUR: return GraphColour.valueOf(row.getString(tableName + name));
                    default:
                        throw new IllegalStateException("Unknown column type "
                                + type);
                }
            }
        }

        public String name() {
            return name;
        }

        String select(String tableRef, String alias) {
            if (tableRef == null) { tableRef = table.toString().toLowerCase(); }
            if (table == JdbcTable.PARAMETER && name.equals("Value")) {
                if (alias == null) { alias = name; }
                return MessageFormat.format(
                        "{0}.Type, CASE {0}.Type"
                        + " WHEN ''String'' THEN {0}.StringValue"
                        + " WHEN ''Double'' THEN {0}.DoubleValue"
                        + " WHEN ''Integer'' THEN {0}.IntegerValue"
                        + " END AS `" + alias + "`",
                        tableRef);
            }
            if (alias == null) {
                return tableRef + "." + name;
            } else {
                return tableRef + "." + name + " AS " + alias;
            }
        }
        
        String select() {
            return select(null, null);
        }
        
        String insertColumns() {
            if (table == JdbcTable.PARAMETER && name.equals("Value")) {
                return "Type, IntegerValue, DoubleValue, StringValue";
            } else if (table == JdbcTable.SUB_RUN && name.equals("ParentRun")) {
                return "ParentRun, Sequence";
            }
            return name;
        }
        
        String placeHolder() {
            if (table == JdbcTable.PARAMETER && name.equals("Value")) {
                return "?, ?, ?, ?";
            } else if (table == JdbcTable.SUB_RUN && name.equals("ParentRun")) {
                return "?, ?";
            }
            return "?";
        }
        
        private enum Type {
            STRING,
            INTEGER,
            DOUBLE,
            DATE,
            BOOLEAN,
            CATEGORY,
            COMPARATOR,
            RANK,
            LONG,
            ERROR_TYPE,
            AXIS_NAME,
            AXIS_TYPE,
            AXIS_VALUE_TYPE,
            LINE_STYLE,
            LINE_COLOUR,
        }
    }
    
    private final static Map<JdbcTable, Map<String, Column>> TableMaps = new EnumMap<>(JdbcTable.class);
    
    private final static Map<String, Column> AnalysisMap = new LinkedHashMap<>();
    private final static Map<String, Column> AnalysisCriteriaMap = new LinkedHashMap<>();
    private final static Map<String, Column> AnalysisDerivedDataMap = new LinkedHashMap<>();
    private final static Map<String, Column> AnalysisGraphMap = new LinkedHashMap<>();
    private final static Map<String, Column> AnalysisAxisLabelMap = new LinkedHashMap<>();
    private final static Map<String, Column> ApplicationMap = new LinkedHashMap<>();
    private final static Map<String, Column> CompilerMap = new LinkedHashMap<>();
    private final static Map<String, Column> MachineMap = new LinkedHashMap<>();
    private final static Map<String, Column> MpiMap = new LinkedHashMap<>();
    private final static Map<String, Column> OperatingSystemMap = new LinkedHashMap<>();
    private final static Map<String, Column> ProcessorMap = new LinkedHashMap<>();
    private final static Map<String, Column> RunMap = new LinkedHashMap<>();
    private final static Map<String, Column> ParameterMap = new LinkedHashMap<>();
    private final static Map<String, Column> ResultMap = new LinkedHashMap<>();
    private final static Map<String, Column> FlagMap = new LinkedHashMap<>();
    private final static Map<String, Column> RunFlagMap = new LinkedHashMap<>();
    private final static Map<String, Column> SubRunMap = new LinkedHashMap<>();
    
    private final static Map<JdbcTable, List<String>> TableKeys = new EnumMap<>(JdbcTable.class);
    
    static {
        TableMaps.put(JdbcTable.ANALYSIS, AnalysisMap);
        TableMaps.put(JdbcTable.ANALYSIS_CRITERA, AnalysisCriteriaMap);
        TableMaps.put(JdbcTable.ANALYSIS_DERIVED_DATA, AnalysisDerivedDataMap);
        TableMaps.put(JdbcTable.ANALYSIS_GRAPH, AnalysisGraphMap);
        TableMaps.put(JdbcTable.ANALYSIS_AXIS_LABEL, AnalysisAxisLabelMap);
        TableMaps.put(JdbcTable.APPLICATION, ApplicationMap);
        TableMaps.put(JdbcTable.COMPILER, CompilerMap);
        TableMaps.put(JdbcTable.MACHINE, MachineMap);
        TableMaps.put(JdbcTable.MPI, MpiMap);
        TableMaps.put(JdbcTable.OPERATING_SYSTEM, OperatingSystemMap);
        TableMaps.put(JdbcTable.PROCESSOR, ProcessorMap);
        TableMaps.put(JdbcTable.RUN, RunMap);
        TableMaps.put(JdbcTable.PARAMETER, ParameterMap);
        TableMaps.put(JdbcTable.RESULT, ResultMap);
        TableMaps.put(JdbcTable.FLAGS, FlagMap);
        TableMaps.put(JdbcTable.RUN_FLAGS, RunFlagMap);
        TableMaps.put(JdbcTable.SUB_RUN, SubRunMap);
        
        AnalysisMap.put("creator",     new Column(JdbcTable.ANALYSIS, "Creator", Column.Type.STRING));
        AnalysisMap.put("notes",       new Column(JdbcTable.ANALYSIS, "Notes", Column.Type.STRING));
        AnalysisMap.put("dataPrivate", new Column(JdbcTable.ANALYSIS, "Private", Column.Type.BOOLEAN));
        AnalysisMap.put("date",        new Column(JdbcTable.ANALYSIS, "Date", Column.Type.DATE));
        
        AnalysisCriteriaMap.put("analysisId", new Column(JdbcTable.ANALYSIS_CRITERA, "AnalysisID", Column.Type.LONG));
        AnalysisCriteriaMap.put("sequence",   new Column(JdbcTable.ANALYSIS_CRITERA, "Sequence", Column.Type.LONG));
        AnalysisCriteriaMap.put("category",   new Column(JdbcTable.ANALYSIS_CRITERA, "Category", Column.Type.CATEGORY));
        AnalysisCriteriaMap.put("field",      new Column(JdbcTable.ANALYSIS_CRITERA, "Field", Column.Type.STRING));
        AnalysisCriteriaMap.put("rank",       new Column(JdbcTable.ANALYSIS_CRITERA, "Rank", Column.Type.RANK));
        AnalysisCriteriaMap.put("comparator", new Column(JdbcTable.ANALYSIS_CRITERA, "Comparator", Column.Type.COMPARATOR));
        AnalysisCriteriaMap.put("value",      new Column(JdbcTable.ANALYSIS_CRITERA, "Value", Column.Type.STRING));
        AnalysisCriteriaMap.put("valueType",  new Column(JdbcTable.ANALYSIS_CRITERA, "ValueType", Column.Type.STRING));
        
        AnalysisDerivedDataMap.put("analysisId", new Column(JdbcTable.ANALYSIS_DERIVED_DATA, "AnalysisID", Column.Type.LONG));
        AnalysisDerivedDataMap.put("name", new Column(JdbcTable.ANALYSIS_DERIVED_DATA, "Name", Column.Type.STRING));
        AnalysisDerivedDataMap.put("equation", new Column(JdbcTable.ANALYSIS_DERIVED_DATA, "Equation", Column.Type.STRING));
        
        AnalysisGraphMap.put("analysisId", new Column(JdbcTable.ANALYSIS_GRAPH, "AnalysisID", Column.Type.LONG));
        AnalysisGraphMap.put("xAxisName", new Column(JdbcTable.ANALYSIS_GRAPH, "xAxisName", Column.Type.AXIS_NAME));
        AnalysisGraphMap.put("yAxisName", new Column(JdbcTable.ANALYSIS_GRAPH, "yAxisName", Column.Type.AXIS_NAME));
        AnalysisGraphMap.put("xAxisType", new Column(JdbcTable.ANALYSIS_GRAPH, "xAxisType", Column.Type.AXIS_TYPE));
        AnalysisGraphMap.put("yAxisType", new Column(JdbcTable.ANALYSIS_GRAPH, "yAxisType", Column.Type.AXIS_TYPE));
        AnalysisGraphMap.put("axisValueType", new Column(JdbcTable.ANALYSIS_GRAPH, "AxisValueType", Column.Type.AXIS_VALUE_TYPE));
        AnalysisGraphMap.put("rank", new Column(JdbcTable.ANALYSIS_GRAPH, "Rank", Column.Type.RANK));
        AnalysisGraphMap.put("lineStyle", new Column(JdbcTable.ANALYSIS_GRAPH, "LineStyle", Column.Type.LINE_STYLE));
        AnalysisGraphMap.put("lineColour", new Column(JdbcTable.ANALYSIS_GRAPH, "LineColour", Column.Type.LINE_COLOUR));
        AnalysisGraphMap.put("lineWidth", new Column(JdbcTable.ANALYSIS_GRAPH, "LineWidth", Column.Type.LONG));
        
        AnalysisAxisLabelMap.put("analysisId", new Column(JdbcTable.ANALYSIS_AXIS_LABEL, "AnalysisID", Column.Type.LONG));
        AnalysisAxisLabelMap.put("oldText", new Column(JdbcTable.ANALYSIS_AXIS_LABEL, "OldText", Column.Type.STRING));
        AnalysisAxisLabelMap.put("newText", new Column(JdbcTable.ANALYSIS_AXIS_LABEL, "NewText", Column.Type.STRING));
                
        ApplicationMap.put("name",         new Column(JdbcTable.APPLICATION, "Name", Column.Type.STRING));
        ApplicationMap.put("versionMajor", new Column(JdbcTable.APPLICATION, "VersionMajor", Column.Type.INTEGER));
        ApplicationMap.put("versionMinor", new Column(JdbcTable.APPLICATION, "VersionMinor", Column.Type.INTEGER));
        ApplicationMap.put("versionBuild", new Column(JdbcTable.APPLICATION, "VersionBuild", Column.Type.INTEGER));
        ApplicationMap.put("versionCode",  new Column(JdbcTable.APPLICATION, "VersionCode", Column.Type.STRING));
        ApplicationMap.put("description",  new Column(JdbcTable.APPLICATION, "Description", Column.Type.STRING));
        ApplicationMap.put("dataPrivate",  new Column(JdbcTable.APPLICATION, "Private", Column.Type.BOOLEAN));
        
        CompilerMap.put("name",         new Column(JdbcTable.COMPILER, "Name", Column.Type.STRING));
        CompilerMap.put("vendor",       new Column(JdbcTable.COMPILER, "Vendor", Column.Type.STRING));
        CompilerMap.put("versionMajor", new Column(JdbcTable.COMPILER, "VersionMajor", Column.Type.INTEGER));
        CompilerMap.put("versionMinor", new Column(JdbcTable.COMPILER, "VersionMinor", Column.Type.INTEGER));
        CompilerMap.put("versionBuild", new Column(JdbcTable.COMPILER, "VersionBuild", Column.Type.INTEGER));
        
        MachineMap.put("name",   new Column(JdbcTable.MACHINE, "Name", Column.Type.STRING));
        MachineMap.put("vendor", new Column(JdbcTable.MACHINE, "Vendor", Column.Type.STRING));
        
        MpiMap.put("name",         new Column(JdbcTable.MPI, "Name", Column.Type.STRING));
        MpiMap.put("vendor",       new Column(JdbcTable.MPI, "Vendor", Column.Type.STRING));
        MpiMap.put("versionMajor", new Column(JdbcTable.MPI, "VersionMajor", Column.Type.INTEGER));
        MpiMap.put("versionMinor", new Column(JdbcTable.MPI, "VersionMinor", Column.Type.INTEGER));
        MpiMap.put("versionBuild", new Column(JdbcTable.MPI, "VersionBuild", Column.Type.INTEGER));
        
        OperatingSystemMap.put("name",              new Column(JdbcTable.OPERATING_SYSTEM, "Name", Column.Type.STRING));
        OperatingSystemMap.put("vendor",            new Column(JdbcTable.OPERATING_SYSTEM, "Vendor", Column.Type.STRING));
        OperatingSystemMap.put("versionMajor",      new Column(JdbcTable.OPERATING_SYSTEM, "VersionMajor", Column.Type.INTEGER));
        OperatingSystemMap.put("versionMinor",      new Column(JdbcTable.OPERATING_SYSTEM, "VersionMinor", Column.Type.INTEGER));
        OperatingSystemMap.put("versionBuild",      new Column(JdbcTable.OPERATING_SYSTEM, "VersionBuild", Column.Type.INTEGER));
        OperatingSystemMap.put("versionBuildMinor", new Column(JdbcTable.OPERATING_SYSTEM, "VersionBuildMinor", Column.Type.INTEGER));
        OperatingSystemMap.put("kernel",            new Column(JdbcTable.OPERATING_SYSTEM, "Kernel", Column.Type.STRING));
        
        ProcessorMap.put("name",           new Column(JdbcTable.PROCESSOR, "Name", Column.Type.STRING));
        ProcessorMap.put("vendor",         new Column(JdbcTable.PROCESSOR, "Vendor", Column.Type.STRING));
        ProcessorMap.put("architecture",   new Column(JdbcTable.PROCESSOR, "ProcessorArchitecture", Column.Type.STRING));
        ProcessorMap.put("clockSpeed",     new Column(JdbcTable.PROCESSOR, "ClockSpeedHz", Column.Type.INTEGER));
        ProcessorMap.put("cores",          new Column(JdbcTable.PROCESSOR, "CoresPerProcessor", Column.Type.INTEGER));
        ProcessorMap.put("threadsPerCore", new Column(JdbcTable.PROCESSOR, "ThreadsPerCore", Column.Type.INTEGER));
        
        RunMap.put("creator",         new Column(JdbcTable.RUN, "RunCreator", Column.Type.STRING));
        RunMap.put("insertionDate",   new Column(JdbcTable.RUN, "Date", Column.Type.DATE));
        RunMap.put("restricted",      new Column(JdbcTable.RUN, "Private", Column.Type.BOOLEAN));
        RunMap.put("runDate",         new Column(JdbcTable.RUN, "RunDate", Column.Type.DATE));
        RunMap.put("runId",           new Column(JdbcTable.RUN, "RunID", Column.Type.STRING));
        RunMap.put("tag",             new Column(JdbcTable.RUN, "Tag", Column.Type.STRING));
        RunMap.put("file",            new Column(JdbcTable.RUN, "File", Column.Type.STRING));
        
        ParameterMap.put("name",  new Column(JdbcTable.PARAMETER, "Name", Column.Type.STRING));
        ParameterMap.put("rank",  new Column(JdbcTable.PARAMETER, "Rank", Column.Type.RANK));
        ParameterMap.put("value", new Column(JdbcTable.PARAMETER, "Value", null));
        
        ResultMap.put("name",       new Column(JdbcTable.RESULT, "Name", Column.Type.STRING));
        ResultMap.put("rank",       new Column(JdbcTable.RESULT, "Rank", Column.Type.RANK));
        ResultMap.put("value",      new Column(JdbcTable.RESULT, "Value", Column.Type.DOUBLE));
        ResultMap.put("error",      new Column(JdbcTable.RESULT, "Error", Column.Type.DOUBLE));
        ResultMap.put("errorType",  new Column(JdbcTable.RESULT, "ErrorType", Column.Type.ERROR_TYPE));
        ResultMap.put("count",      new Column(JdbcTable.RESULT, "Count", Column.Type.LONG));
        ResultMap.put("pauseCount", new Column(JdbcTable.RESULT, "PauseCount", Column.Type.LONG));
        
        FlagMap.put("flag", new Column(JdbcTable.FLAGS, "Flag", Column.Type.STRING));
        
        RunFlagMap.put("runId", new Column(JdbcTable.RUN_FLAGS, "RunID", Column.Type.LONG));
        RunFlagMap.put("flagId", new Column(JdbcTable.RUN_FLAGS, "FlagID", Column.Type.LONG));
        
        SubRunMap.put("run", new Column(JdbcTable.SUB_RUN, "ParentRun", Column.Type.LONG));
        SubRunMap.put("sequence", new Column(JdbcTable.SUB_RUN, "Sequence", Column.Type.LONG));
        
        TableKeys.put(JdbcTable.ANALYSIS, Arrays.asList("creator", "notes", "dataPrivate", "date"));
        TableKeys.put(JdbcTable.APPLICATION, Arrays.asList("name", "versionMajor", "versionMinor", "versionBuild", "versionCode"));
        TableKeys.put(JdbcTable.COMPILER, Arrays.asList("name", "vendor", "versionMajor", "versionMinor", "versionBuild"));
        TableKeys.put(JdbcTable.FLAGS, Arrays.asList("flag"));
        TableKeys.put(JdbcTable.MACHINE, Arrays.asList("name", "vendor"));
        TableKeys.put(JdbcTable.MPI, Arrays.asList("name", "vendor", "versionMajor", "versionMinor", "versionBuild"));
        TableKeys.put(JdbcTable.OPERATING_SYSTEM, Arrays.asList("name", "versionMajor", "versionMinor", "versionBuild", "vendor", "versionBuildMinor", "kernel"));
        TableKeys.put(JdbcTable.PROCESSOR, Arrays.asList("name", "vendor", "architecture", "cores", "threadsPerCore", "clockSpeed"));
        TableKeys.put(JdbcTable.RUN, Arrays.asList("runId"));
    }
    
    static Collection<String> getSelectColumns(JdbcTable table) {
        final List<String> colNames = new ArrayList<>();
        
        for (Map.Entry<String, Column> entry : TableMaps.get(table).entrySet()) {
            colNames.add(entry.getValue().select());
        }
        
        return colNames;
    }
    
    static Collection<String> getBusinessKey(JdbcTable table) {
        return TableKeys.get(table);
    }
    
    static Map<String, Column> getTableMap(JdbcTable table) {
        return TableMaps.get(table);
    }
    
    final static Map<JdbcTable, Map<JdbcTable, Column>> joins = DefaultHashMap.mapOfHashMaps();
    
    static {
        joins.get(JdbcTable.RUN).put(JdbcTable.RUN_FLAGS, new Column(JdbcTable.RUN_FLAGS, "RunID", Column.Type.LONG));
        joins.get(JdbcTable.RUN).put(JdbcTable.SUB_RUN, new Column(JdbcTable.SUB_RUN, "ParentRun", Column.Type.LONG));
        joins.get(JdbcTable.FLAGS).put(JdbcTable.RUN_FLAGS, new Column(JdbcTable.RUN_FLAGS, "FlagID", Column.Type.LONG));
        
        joins.get(JdbcTable.APPLICATION).put(JdbcTable.RUN, new Column(JdbcTable.RUN, "Application", Column.Type.LONG));
        joins.get(JdbcTable.COMPILER).put(JdbcTable.RUN, new Column(JdbcTable.RUN, "Compiler", Column.Type.LONG));
        joins.get(JdbcTable.MACHINE).put(JdbcTable.RUN, new Column(JdbcTable.RUN, "Machine", Column.Type.LONG));
        joins.get(JdbcTable.MPI).put(JdbcTable.RUN, new Column(JdbcTable.RUN, "MPI", Column.Type.LONG));
        joins.get(JdbcTable.OPERATING_SYSTEM).put(JdbcTable.RUN, new Column(JdbcTable.RUN, "OperatingSystem", Column.Type.LONG));
        joins.get(JdbcTable.PROCESSOR).put(JdbcTable.RUN, new Column(JdbcTable.RUN, "Processor", Column.Type.LONG));
        
        joins.get(JdbcTable.SUB_RUN).put(JdbcTable.PARAMETER, new Column(JdbcTable.PARAMETER, "SubRunOwner", Column.Type.LONG));
        joins.get(JdbcTable.SUB_RUN).put(JdbcTable.RESULT, new Column(JdbcTable.RESULT, "SubRun", Column.Type.LONG));
        
        joins.get(JdbcTable.ANALYSIS).put(JdbcTable.ANALYSIS_CRITERA, new Column(JdbcTable.ANALYSIS_CRITERA, "AnalysisID", Column.Type.LONG));
        joins.get(JdbcTable.ANALYSIS).put(JdbcTable.ANALYSIS_DERIVED_DATA, new Column(JdbcTable.ANALYSIS_DERIVED_DATA, "AnalysisID", Column.Type.LONG));
        joins.get(JdbcTable.ANALYSIS).put(JdbcTable.ANALYSIS_GRAPH, new Column(JdbcTable.ANALYSIS_GRAPH, "AnalysisID", Column.Type.LONG));
        joins.get(JdbcTable.ANALYSIS).put(JdbcTable.ANALYSIS_AXIS_LABEL, new Column(JdbcTable.ANALYSIS_AXIS_LABEL, "AnalysisID", Column.Type.LONG));
    }
}
