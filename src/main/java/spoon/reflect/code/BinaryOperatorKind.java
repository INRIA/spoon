/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
