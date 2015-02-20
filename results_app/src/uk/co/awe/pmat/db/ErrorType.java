package uk.co.awe.pmat.db;

/**
 * An {@code Enum} representation of the error type of {@link Value} objects.
 * 
 * @author AWE Plc copyright 2013
 */
public enum ErrorType {
	/** Error used in SkaMPI results files. */
	SKAMPI_STANDARD,
	/** Error used in PMTM results files. */
	PMTM_STANDARD,
	/** No error. */
	NONE
}
