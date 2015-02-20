package uk.co.awe.pmat.datafiles;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;

/**
 * A base class for all the different types of files that can be loaded via the
 * application and then saved to the database. This is really a wrapper for the
 * {@code Data} object being loaded, which also stores some extra information
 * needed by the data file loader.
 * 
 * @param <T>
 *            the version {@code Enum} of the data file type.
 * @author AWE Plc copyright 2013
 */
public abstract class DataFile<T extends Enum<T>> {

	private final Run data;
	private final String applicationName;
	private final Enum<T> version;
	private final String tag;

	/**
	 * Create a new {@code DataFile}.
	 * 
	 * @param data
	 *            the data object to populate from the file.
	 * @param applicationName
	 *            the name of the application for whom we are loading the data.
	 * @param version
	 *            the data file version.
	 * @param tag
	 *            the tag to save against the data.
	 */
	protected DataFile(Run data, String applicationName, Enum<T> version,
			String tag) {
		this.data = data;
		this.applicationName = applicationName;
		this.version = version;
		this.tag = tag;
	}

	/**
	 * Return the version of the data file we are loading.
	 * 
	 * @return the version.
	 */
	protected Enum<T> getVersion() {
		return version;
	}

	/**
	 * Add a data set to this collection.
	 * 
	 * @param dataSet
	 *            the data set to add.
	 */
	public void addDataSet(RunData dataSet) {
		data.addDataSet(dataSet);
	}

	/**
	 * Return a {@code Data} view of this {@code DataFile}.
	 * 
	 * @return this {@code DataFile} as a {@code Data} object.
	 */
	public Run asRun() {
		return data;
	}

	/**
	 * Get the name of application for which we are holding data.
	 * 
	 * @return the application name.
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Add all data sets from a collection into this {@code DataFile}.
	 * 
	 * @param dataSets
	 *            the collection of data sets to add.
	 */
	public void addAllDataSets(Collection<RunData> dataSets) {
		data.addAllDataSets(dataSets);
	}

	/**
	 * Return the date on which the data was run.
	 * 
	 * @return the run date.
	 */
	public Date getRunDate() {
		return data.getRunDate();
	}

	/**
	 * Return whether the data is private to this institution.
	 * 
	 * @return {@code true} if the data is private, {@code false} otherwise.
	 */
	public boolean getDataIsPrivate() {
		return data.isRestricted();
	}

	/**
	 * Return the user who created the data.
	 * 
	 * @return the user.
	 */
	public String getUser() {
		return data.getCreator();
	}

	/**
	 * Return all stored data sets.
	 * 
	 * @return the data sets.
	 */
	public List<RunData> getDataSets() {
		return data.getDataSets();
	}

	/**
	 * Return the run tag.
	 * 
	 * @return the run tag.
	 */
	public String getTag() {
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataFile)) {
			return false;
		}
		final DataFile<?> other = (DataFile<?>) obj;
		if (applicationName != other.getApplicationName()
				&& (applicationName == null || !applicationName.equals(other
						.getApplicationName()))) {
			return false;
		}
		if (version != other.getVersion()) {
			return false;
		}
		return data.equals(other.data);
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 11 * hash
				+ (applicationName != null ? applicationName.hashCode() : 0);
		hash = 11 * hash + (version != null ? version.hashCode() : 0);
		return hash;
	}

}
