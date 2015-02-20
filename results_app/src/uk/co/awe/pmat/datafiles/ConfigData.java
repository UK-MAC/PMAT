package uk.co.awe.pmat.datafiles;

import java.util.List;
import uk.co.awe.pmat.ApplicationException;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Run;

/**
 * An interface for the configuration data that can be displayed and edited by
 * the user when a set of runs is loaded.
 * 
 * @author AWE Plc copyright 2013
 */
public interface ConfigData {

	/**
	 * Load the configuration data from a list of runs.
	 * 
	 * @param files
	 *            the files used to set configuration values.
	 * @throws DatabaseException
	 *             if no connection to the database can be created.
	 */
	void loadData(List<DataFile<?>> files) throws DatabaseException;

	/**
	 * Populate a run with configuration data ready to be saved into the
	 * database.
	 * 
	 * @param run
	 *            the run.
	 * @return the updated run.
	 * @throws ApplicationException
	 *             if insufficient state has been set to fully populate the run.
	 */
	Run populateRunData(Run run) throws ApplicationException;

}
