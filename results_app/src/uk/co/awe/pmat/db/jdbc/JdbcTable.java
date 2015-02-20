package uk.co.awe.pmat.db.jdbc;

import java.text.MessageFormat;
import uk.co.awe.pmat.db.criteria.Comparator;

enum JdbcTable {
	APPLICATION("Application"), COMPILER("Compiler"), MACHINE("Machine"), MPI(
			"MPI"), OPERATING_SYSTEM("OperatingSystem"), PROCESSOR("Processor"), RESULT(
			"Result"), PARAMETER("Parameter"), RUN("Run"), SUB_RUN("SubRun"), RUN_FLAGS(
			"RunFlags"), FLAGS("Flags"), ANALYSIS("Analysis"), ANALYSIS_CRITERA(
			"AnalysisCriteria"), ANALYSIS_DERIVED_DATA("AnalysisDerivedData"), ANALYSIS_GRAPH(
			"AnalysisGraph"), ANALYSIS_AXIS_LABEL("AnalysisAxisLabel");

	private final String tableName;

	private JdbcTable(String tableName) {
		this.tableName = tableName;
	}

	String tableName() {
		return tableName;
	}

	String tableSelect() {
		return tableName + " AS " + name().toLowerCase();
	}

	String tableReference() {
		return name().toLowerCase();
	}

	String tableColumn(String name) {
		return tableReference() + "."
				+ JdbcMapping.getTableMap(this).get(name).name();
	}

	String joinLeft(JdbcTable table) {
		return joinLeft(table, null);
	}

	String joinLeft(JdbcTable table, String tableRef) {
		if (tableRef == null) {
			tableRef = table.name().toLowerCase();
		}
		final String join = "{0} AS {1} ON {2} = {3}";
		return MessageFormat.format(join,
		/* 0 */table.tableName(),
		/* 1 */tableRef,
		/* 2 */JdbcMapping.joins.get(table).get(this).select(),
		/* 3 */tableRef + ".ID");
	}

	String joinRight(JdbcTable table) {
		return joinRight(table, null);
	}

	String joinRight(JdbcTable table, String tableRef) {
		if (tableRef == null) {
			tableRef = table.name().toLowerCase();
		}
		final String join = "{0} AS {1} ON {2} = {3}";
		return MessageFormat.format(join,
		/* 0 */table.tableName(),
		/* 1 */tableRef,
		/* 2 */JdbcMapping.joins.get(this).get(table).select(tableRef, null),
		/* 3 */tableReference() + ".ID");
	}

	String compare(String name, Object object, Comparator comparator) {
		if (object == null) {
			switch (comparator) {
			case EQ:
				return JdbcMapping.getTableMap(this).get(name).name()
						+ " IS NULL";
			case GE: // Fall through
			case GT: // Fall through
			case LE: // Fall through
			case LT: // Fall through
			case NE:
				return JdbcMapping.getTableMap(this).get(name).name()
						+ " IS NOT NULL";
			default:
				throw new IllegalArgumentException("Unknown comparator "
						+ comparator);
			}

		} else {
			switch (comparator) {
			case EQ:
				return JdbcMapping.getTableMap(this).get(name).name() + " = ?";
			case GE:
				return JdbcMapping.getTableMap(this).get(name).name() + " >= ?";
			case GT:
				return JdbcMapping.getTableMap(this).get(name).name() + " > ?";
			case LE:
				return JdbcMapping.getTableMap(this).get(name).name() + " <= ?";
			case LT:
				return JdbcMapping.getTableMap(this).get(name).name() + " < ?";
			case NE:
				return JdbcMapping.getTableMap(this).get(name).name() + " <> ?";
			default:
				throw new IllegalArgumentException("Unknown comparator "
						+ comparator);
			}
		}
	}

	String equals(String name, Object object) {
		return compare(name, object, Comparator.EQ);
	}
}
