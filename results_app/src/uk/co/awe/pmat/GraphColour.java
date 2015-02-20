package uk.co.awe.pmat;

import java.awt.Color;

/**
 * A collection of available named colours along with their RGB values.
 */
public enum GraphColour {

	/** GREEN */
	GREEN("Green", 0, 116, 123),
	/** GREEN80 */
	GREEN80("80% Green", 51, 144, 149),
	/** GREEN50 */
	GREEN50("50% Green", 128, 185, 189),
	// GREEN20("20% Green", 204, 227, 229),
	/** YELLOW */
	YELLOW("Yellow", 255, 199, 88),
	/** YELLOW80 */
	YELLOW80("80% Yellow", 255, 210, 122),
	/** YELLOW50 */
	YELLOW50("50% Yellow", 255, 227, 172),
	// YELLOW20("20% Yellow", 255, 244, 224),
	/** RED */
	RED("Red", 239, 67, 56),
	/** RED80 */
	RED80("80% Red", 243, 104, 96),
	/** RED50 */
	RED50("50% Red", 247, 160, 155),
	// RED20("20% Red", 252, 217, 215),
	/** STONE */
	STONE("Stone", 166, 148, 92),
	/** STONE80 */
	STONE80("80% Stone", 184, 170, 125),
	/** STONE50 */
	STONE50("50% Stone", 211, 201, 173),
	// STONE20("20% Stone", 237, 233, 222),
	/** BLUE */
	BLUE("Blue", 97, 155, 211),
	/** BLUE80 */
	BLUE80("80% Blue", 129, 175, 220),
	/** BLUE50 */
	BLUE50("50% Blue", 175, 205, 233),
	// BLUE20("20% Blue", 223, 235, 247),
	/** MINT */
	MINT("Mint", 77, 196, 207),
	/** MINT80 */
	MINT80("80% Mint", 113, 207, 217),
	/** MINT50 */
	MINT50("50% Mint", 166, 225, 231),
	// MINT20("20% Mint", 219, 243, 245),
	/** GREY */
	GREY("Grey", 165, 169, 172),
	/** GREY80 */
	GREY80("80% Grey", 183, 186, 189),
	/** GREY50 */
	GREY50("50% Grey", 210, 212, 213);
	// GREY20("20% Grey", 237, 238, 238);

	private final String name;
	private final Color color;

	/**
	 * Create a new {@code GraphColour}.
	 * 
	 * @param name
	 *            the name of the colour.
	 * @param r
	 *            the red colour value.
	 * @param g
	 *            the green colour value.
	 * @param b
	 *            the blue colour value.
	 */
	private GraphColour(String name, int r, int g, int b) {
		this.name = name;
		this.color = new Color(r, g, b);
	}

	/**
	 * Returns the {@code Color} of this {@code GraphColour}.
	 * 
	 * @return the {@code Color}.
	 * @see java.awt.Color
	 */
	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return name;
	}
}
