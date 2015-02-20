package uk.co.awe.pmat.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;

/**
 * 
 * @author AWE Plc copyright 2013
 */
final class JdbcRestriction implements Restriction {

	private final static Logger LOG = LoggerFactory
			.getLogger(JdbcRestriction.class);

	private final transient JdbcHelper helper;
	private final Category category;
	private final String field;
	private final Rank rank;
	private final Comparator comparator;
	private final Value<?> value;

	private List<Long> cachedIds = null;

	public JdbcRestriction(JdbcHelper helper, Category category, String field,
			Rank rank, Comparator comparator, Value<?> value) {
		this.helper = helper;
		this.category = category;
		this.field = field;
		this.rank = rank;
		this.comparator = comparator;
		this.value = value;
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public String getField() {
		return field;
	}

	@Override
	public Rank getRank() {
		return rank;
	}

	@Override
	public Comparator getComparator() {
		return comparator;
	}

	@Override
	public Value<?> getValue() {
		return value;
	}

	@Override
	public Element toXML() {
		Element node = new DOMElement(new QName(Restriction.class
				.getSimpleName()));

		node.add(new DOMAttribute(new QName("category"), category.name()));
		node.add(new DOMAttribute(new QName("field"), field));
		node.add(new DOMAttribute(new QName("comparator"), comparator.name()));
		node.add(new DOMAttribute(new QName("rank"), rank.name()));
		node.add(new DOMAttribute(new QName("value"), value.getValue()
				.toString()));
		node.add(new DOMAttribute(new QName("valueType"), value.getValue()
				.getClass().getSimpleName()));

		return node;
	}

	@Override
	public List<Long> getMatchingIDs() throws DatabaseException {
		if (cachedIds == null) {
			cachedIds = performQuery();
		}
		return cachedIds;
	}

	private List<Long> performQuery() throws DatabaseException {
        final List<Object> sqlParams = new ArrayList<>();
        
        String query = MessageFormat.format("SELECT DISTINCT {0}.ID AS ID FROM {1}",
                JdbcTable.SUB_RUN.tableReference(),
                helper.getSchema() + "." + JdbcTable.SUB_RUN.tableSelect());
        
        Object val = value.getValue();
        
        switch (getCategory()) {
            case APPLICATION: // Fall through
            case COMPILER: // Fall through
            case MACHINE: // Fall through
            case MPI: // Fall through
            case OPERATING_SYSTEM: // Fall through
            case PROCESSOR:
                JdbcTable table = JdbcUtils.categoryToTable(category);
                query += MessageFormat.format(" JOIN {0} JOIN {1} WHERE {2}.{3}",
                /* 0 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.joinLeft(JdbcTable.RUN),
                /* 1 */ helper.getSchema() + "." + JdbcTable.RUN.joinLeft(table),
                /* 2 */ table.tableReference(),
                /* 3 */ table.compare(field, val, comparator));
                if (val != null) { sqlParams.add(val); }
                break;
            case PARAMETER:
                if (val != null) {
                    query = MessageFormat.format("SELECT {0}.ID AS ID, {1} FROM {2} JOIN {3} WHERE {4} AND {5}.{6}{7}",
                    /* 0 */ JdbcTable.SUB_RUN.tableReference(),
                    /* 1 */ JdbcMapping.getTableMap(JdbcTable.PARAMETER).get("value").select(),
                    /* 2 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.tableSelect(),
                    /* 3 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.PARAMETER),
                    /* 4 */ JdbcTable.PARAMETER.compare("name", field, Comparator.EQ),
                    /* 5 */ JdbcTable.PARAMETER.tableReference(),
                    /* 6 */ val.getClass().getSimpleName(),
                    /* 7 */ JdbcTable.PARAMETER.compare("value", val, comparator));
                }
                if (field != null) { sqlParams.add(field); }
                if (val != null) { sqlParams.add(val); }
                break;
            case RESULT:
               query += MessageFormat.format(" JOIN {0} WHERE {1} AND {2}.{3}",
                /* 0 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.joinRight(JdbcTable.RESULT),
                /* 1 */ JdbcTable.RESULT.compare("name", field, Comparator.EQ),
                /* 2 */ JdbcTable.RESULT.tableReference(),
                /* 3 */ JdbcTable.RESULT.compare("value", val, comparator));
                if (field != null) { sqlParams.add(field); }
                if (val != null) { sqlParams.add(val); }
                break;
            case RUN:
                query += MessageFormat.format(" JOIN {0} WHERE {1}.{2}",
                /* 0 */ helper.getSchema() + "." + JdbcTable.SUB_RUN.joinLeft(JdbcTable.RUN),
                /* 1 */ JdbcTable.RUN.tableReference(),
                /* 1 */ JdbcTable.RUN.compare(field, val, comparator));
                if (val != null) { sqlParams.add(val); }
                break;
            default:
                throw new IllegalStateException("Unknown category " + getCategory());
        }
        
        try (final ResultSet resultSet = helper.executeQuery(query, sqlParams)) {
            final List<Long> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(resultSet.getLong("ID"));
            }
            return ids;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }
}
