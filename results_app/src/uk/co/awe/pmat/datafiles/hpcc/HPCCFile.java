package uk.co.awe.pmat.datafiles.hpcc;

import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.Run;

/**
 * A {@code RunFile} which is used to load in HPCC files.
 * 
 * @author AWE Plc copyright 2013
 */
public class HPCCFile extends DataFile<HPCCVersion> {

	/**
	 * Create a new {@code HPCCFile}.
	 * 
	 * @param data
	 *            the data object to populate from the file.
	 * @param applicationName
	 *            the name of the application for whom we are loading the data.
	 * @param version
	 *            the HPCC file version.
	 */
	public HPCCFile(Run data, String applicationName, Enum<HPCCVersion> version) {
		super(data, applicationName, version, null);
	}
}
