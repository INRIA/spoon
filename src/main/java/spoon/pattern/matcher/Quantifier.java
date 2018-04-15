/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.pattern.matcher;

import spoon.pattern.node.RootNode;

/**
 * Defines a strategy used to resolve conflict between two {@link RootNode}s
 */
public enum Quantifier {
	/**
	 * Greedy quantifiers are considered "greedy" because they force the matcher to read in, or eat,
	 * the entire input prior to attempting the next match.
	 * If the next match attempt (the entire input) fails, the matcher backs off the input by one and tries again,
	 * repeating the process until a match is found or there are no more elements left to back off from.
	 */
	GREEDY,
	/**
	 * The reluctant quantifier takes the opposite approach: It start at the beginning of the input,
	 * then reluctantly eat one character at a time looking for a match.
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
