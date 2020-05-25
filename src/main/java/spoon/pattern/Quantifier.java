/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

/**
 * Defines a matching strategy for pattern parameters, default is {@link #GREEDY}.
 */
public enum Quantifier {
	/**
	 * Force the matcher to read in, or eat,
	 * the entire input prior to attempting the next match (default).
	 * If the next match attempt (the entire input) fails, the matcher backs off the input by one and tries again,
	 * repeating the process until a match is found or there are no more elements left to back off from.
	 */
	GREEDY,
	/**
	 * The reluctant quantifier takes the opposite approach: It start at the beginning of the input,
	 * then reluctantly eats one character at a time looking for a match.
	 * The last thing it tries is the entire input.
	 */
	RELUCTANT,
	/**
	 * The possessive quantifier always eats the entire input string,
	 * trying once (and only once) for a match. Unlike the greedy quantifiers, possessive quantifiers never back off,
	 * even if doing so would allow the overall match to succeed.
	 */
	POSSESSIVE
}
