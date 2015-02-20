package uk.co.awe.pmat.datafiles;

import java.util.List;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Run;

/**
 * An interface for the per run configuration data which is added to the runs
 * before they are saved.
 * 
 * @author AWE Plc copyright 2013
 */
public interface DataConfig {

	/**
	 * Clear all configuration data for all runs.
	 */
	void clear();

	/**
	 * Load configuration data for the specified runs, created using the given
	 * file.
	 * 
	 * @param runs
	 *            the runs for which to load the configuration data..
	 * @throws DatabaseException
	 *             if no connection to the database could be created.
	 */
	void loadRuns(List<Run> runs) throws DatabaseException;

	/**
	 * Populate a specific run with the configuration data associated with that
	 * run.
	 * 
	 * @param run
	 *            the run whose data we are populating.
	 */
	void populateRun(Run run);

}
