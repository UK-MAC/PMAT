package uk.co.awe.pmat.db.xml;

import java.util.List;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;

/**
 * 
 * @author AWE Plc copyright 2013
 */
final class RestrictionElementHandler implements ElementHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(RestrictionElementHandler.class);

	private final List<Restriction> restrictions;

	/**
	 * Create a new {@code RestrictionElementHandler}.
	 * 
	 * @param dbMapping
	 *            the mapping used to communicate with the database.
	 * @param restrictions
	 *            the restrictions we are deserialising.
	 */
	RestrictionElementHandler(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}

	@Override
	public void onStart(ElementPath path) {
		LOG.debug("Start of node: " + path.getCurrent().getName());

		final Element node = path.getCurrent();
		final Category category = Category.valueOf(node
				.attributeValue(new QName("category")));
		final String field = node.attributeValue(new QName("field"));
		final Rank rank = Rank.valueOf(node.attributeValue(new QName("rank")));
		final Comparator comparator = Comparator.valueOf(node
				.attributeValue(new QName("comparator")));
		final String valueType = node.attributeValue(new QName("valueType"));
		final Value<?> value = Value.valueOf(valueType, node
				.attributeValue(new QName("value")));

		restrictions.add(DatabaseManager.getConnection().newRestriction(
				category, field, rank, comparator, value));
	}

	@Override
	public void onEnd(ElementPath path) {
		LOG.debug("End of node: " + path.getCurrent().getName());
	}
}
