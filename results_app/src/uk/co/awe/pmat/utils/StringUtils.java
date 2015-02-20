package uk.co.awe.pmat.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public class StringUtils {

	/**
	 * This class cannot be instantiated.
	 */
	private StringUtils() {
	}

	/**
	 * Utility method which capitalises the first letter of each word in a
	 * string.
	 * 
	 * @param text
	 *            The string to parse
	 * @return The string with the first letter of each word capitalised
	 */
	public static String capitaliseWords(final String text) {
        final String[] words = text.split(" ");
        List<String> capitalisedWords = new ArrayList<>(words.length);

        for (String word : words) {
            if (word.isEmpty()) { continue; }
            capitalisedWords.add(
                    word.substring(0, 1).toUpperCase() + word.substring(1));
        }

        return joinStrings(capitalisedWords, " ");
    }

	/**
	 * Utility method which adds spaces and initial capital letters to camel
	 * case words, e.g. "thisWord" -> "This Word".
	 * 
	 * @param words
	 *            The list of words to normalise
	 * @return The normalised words
	 */
	public static List<String> normaliseCamelCase(final List<String> words) {
        List<String> normalisedWords = new ArrayList<>();

        for (String word : words) {
            normalisedWords.add(normaliseCamelCase(word));
        }

        return normalisedWords;
    }

	/**
	 * Utility method which adds spaces and initial capital letters to a camel
	 * case word, e.g. "thisWord" -> "This Word".
	 * 
	 * @param word
	 *            The word to normalise
	 * @return The normalised word
	 */
	public static String normaliseCamelCase(final String word) {
        final List<String> tokens = new ArrayList<>();
        final StringBuilder token = new StringBuilder();
        for (int charIdx = 0; charIdx < word.length(); ++charIdx) {
            final char currentChar = word.charAt(charIdx);
            if (Character.isUpperCase(currentChar)) {
                if (token.length() != 0
                        && !Character.isUpperCase(token.charAt(token.length() - 1))) {
                    tokens.add(capitaliseWords(token.toString()));
                    token.setLength(0);
                }
            }
            token.append(currentChar);
        }
        if (!token.toString().isEmpty()) {
            tokens.add(capitaliseWords(token.toString()));
        }
        return joinStrings(tokens, " ");
    }

	/**
	 * Utility method to concatenate a collection of strings together, separated
	 * by a given deliminator.
	 * 
	 * @param words
	 *            The collection of string to concatenate
	 * @param delim
	 *            The deliminator to interject between the strings
	 * @return The concatenated string
	 */
	public static String joinStrings(final Collection<String> words,
			final String delim) {

		StringBuilder stringBuilder = new StringBuilder();

		boolean firstWord = true;
		for (String word : words) {
			if (!firstWord) {
				stringBuilder.append(delim);
			} else {
				firstWord = false;
			}
			stringBuilder.append(word);
		}

		return stringBuilder.toString();
	}

	public static String repeatChar(char c, int n) {
		if (n < 0) {
			throw new IllegalArgumentException("repeatChar given negative n");
		}

		char[] charArray = new char[n];
		Arrays.fill(charArray, c);
		return new String(charArray);
	}

	public static int closeness(String wordA, String wordB) {

		int closeness = 0;

		final String[] tokensA = wordA.split("[ _-]");
		final String[] tokensB = wordB.split("[ _-]");

		for (String tokenA : tokensA) {
			for (String tokenB : tokensB) {
				int a = wordCloseness(tokenA.toLowerCase(), tokenB
						.toLowerCase());
				int b = wordCloseness(tokenB.toLowerCase(), tokenA
						.toLowerCase());
				closeness += Math.max(a, b);
			}
		}

		return closeness;
	}

	private static int wordCloseness(String lhs, String rhs) {
		int closeness = 0;

		int idx = 0;

		while (idx < lhs.length() && idx < rhs.length()) {
			if (lhs.charAt(idx) == rhs.charAt(idx)) {
				++closeness;
				++idx;
			} else {
				break;
			}
		}

		return closeness;
	}
}
