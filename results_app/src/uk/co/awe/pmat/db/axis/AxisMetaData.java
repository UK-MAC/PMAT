package uk.co.awe.pmat.db.axis;

import uk.co.awe.pmat.db.MetaData.Type;

/**
 * An {@link Axis} which is used to display meta-data.
 * 
 * @author AWE Plc copyright 2013
 */
class AxisMetaData extends Axis {

	private final AxisName name;
	private final Type metaType;

	/**
	 * Creates a new {@code AxisMetaData}.
	 * 
	 * @param name
	 *            the axis name.
	 * @param metaType
	 *            the meta-data type.
	 */
	AxisMetaData(AxisName name, Type metaType) {
		this.name = name;
		this.metaType = metaType;
	}

	@Override
	public AxisType getType() {
		return AxisType.META_DATA;
	}

	@Override
	public AxisName getAxisName() {
		return name;
	}

	@Override
	public Object getSubType() {
		return metaType;
	}

	@Override
	public String displayName() {
		return metaType.displayName();
	}

	@Override
	public String toString() {
		return metaType.name();
	}

}
