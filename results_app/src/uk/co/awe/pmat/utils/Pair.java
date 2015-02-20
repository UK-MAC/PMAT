package uk.co.awe.pmat.utils;

/**
 * Simple (immutable) pair class as Java doesn't have one.
 * 
 * @author AWE Plc copyright 2013
 * @param <T>
 *            The type of the first item in the pair
 * @param <U>
 *            The type of the second item in the pair
 */
public final class Pair<T, U> {

	private final T first;
	private final U second;

	/**
	 * Construct an immutable pair object.
	 * 
	 * @param first
	 *            The first item in the pair
	 * @param second
	 *            The second item in the pair
	 */
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", first, second);
	}

	/**
	 * Return the first item in the pair.
	 * 
	 * @return The first item in the pair
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Return the second item in the pair.
	 * 
	 * @return The second item in the pair
	 */
	public U getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (first != other.first
				&& (first == null || !first.equals(other.first))) {
			return false;
		}
		if (second != other.second
				&& (second == null || !second.equals(other.second))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + (first != null ? first.hashCode() : 0);
		hash = 61 * hash + (second != null ? second.hashCode() : 0);
		return hash;
	}

}
