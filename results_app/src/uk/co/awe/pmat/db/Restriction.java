package uk.co.awe.pmat.db;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * An interface to represent the restriction of data in the application, i.e.
 * when we do not want to analyse all the data in the database, but perhaps only
 * that which matches the "machine name == 'Machine A'" restriction.
 * 
 * @author AWE Plc copyright 2013
 */
public interface Restriction extends XMLSerialisable {

	/**
	 * A variable used to specify that no restrictions are currently in place.
	 */
	Collection<Restriction> NONE = Collections.emptyList();

	/**
	 * Return the category of the restriction, i.e. whether we are restricting
	 * on system state, application, a result, etc.
	 * 
	 * @return the restriction category.
	 */
	Category getCategory();

	/**
	 * Return the field of the restriction. This will depend on the restriction
	 * category, i.e. for system state it will be the system state type, e.g.
	 * machine, or for result it will the result name.
	 * 
	 * @return the restriction field.
	 */
	String getField();

	/**
	 * Return the ranks on which we are applying this restriction.
	 * 
	 * @return the restriction rank.
	 */
	Rank getRank();

	/**
	 * Return the comparator which will be used in the restriction, i.e. equal
	 * to, greater than, etc.
	 * 
	 * @return the restriction comparator.
	 */
	Comparator getComparator();

	/**
	 * Return the value we are restricting against using the comparator.
	 * 
	 * @return the restriction value.
	 */
	Value<?> getValue();

	/**
	 * Return the list of database IDs of {@code SubRun}s that match the
	 * restriction.
	 * 
	 * @return the database IDs.
	 * @throws DatabaseException
	 *             if an error occurs finding the IDs.
	 */
	List<Long> getMatchingIDs() throws DatabaseException;
}
