package uk.co.awe.pmat.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import static uk.co.awe.pmat.utils.StringUtils.joinStrings;

/**
 * A class to hold utilities methods used on arrays.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ArrayUtils {

	/**
	 * This class cannot be instantiated.
	 */
	private ArrayUtils() {
	}

	/**
	 * Utility method to return the names of classes in a {@code Class} array.
	 * Similar to {@link java.util.Arrays#toString()} but uses the {@code
	 * getSimpleName} method instead of the {@code toString} method on the
	 * {@code Class} objects.
	 * 
	 * @param classArray
	 *            the array of {@code Class} objects.
	 * @return a string containing the names of the classes.
	 */
	public static String toString(final Class<?>[] classArray) {
		List<String> names = new ArrayList<String>();

		for (Class<?> cls : classArray) {
			names.add(cls.getSimpleName());
		}

		return "[" + joinStrings(names, ", ") + "]";
	}

	/**
	 * Utility method to check whether an array contains another array.
	 * 
	 * @param <T>
	 *            the type of the arrays.
	 * @param outer
	 *            the containing array.
	 * @param inner
	 *            the contained array.
	 * @return {@code true} if the outer array contains the inner array, {@code
	 *         false} otherwise.
	 */
	public static <T> boolean arrayContains(final T[] outer, final T[] inner) {

		for (T innerItem : inner) {
			boolean found = arrayContains(outer, innerItem);
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Utility method to check whether an array contains a given element. If the
	 * array is sorted, use
	 * {@link java.util.Arrays#binarySearch(Object[], Object)} instead.
	 * 
	 * @param <T>
	 *            the type of the arrays.
	 * @param array
	 *            the array to search.
	 * @param element
	 *            the element to search for.
	 * @return {@code true} if the given element is contained in the array,
	 *         {@code false} otherwise.
	 */
	public static <T> boolean arrayContains(T[] array, T element) {
		for (T el : array) {
			if (el.equals(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an unmodifiable view of an array, useful for exporting public
	 * arrays in classes.
	 * 
	 * @param <T>
	 *            the type of the array.
	 * @param array
	 *            the array for which we creating the list view.
	 * @return a list view of the array.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> asUnmodifiableList(T... array) {
		return new UnmodifiableList<T>(array);
	}

	/**
	 * Unmodifiable list class used in {@link #asUnmodifiableList}.
	 * 
	 * @param <E>
	 *            the list element type.
	 */
	private static class UnmodifiableList<E> extends AbstractList<E> implements
			RandomAccess {

		private final E[] array;

		/**
		 * Create a new {@code UnmodifiableList}.
		 * 
		 * @param array
		 *            the array for which this list is a view.
		 */
		UnmodifiableList(E[] array) {
			this.array = array;
		}

		@Override
		public E get(int index) {
			return array[index];
		}

		@Override
		public int size() {
			return array.length;
		}
	}

}
