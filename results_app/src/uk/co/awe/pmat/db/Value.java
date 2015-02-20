package uk.co.awe.pmat.db;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * 
 * @param <T>
 * @author AWE Plc copyright 2013
 */
@SuppressWarnings("rawtypes")
public final class Value<T extends Comparable> implements Comparable<Value<?>>,
		XMLSerialisable {

	private final String name;
	private final Rank rank;
	private final T value;
	private final T error;
	private final ErrorType errorType;
	private final Long count;
	private final Long pauseCount;
	private final Class<?> valueClass;

	/**
	 * Create a {@code Value<T>} object with the given value, where {@code T} is
	 * derived from the given type argument.
	 * 
	 * @param type
	 *            the type of the value.
	 * @param value
	 *            the value.
	 * @return a {@code Value} holding the cast value.
	 */
	public static Value<?> valueOf(String type, String value) {
        if (type.equals(String.class.getSimpleName())) {
            return new Value<>("", Rank.UNKNOWN, value);
        } else if (type.equals(Integer.class.getSimpleName())) {
            return new Value<>("", Rank.UNKNOWN, Integer.valueOf(value));
        } else if (type.equals(Double.class.getSimpleName())) {
            return new Value<>("", Rank.UNKNOWN, Double.valueOf(value));
        } else {
            throw new IllegalArgumentException("Unknown type " + type);
        }
    }

	/**
	 * Convert the given {@code Value<?>} object into a {@code Value<Double>}
	 * object if possible, otherwise return {@code null}.
	 * 
	 * @param val
	 *            the value to convert.
	 * @return the value as a {@code Value<Double>} object.
	 */
	@SuppressWarnings("unchecked")
	public static Value<Double> doubleValue(Value<?> val) {
		final Object value = val.getValue();
		if (value instanceof Integer) {
			final Double dblVal = ((Integer) value).doubleValue();
			final Double dblErr = val.getError() == null ? null
					: ((Integer) val.getError()).doubleValue();
			return val.updateValue(dblVal, dblErr);
		} else if (value instanceof Double) {
			return (Value<Double>) val;
		} else {
			return val.updateValue(null, null);
		}
	}

	/**
	 * Create a new {@code Value}.
	 * 
	 * @param name
	 *            the name of the value.
	 * @param value
	 *            the value.
	 * @param error
	 *            the error of the value.
	 * @param errorType
	 *            the error type.
	 * @param rank
	 *            the rank of the value.
	 * @param count
	 *            the sample count of the value.
	 * @param pauseCount
	 *            the pause count of the value.
	 */
	public Value(String name, T value, T error, ErrorType errorType, Rank rank,
			Long count, Long pauseCount) {
		if (name == null || rank == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.value = value;
		this.error = error;
		this.errorType = errorType;
		this.rank = rank;
		this.count = count;
		this.pauseCount = pauseCount;
		this.valueClass = (value != null ? value.getClass() : Void.class);
	}

	/**
	 * Create a new {@code Value}.
	 * 
	 * @param name
	 *            the name of the value.
	 * @param rank
	 *            the rank of the value.
	 * @param value
	 *            the value.
	 */
	public Value(String name, Rank rank, T value) {
		this(name, value, null, null, rank, null, null);
	}

	/**
	 * Return a copy of this {@code Value} but with given name.
	 * 
	 * @param nme
	 *            the new name.
	 * @return this {@code Value} with the name updated.
	 */
	public Value<T> updateName(String nme) {
        return new Value<>(nme, value, error, errorType, rank, count, pauseCount);
    }

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Value<?> other) {
		if (other.valueClass.equals(valueClass)) {
			if (value == null && other.value == null) {
				return 0;
			} else {
				return value == null ? -1 : value.compareTo(other.value);
			}
		}
		return -1;
	}

	/**
	 * Return the name of this value.
	 * 
	 * @return the value name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the value of this value.
	 * 
	 * @return the value value.
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Return the sample count of this value.
	 * 
	 * @return the value sample count.
	 */
	public Long getCount() {
		return count;
	}

	/**
	 * Return the error of this value.
	 * 
	 * @return the value error.
	 */
	public T getError() {
		return error;
	}

	/**
	 * Return the error type of this value.
	 * 
	 * @return the value error type.
	 */
	public ErrorType getErrorType() {
		return errorType;
	}

	/**
	 * Return the pause count of this value.
	 * 
	 * @return the value pause count.
	 */
	public Long getPauseCount() {
		return pauseCount;
	}

	/**
	 * Return the rank of this value.
	 * 
	 * @return the value rank.
	 */
	public Rank getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return name + ": " + (value == null ? NULL : value.toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Value)) {
			return false;
		}
		final Value<?> other = (Value<?>) obj;
		if ((this.name == null) ? (other.name != null) : !this.name
				.equals(other.name)) {
			return false;
		}
		if (this.rank != other.rank
				&& (this.rank == null || !this.rank.equals(other.rank))) {
			return false;
		}
		if (this.value != other.value
				&& (this.value == null || !this.value.equals(other.value))) {
			return false;
		}
		if (this.error != other.error
				&& (this.error == null || !this.error.equals(other.error))) {
			return false;
		}
		if (this.errorType != other.errorType) {
			return false;
		}
		if (this.count != other.count
				&& (this.count == null || !this.count.equals(other.count))) {
			return false;
		}
		if (this.pauseCount != other.pauseCount
				&& (this.pauseCount == null || !this.pauseCount
						.equals(other.pauseCount))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 53 * hash + (this.rank != null ? this.rank.hashCode() : 0);
		hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
		hash = 53 * hash + (this.error != null ? this.error.hashCode() : 0);
		hash = 53 * hash
				+ (this.errorType != null ? this.errorType.hashCode() : 0);
		hash = 53 * hash + (this.count != null ? this.count.hashCode() : 0);
		hash = 53 * hash
				+ (this.pauseCount != null ? this.pauseCount.hashCode() : 0);
		return hash;
	}

	/**
	 * Return a copy of this {@code Value} but with the value updated.
	 * 
	 * @param newVal
	 *            the new value.
	 * @return this {@code Value} but with updated value.
	 */
	public Value<T> updateValue(T newVal) {
        return new Value<>(name, newVal, error, errorType, rank, count, pauseCount);
    }

	/**
	 * Return a {@code Value<Double>} with the same name, error type, rank, etc.
	 * as this {@code Value}, but with the given value and error.
	 * 
	 * @param dblVal
	 *            the new value.
	 * @param dblErr
	 *            the new error.
	 * @return this {@code Value} but with updated value and error.
	 */
	public Value<Double> updateValue(Double dblVal, Double dblErr) {
        return new Value<>(name, dblVal, dblErr, errorType, rank, count, pauseCount);
    }

	@Override
	public Element toXML() {
		final Element node = new DOMElement(getClass().getSimpleName());

		node.add(new DOMAttribute(new QName("name"), name));
		node
				.add(new DOMAttribute(new QName("type"), valueClass
						.getSimpleName()));
		node.add(new DOMAttribute(new QName("rank"), rank.name()));
		node.add(new DOMAttribute(new QName("value"), value.toString()));

		if (error != null) {
			node.add(new DOMAttribute(new QName("error"), error.toString()));
		}
		if (errorType != null) {
			node.add(new DOMAttribute(new QName("errorType"), errorType
					.toString()));
		}
		if (count != null) {
			node.add(new DOMAttribute(new QName("count"), count.toString()));
		}
		if (pauseCount != null) {
			node.add(new DOMAttribute(new QName("pauseCount"), pauseCount
					.toString()));
		}

		return node;
	}

}
