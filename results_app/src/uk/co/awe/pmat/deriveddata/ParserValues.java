package uk.co.awe.pmat.deriveddata;

import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.MetaData.Type;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Value;

/**
 * The {@code ParserValues} class is a container class which holds a number of
 * sub classes of {@code DerivedData}. These classes are used, alongside the
 * derived data functions, to build up an object tree representation of the
 * parsed derived data equation.
 * 
 * @author AWE Plc copyright 2013
 */
public final class ParserValues {

	/**
	 * This class cannot be instantiated.
	 */
	private ParserValues() {
	}

	/**
	 * The {@code Constant} class represents constant values in the derived data
	 * equations, i.e. Strings and Numbers.
	 * 
	 * @param <T>
	 *            The type of the constant
	 */
	public static final class Constant<T extends Comparable<T>> implements
			DerivedData {

		private final Value<T> value;

		/**
		 * Create a new {@code Constant} representing the given value.
		 * 
		 * @param value
		 *            The value to hold
		 */
		public Constant(T value) {
            this.value = new Value<>(value.getClass().getSimpleName(), Rank.UNKNOWN, value);
        }

		@Override
		public Class<?>[] returnTypes() {
			return new Class<?>[] { value.getValue().getClass() };
		}

		@Override
		public String evaluate(DataGrid dataGrid) throws DerivedDataException {
			final String name = value.getValue().toString();
			for (DataGrid.Row row : dataGrid.getRows()) {
				row.addyValue(name, value);
			}
			return name;
		}
	}

	/**
	 * The {@code Variable} class represents the data to which the derived data
	 * equation is applied, e.g. "'Communication Time' + 'Computation Time'"
	 * would parse to two variables being the 'Communication Time' and
	 * 'Computation Time' to which the operator '+' would be applied.
	 */
	public static final class Variable implements DerivedData {

		/**
		 * An {@code Enum} representation of the different types of {@code
		 * Variable}s that can be handled by derived data functions.
		 */
		public enum Type {
			/** A parameter variable. */
			PARAMETER,
			/** A result variable */
			RESULT
		}

		private final Type type;
		private final String name;

		/**
		 * Create a new {@code Variable} representing the given data variable.
		 * 
		 * @param type
		 *            the type of data this variable is representing.
		 * @param name
		 *            the name of the data this {@code Variable} is
		 *            representing.
		 */
		public Variable(Type type, String name) {
			this.type = type;
			this.name = name;
		}

		/**
		 * Return the name of the variable.
		 * 
		 * @return the variable name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Return the type of the variable.
		 * 
		 * @return the variable type.
		 */
		public Type getType() {
			return type;
		}

		@Override
		public Class<?>[] returnTypes() {
			return new Class<?>[] { Double.class };
		}

		@Override
		public String evaluate(DataGrid dataGrid) throws DerivedDataException {
			// The column should already exist in the dataGrid so nothing to do.
			return name;
		}
	}

	/**
	 * The {@code Property} class represents some meta data which can be used to
	 * filter or constrain the {@code Variable}s retrieved, e.g. we could be
	 * looking for the values of 'Computation Time' {@code Variable} where the
	 * machine name is equal to 'MachineA'.
	 */
	public static final class Property implements DerivedData {

		private final MetaData.Type type;

		/**
		 * Create a new {@code Property} representing the given meta data.
		 * 
		 * @param type
		 *            The class of the entity this meta data is for
		 */
		public Property(String type) {
			this.type = MetaData.Type.valueOf(type);
		}

		/**
		 * Return the type of the property.
		 * 
		 * @return the property type.
		 */
		public Type getType() {
			return type;
		}

		@Override
		public Class<?>[] returnTypes() {
			return new Class<?>[] { String.class };
		}

		@Override
		public String evaluate(DataGrid dataGrid) throws DerivedDataException {
			// The column should already exist in the dataGrid so nothing to do.
			return type.asFieldName();
		}
	}

}
