package uk.co.awe.pmat.datafiles.pmtm;

/**
 * An {@code Enum} representation of the different possible versions of PMTM
 * files which can be processed.
 * 
 * 
 * @author AWE Plc copyright 2013
 */
public enum PMTMVersion {

	// Versions must be maintained in historical order for isEarlier to work.

	/** Version 0.1 */
	V0_1("0.1"),
	/** Version 0.2 */
	V0_2("0.2"),
	/** Version 0.2.2 */
	V0_2_2("0.2.2"),
	/** Version 0.2.3 */
	V0_2_3("0.2.3"),
	/** Version 0.2.4 */
	V0_2_4("0.2.4"),
	/** Version 0.2.5 */
	V0_2_5("0.2.5"),
	/** Version 0.2.6 */
	V0_2_6("0.2.6"),
	/** Version 0.2.7 */
	V0_2_7("0.2.7"),
	/** Version 0.3.0 */
	V0_3_0("O.3.0"),
	/** Version 0.3.1 */
	V0_3_1("0.3.1"),
	/** Version 1.0.0 */
	V1_0_0("1.0.0"),
	/** Version 2.0.0 */
	V2_0_0("2.0.0"),
	/** Version 2.1.0 */
	V2_1_0("2.1.0"),
	/** Version 2.1.1 */
	V2_1_1("2.1.1"),
	/** Version 2.2.0 */
	V2_2_0("2.2.0"),
	/** Version 2.2.1 */
	V2_2_1("2.2.1"),
	/** Version 2.3.0 */
	V2_3_0("2.3.0"),
	/** Version 2.3.1 */
	V2_3_1("2.3.1"),
        /** Version 2.4.0 */
        V2_4_0("2.4.0");

	private String versionAsString;

	/**
	 * Create a new {@code PMTMVersion}.
	 * 
	 * @param version
	 *            the version string that will be read from the files.
	 */
	private PMTMVersion(String version) {
		versionAsString = version;
	}

	/**
	 * Returns a constant based on the version passed in. Different to valueOf
	 * because the string used is not the same as the constants name but the
	 * value reported by the program
	 * 
	 * @param version
	 *            the version as a string
	 * @return the version as an {@code Enum}
	 */
	public static PMTMVersion getFromString(String version) {
		for (PMTMVersion ver : PMTMVersion.values()) {
			if (ver.toString().equalsIgnoreCase(version)) {
				return ver;
			}
		}
		throw new IllegalArgumentException("Unknown PMTM version :" + version);
	}

	@Override
	public String toString() {
		return versionAsString;
	}

	/**
	 * Compare this version against another and return whether this one is
	 * earlier or not.
	 * 
	 * @param version
	 *            The SkampiVersion to compare against
	 * @return True if this version is an earlier version, otherwise false
	 */
	public boolean isEarlierThan(PMTMVersion version) {
		return this.compareTo(version) < 0;
	}

	/**
	 * Compare this version against another and return whether this one is equal
	 * to or later than it.
	 * 
	 * @param version
	 *            The SkampiVersion to compare against
	 * @return True if this version is the same or later than the comparator,
	 *         otherwise false
	 */
	public boolean isLaterThanOrEqualTo(PMTMVersion version) {
		return this.compareTo(version) >= 0;
	}

}
