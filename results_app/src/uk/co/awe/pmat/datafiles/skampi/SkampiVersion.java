package uk.co.awe.pmat.datafiles.skampi;

/**
 * An {@code Enum} representation of the different possible versions of SkaMPI
 * files which can be processed.
 * 
 * @author AWE Plc copyright 2013
 */
public enum SkampiVersion {

	// Versions, add in historical order for isEarlier to work.

	/** Version 5.0.4 rev 355 */
	V5_0_4_355("5.0.4 rev. 355");

	private String versionAsString;

	/**
	 * Create a new {@code SkampiVersion}.
	 * 
	 * @param version
	 *            the version string that will be read from the files.
	 */
	private SkampiVersion(String version) {
		versionAsString = version;
	}

	/**
	 * Returns a constant based on the version passed in. Different to valueOf
	 * because the string used is not the same as the constants name but the
	 * value reported by the program.
	 * 
	 * @param version
	 *            the version as a string.
	 * @return the version as an {@code Enum}.
	 */
	public static SkampiVersion getFromString(String version) {
		for (SkampiVersion ver : SkampiVersion.values()) {
			if (ver.toString().equalsIgnoreCase(version)) {
				return ver;
			}
		}
		throw new IllegalArgumentException("Unknown Skampi version :" + version);
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
	 *            the SkampiVersion to compare against.
	 * @return {@code true} if this version is an earlier version, otherwise
	 *         {@code false}.
	 */
	public boolean isEarlier(SkampiVersion version) {
		return this.compareTo(version) < 0;
	}
}
