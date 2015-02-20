package uk.co.awe.pmat.db.axis;

import uk.co.awe.pmat.db.Value;

/**
 * An {@code Enum} representation of the data types that can an axis can show.
 * 
 * @author AWE Plc copyright 2013
 */
public enum AxisValueType {
	/** Display the value field of values */
	VALUE,
	/** Display the error field of values */
	ERROR,
	/** Display the count field of values */
	COUNT,
	/** Display the pause count field of values */
	PAUSE_COUNT;

	/**
	 * Extract and return the field given by this {@code AxisValueType} from the
	 * given {@code Value} object. If the {@code Value} object does not hold
	 * numeric data then return {@code null}, i.e. for a {@code Value<String>}.
	 * 
	 * @param value
	 *            the {@code Value} object from which to extract the data.
	 * @return the value stored in the field of the given {@code Value}
	 *         specified by this {@code AxisValueType}, or {@code null}.
	 */
	public Double extractValue(Value<?> value) {
		if (value != null && value.getValue() instanceof Number) {
			@SuppressWarnings("unchecked")
			Value<? extends Number> val = (Value<? extends Number>) value;
			switch (this) {
			case VALUE:
				return val.getValue().doubleValue();
			case ERROR:
				return val.getError().doubleValue();
			case COUNT:
				return val.getCount().doubleValue();
			case PAUSE_COUNT:
				return val.getPauseCount().doubleValue();
			default:
				throw new IllegalStateException("Unknown AxisValueType " + this);
			}
		} else {
			return null;
		}
	}
}
