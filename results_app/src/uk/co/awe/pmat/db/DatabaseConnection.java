package uk.co.awe.pmat.db;

import java.util.Collection;
import java.util.List;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.db.series.Series;

/**
 * An interface to allow the application code to communicate to the database.
 * This is designed to separate the database logic (currently implemented via
 * Hibernate) with the application logic.
 * 
 * @author AWE Plc copyright 2013
 */
public interface DatabaseConnection {

	// ------------------------------------------------------------------------/
	// Restrictions -----------------------------------------------------------/

	/**
	 * Obtain a new database restriction. This is used to restrict which {@code
	 * DataSet}s are returned from the database.
	 * 
	 * @param category
	 *            the aspect of the data we are restricting, e.g. if we are
	 *            restricting on system state.
	 * @param field
	 *            the field within the category we are restricting, e.g. we
	 *            might restrict on the name field of the application category.
	 * @param rank
	 *            which rank we are restricting, might also be all or any.
	 * @param comparator
	 *            how we are restricting, i.e. equality restriction.
	 * @param value
	 *            the value we are restricting against.
	 * @return the new restriction.
	 */
	Restriction newRestriction(Category category, String field, Rank rank,
			Comparator comparator, Value<?> value);

	/**
	 * Return the possible fields for the given {@code Category} from all the
	 * {@code DataSet}s subject to the given restrictions.
	 * 
	 * @param restrictions
	 *            the restrictions to apply.
	 * @param category
	 *            the category for which to get the possible fields for.
	 * @return the fields.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<String> getFields(Collection<Restriction> restrictions,
			Category category) throws DatabaseException;

	/**
	 * Return the possible ranks from all the {@code DataSet}s subject to the
	 * given restrictions.
	 * 
	 * @param restrictions
	 *            the restrictions to apply.
	 * @param category
	 *            the category for which to get the possible values for.
	 * @param field
	 *            the field for which to get the possible values for.
	 * @return the ranks.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<Rank> getRanks(List<Restriction> restrictions, Category category,
			String field) throws DatabaseException;

	/**
	 * Return the possible values for the given {@code Category}, field and
	 * {@code Rank} from all the {@code DataSet}s subject to the given
	 * restrictions.
	 * 
	 * @param restrictions
	 *            the restrictions to apply.
	 * @param category
	 *            the category for which to get the possible values for.
	 * @param field
	 *            the field for which to get the possible values for.
	 * @param rank
	 *            the rank for which to get the possible values for.
	 * @return the values.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<Value<?>> getValues(Collection<Restriction> restrictions,
			Category category, String field, Rank rank)
			throws DatabaseException;

	// ------------------------------------------------------------------------/
	// Meta data --------------------------------------------------------------/

	/**
	 * Returns all the meta-data stored in the database for a given type.
	 * 
	 * @param type
	 *            the type of the {@code MetaData}.
	 * @return the meta-data.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<MetaData> getAllMetaData(MetaData.Type type) throws DatabaseException;

	/**
	 * Return all the meta-data associated with all the {@code Run}s subject to
	 * the given restrictions. Note that even when the restrictions is empty
	 * that this may not match {@link #getAllMetaData(MetaData.Type)} as meta-
	 * data which are not associated with any {@code Run} will <i>not</i> be
	 * returned.
	 * 
	 * @param restrictions
	 *            the restrictions to apply.
	 * @param type
	 *            the meta-data type to return.
	 * @return the matching meta-data.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<MetaData> getMetaData(Collection<Restriction> restrictions,
			MetaData.Type type) throws DatabaseException;

	/**
	 * Return the vendor names for a given meta-data type.
	 * 
	 * @param type
	 *            type of the {@code MetaData}.
	 * @return the vendor names.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<String> getMetaDataVendors(MetaData.Type type)
			throws DatabaseException;

	// ------------------------------------------------------------------------/
	// Analyses ---------------------------------------------------------------/

	/**
	 * Return all the analyses saved in the database with the given creator
	 * name, or simply all the analyses if the creator name is {@code null}.
	 * 
	 * @param creatorName
	 *            the analyses user name.
	 * @return the analyses.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<Analysis> getSavedAnalyses(String creatorName)
			throws DatabaseException;

	/**
	 * Perform an analysis query on the database using the given axes, rank,
	 * restrictions and series and return the data organised into a {@code
	 * DataGrid}.
	 * 
	 * @param xAxis
	 *            the x-axis of the analysis.
	 * @param yAxis
	 *            the y-axis of the analysis.
	 * @param rank
	 *            the rank of the y-axis.
	 * @param restrictions
	 *            the restrictions to apply.
	 * @param series
	 *            the series to apply.
	 * @return the analysis data.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	DataGrid getDataGrid(Axis xAxis, Axis yAxis, Rank rank,
			Collection<Restriction> restrictions, Collection<Series> series)
			throws DatabaseException;

	// ------------------------------------------------------------------------/
	// Runs -------------------------------------------------------------------/

	/**
	 * Returns all the {@code Run}s stored in the database.
	 * 
	 * @return the runs.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	List<Run> getRuns() throws DatabaseException;

	/**
	 * Returns all the {@code DataSet}s associated with the given {@code Run}.
	 * 
	 * @param run
	 *            the run.
	 * @return the datasets.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	Collection<RunData> getDataSets(Run run) throws DatabaseException;

	// ------------------------------------------------------------------------/
	// Saving/Deleting --------------------------------------------------------/

	/**
	 * Save an object into the database.
	 * 
	 * @param dbObject
	 *            the object to save.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	void save(DatabaseObject dbObject) throws DatabaseException;

	/**
	 * Delete an object from the database.
	 * 
	 * @param dbObject
	 *            the object to delete.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	void delete(DatabaseObject dbObject) throws DatabaseException;

	/**
	 * Try the database connection using a simple query.
	 * 
	 * @param config
	 *            the application configuration.
	 * @return {@code true} if the connection was successful, {@code false}
	 *         otherwise.
	 */
	boolean tryConnection(Configuration config);

	/**
	 * Update the given database object in the database.
	 * 
	 * @param oldObject
	 *            the database object to update.
	 * @param newObject
	 *            the new object to update it to.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	public void update(DatabaseObject oldObject, DatabaseObject newObject)
			throws DatabaseException;

	/**
	 * Get the build flags store against the given run.
	 * 
	 * @param run
	 *            the run to get the flags for.
	 * @return the flags.
	 * @throws DatabaseException
	 *             if an error occurs querying the database.
	 */
	public Collection<String> getFlags(Run run) throws DatabaseException;

	public void close();

}
