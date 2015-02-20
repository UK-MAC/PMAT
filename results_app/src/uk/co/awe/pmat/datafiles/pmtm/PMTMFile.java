package uk.co.awe.pmat.datafiles.pmtm;

import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.Run;

/**
 * A {@code RunFile} which is used to load in PMTM files.
 * 
 * @author AWE Plc copyright 2013
 */
public class PMTMFile extends DataFile<PMTMVersion> {

	/**
	 * Create a new {@code PMTMFile}.
	 * 
	 * @param data
	 *            the data object to populate from the file.
	 * @param applicationName
	 *            the name of the application for whom we are loading the data.
	 * @param version
	 *            the PMTM file version.
	 * @param tag
	 *            the PMTM run tag.
	 */
	public PMTMFile(Run data, String applicationName,
			Enum<PMTMVersion> version, String tag) {
		super(data, applicationName, version, tag);
	}

	/**
	 * Get the version of the PMTM library used to generate this file.
	 * 
	 * @return version The PMTM version
	 */
	public PMTMVersion getPmtmVersion() {
		return PMTMVersion.valueOf(getVersion().name());
	}

}
