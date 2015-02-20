package uk.co.awe.pmat.db.series;

/**
 * A class which represents a group in a series. I.e. a series could have the
 * groups 1, 2, 3, ... or "red", "yellow", "green", ... and a series group would
 * be the representation of each of these items.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class SeriesGroup implements Comparable<SeriesGroup> {

	/**
	 * Return the name of this series group.
	 * 
	 * @return The name of this series group
	 */
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SeriesGroup)) {
			return false;
		}
		final SeriesGroup other = (SeriesGroup) obj;
		return getName().equals(other.getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public int compareTo(SeriesGroup other) {
		return getName().compareTo(other.getName());
	}

}
