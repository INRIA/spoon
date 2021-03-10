/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.UnaryOperatorKind;

/**
 * Computes source code representation of the operator
 */
class OperatorHelper {

	public enum OperatorAssociativity {
		LEFT, RIGHT, NONE
	}

	private OperatorHelper() {
	}

	public static boolean isPrefixOperator(UnaryOperatorKind o) {
		return isSufixOperator(o) == false;
	}
	public static boolean isSufixOperator(UnaryOperatorKind o) {
		return o.name().startsWith("POST");
	}

	/**
	 * @return java source code representation of a pre or post unary operator.
	 */
	public static String getOperatorText(UnaryOperatorKind o) {
		switch (o) {
			case POS:
				return "+";
			case NEG:
				return "-";
			case NOT:
				return "!";
			case COMPL:
				return "~";
			case PREINC:
				return "++";
			case PREDEC:
				return "--";
			case POSTINC:
				return "++";
			case POSTDEC:
				return "--";
			default:
				throw new SpoonException("Unsupported operator " + o.name());
		}
	}

	/**
	 * @return java source code representation of a binary operator.
	 */
	public static String getOperatorText(BinaryOperatorKind o) {
		switch (o) {
			case OR:
				return "||";
			case AND:
				return "&&";
			case BITOR:
				return "|";
			case BITXOR:
				return "^";
			case BITAND:
				return "&";
			case EQ:
				return "==";
			case NE:
				return "!=";
			case LT:
				return "<";
			case GT:
				return ">";
			case LE:
				return "<=";
			case GE:
				return ">=";
			case SL:
				return "<<";
			case SR:
				return ">>";
			case USR:
				return ">>>";
			case PLUS:
				return "+";
			case MINUS:
				return "-";
			case MUL:
				return "*";
			case DIV:
				return "/";
			case MOD:
				return "%";
			case INSTANCEOF:
				return "instanceof";
			default:
				throw new SpoonException("Unsupported operator " + o.name());
		}
	}

	/**
	 * Get the precedence of a binary operator as defined by
	 * https://introcs.cs.princeton.edu/java/11precedence/
	 *
	 * @param o A binary operator kind.
	 * @return The precedence of the given operator.
	 */
	public static int getOperatorPrecedence(BinaryOperatorKind o) {
		switch (o) {
			case OR: // ||
				return 3;
			case AND: // &&
				return 4;
			case BITOR: // |
				return 5;
			case BITXOR: // ^
				return 6;
			case BITAND: // &
				return 7;
			case EQ: // ==
			case NE: // !=
				return 8;
			case LT: // <
			case GT: // >
			case LE: // <=
			case GE: // >=
			case INSTANCEOF:
				return 9;
			case SL: // <<
			case SR: // >>
			case USR: // >>>
				return 10;
			case PLUS: // +
			case MINUS: // -
				return 11;
			case MUL: // *
			case DIV: // /
			case MOD: // %
				return 12;
			default:
				throw new SpoonException("Unsupported operator " + o.name());
		}
	}

	/**
	 * Get the precedence of a unary operator as defined by
	 * https://introcs.cs.princeton.edu/java/11precedence/
	 *
	 * @param o A unary operator kind.
	 * @return The precedence of the given operator.
	 */
	public static int getOperatorPrecedence(UnaryOperatorKind o) {
		switch (o) {
			case POS:
			case NEG:
			case NOT:
			case COMPL:
			case PREINC:
			case PREDEC:
				return 14;
			case POSTINC:
			case POSTDEC:
				return 15;
			default:
				throw new SpoonException("Unsupported operator " + o.name());
		}
	}

	/**
	 * Get the associativity of a binary operator as defined by
	 * https://introcs.cs.princeton.edu/java/11precedence/
	 *
	 * All binary operators are left-associative in Java, except for the relational operators that
	 * have no associativity (i.e. you can't chain them).
	 *
	 * There's an exception: the ternary operator ?: is right-associative, but that's not an
	 * operator kind in Spoon so we don't deal with it.
	 *
	 * @param o A binary operator kind.
	 * @return The associativity of the operator.
	 */
	public static OperatorAssociativity getOperatorAssociativity(BinaryOperatorKind o) {
		switch (o) {
			case LT: // <
			case GT: // >
			case LE: // <=
			case GE: // >=
			case INSTANCEOF:
				return OperatorAssociativity.NONE;
			default:
				return OperatorAssociativity.LEFT;
		}
	}

	/**
	 * Get the associativity of a unary operator, as defined by
	 * https://introcs.cs.princeton.edu/java/11precedence/
	 *
	 * All unary operators are right-associative, except for post increment and decrement, which
	 * are not associative.
	 *
	 * @param o A unary operator kind.
	 * @return The associativity of the operator.
	 */
	public static OperatorAssociativity getOperatorAssociativity(UnaryOperatorKind o) {
		switch (o) {
			case POSTINC:
			case POSTDEC:
				return OperatorAssociativity.NONE;
			default:
				return OperatorAssociativity.RIGHT;
		}
	}
}
