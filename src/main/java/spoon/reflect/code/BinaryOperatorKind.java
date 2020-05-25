/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This enumeration defines all the kinds of binary operators.
 */
public enum BinaryOperatorKind {

	/**
	 * Logical or.
	 */
	OR, // ||
	/**
	 * Logical and.
	 */
	AND, // &&
	/**
	 * Bit to bit or.
	 */
	BITOR, // |
	/**
	 * Bit to bit xor.
	 */
	BITXOR, // ^
	/**
	 * Bit to bit and.
	 */
	BITAND, // &
	/**
	 * Equality.
	 */
	EQ, // ==
	/**
	 * Inequality.
	 */
	NE, // !=
	/**
	 * Lower than comparison.
	 */
	LT, // <
	/**
	 * Greater than comparison.
	 */
	GT, // >
	/**
	 * Lower or equal comparison.
	 */
	LE, // <=
	/**
	 * Greater or equal comparison.
	 */
	GE, // >=
	/**
	 * Shift left.
	 */
	SL, // <<
	/**
	 * Shift right.
	 */
	SR, // >>
	/**
	 * Unsigned shift right.
	 */
	USR, // >>>
	/**
	 * Addition.
	 */
	PLUS, // +
	/**
	 * Substraction.
	 */
	MINUS, // -
	/**
	 * Multiplication.
	 */
	MUL, // *
	/**
	 * Division.
	 */
	DIV, // /
	/**
	 * Modulo.
	 */
	MOD, // %
	/**
	 * Instanceof (OO specific).
	 */
	INSTANCEOF
	// instanceof

}
