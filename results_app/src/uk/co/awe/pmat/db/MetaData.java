package uk.co.awe.pmat.db;

import java.util.LinkedHashMap;
import java.util.Map;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * An interface to represent all the meta-data attached to a {@code Run}. This
 * could include the machine, operating system, application, etc. These objects
 * must also be serialisable to an XML file, by implementing the {@code
 * XMLSerialisable} interface.
 * 
 * @author AWE Plc copyright 2013
 */
public interface MetaData extends DatabaseObject<MetaData>, XMLSerialisable {

	/**
	 * Returns a field in the meta-data with the given name.
	 * 
	 * @param name
	 *            the name of the field.
	 * @return the field.
	 */
	Object getData(String name);

	/**
	 * Returns this meta-data as a map of the field names against the field
	 * values.
	 * 
	 * @return the meta-data as a map.
	 */
	Map<String, Object> getDataMap();

	/**
	 * Return the {@link Type} of the meta-data.
	 * 
	 * @return the meta-data type.
	 */
	Type getType();

	/**
	 * Returns the name that should be displayed for this meta-data object. This
	 * name should given enough information about this meta-data to be unique to
	 * this object. This allows the meta-data to be distinguished easily in a
	 * list or table.
	 * 
	 * @return the name of the meta-data.
	 */
	String displayName();

	/**
	 * An {@code Enum} representation of the different types of meta-data that
	 * can be attached to a {@code Run}.
	 */
	enum Type {
		/** APPLICATION */
		APPLICATION("Application", "application"),
		/** COMPILER */
		COMPILER("Compiler", "compiler"),
		/** MPI */
		MPI("MPI", "mpi"),
		/** MACHINE */
		MACHINE("Machine", "machine"),
		/** OPERATING_SYSTEM */
		OPERATING_SYSTEM("Operating System", "operatingSystem"),
		/** PROCESSOR */
		PROCESSOR("Processor", "processor");

		private final String displayName;
		private final String fieldName;

		/**
		 * Create a new {@code Type}.
		 * 
		 * @param displayName
		 *            the name to display in table headers, etc.
		 * @param fieldName
		 *            the name to use when accessing this meta-data
		 *            reflectively.
		 */
		private Type(String displayName, String fieldName) {
			this.displayName = displayName;
			this.fieldName = fieldName;
		}

		/**
		 * Returns the name to display for this {@code Type} to use in display
		 * components; table headers, etc.
		 * 
		 * @return the display name.
		 */
		public String displayName() {
			return displayName;
		}

		/**
		 * Returns the name to use when accessing this meta-data from another
		 * object using reflection, i.e. when using
		 * {@link Class#getField(String)}.
		 * 
		 * @return the field name associated with this {@code Type}.
		 */
		public String asFieldName() {
			return fieldName;
		}

		/**
		 * Return the fields used in displaying this database object.
		 * 
		 * @return an ordered map containing the fields the name of the fields
		 *         and their type.
		 */
		public LinkedHashMap<String, Class<?>> getFields() {
			switch (this) {
			case APPLICATION:
				return Application.getDataFields();
			case COMPILER:
				return Compiler.getDataFields();
			case MACHINE:
				return Machine.getDataFields();
			case MPI:
				return Mpi.getDataFields();
			case OPERATING_SYSTEM:
				return OperatingSystem.getDataFields();
			case PROCESSOR:
				return Processor.getDataFields();
			default:
				throw new IllegalArgumentException("Unknown type " + this);
			}
		}
	}

}
