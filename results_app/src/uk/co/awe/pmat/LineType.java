package uk.co.awe.pmat;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * How a series on the graph is plotted.
 * 
 * @author AWE Plc copyright 2013
 */
public final class LineType implements XMLSerialisable {

	private static final Logger LOG = LoggerFactory.getLogger(LineType.class);

	private static final int DEFAULT_WIDTH = Constants.Plot.Line.DEFAULT_WIDTH;
	private static final LineStyle DEFAULT_STYLE = Constants.Plot.Line.DEFAULT_STYLE;
	private static final GraphColour DEFAULT_COLOUR = Constants.Plot.Line.DEFAULT_COLOUR;

	private LineStyle style;
	private GraphColour colour;
	private int width;

	/**
	 * Create a new LineType object.
	 * 
	 * @param lineStyle
	 *            the line style.
	 * @param color
	 *            the colour of the line.
	 * @param lineWidth
	 *            the width of the line.
	 */
	public LineType(LineStyle lineStyle, GraphColour color, int lineWidth) {
		this.style = lineStyle;
		this.colour = color;
		this.width = lineWidth;
	}

	/**
	 * Create a new {@code LineType} object using default values for the line.
	 */
	public LineType() {
		this(DEFAULT_STYLE, DEFAULT_COLOUR, DEFAULT_WIDTH);
	}

	/**
	 * Create a copy of this {@code LineType}.
	 * 
	 * @return a new {@code LineType}.
	 */
	public LineType copy() {
		return new LineType(style, colour, width);
	}

	/**
	 * Return this line type's {@code GraphColour}.
	 * 
	 * @return the colour.
	 */
	public GraphColour getColour() {
		return colour;
	}

	/**
	 * Set the line colour for this line type.
	 * 
	 * @param colour
	 *            The colour desired for this line, can't be null.
	 * @throws NullPointerException
	 *             if a null {@code GraphColour} is passed as the argument.
	 */
	public void setColour(GraphColour colour) throws NullPointerException {
		if (colour == null) {
			throw new NullPointerException();
		}
		this.colour = colour;
	}

	/**
	 * Return the line style of this line type.
	 * 
	 * @return the style of the line.
	 */
	public LineStyle getStyle() {
		return style;
	}

	/**
	 * Set the style of this line type.
	 * 
	 * @param lineStyle
	 *            The style of the line to use.
	 */
	public void setStyle(LineStyle lineStyle) {
		this.style = lineStyle;
	}

	/**
	 * Returns the width of the line.
	 * 
	 * @return the line width.
	 */
	public Integer getWidth() {
		return this.width;
	}

	/**
	 * Sets the width of the line.
	 * 
	 * @param lineWidth
	 *            The line width.
	 */
	public void setWidth(Integer lineWidth) {
		this.width = lineWidth;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LineType)) {
			return false;
		}
		final LineType other = (LineType) obj;
		if (this.style != other.style) {
			return false;
		}
		if (this.colour != other.colour) {
			return false;
		}
		if (this.width != other.width) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + (this.style != null ? this.style.hashCode() : 0);
		hash = 37 * hash + (this.colour != null ? this.colour.hashCode() : 0);
		hash = 37 * hash + this.width;
		return hash;
	}

	@Override
	public Element toXML() {
		Element node = new DOMElement(new QName(LineType.class.getSimpleName()));

		node.addAttribute(new QName("style"), style.name());
		node.addAttribute(new QName("color"), colour.name());
		node.addAttribute(new QName("width"), Integer.toString(width));

		return node;
	}

}
