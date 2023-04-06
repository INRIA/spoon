/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;

/**
 * Class for determining whether or not an expression requires round brackets in order to preserve
 * AST structure (and consequently semantics).
 */
class RoundBracketAnalyzer {

	enum EncloseInRoundBrackets {
		YES, NO, UNKNOWN;
	}

	private RoundBracketAnalyzer() {
	}

	/**
	 * @param expr A unary or binary expr.
	 * @return true if the expr should be enclosed in round brackets.
	 */
	static EncloseInRoundBrackets requiresRoundBrackets(CtExpression<?> expr) {
		return isNestedOperator(expr)
				? nestedOperatorRequiresRoundBrackets(expr)
				: EncloseInRoundBrackets.UNKNOWN;
	}

	/**
	 * Assuming that operator is a nested operator (i.e. both operator and its parent are
	 * {@link CtUnaryOperator} or {@link CtBinaryOperator}), determine whether or not it must be
	 * enclosed in round brackets.
	 *
	 * Given an element <code>e</code> with a parent <code>p</code>, we must parenthesize
	 * <code>e</code> if any of the following are true.
	 *
	 * <ul>
	 *     <li>The parent p is a unary operator</li>
	 *     <li>The parent p is a binary operator, and <code>precedence(p) > precedence(e></code></li>
	 *     <li>The parent p is a binary operator, <code>precedence(p) == precedence(e)</code>,
	 *     e appears as the X-hand-side operand of p, and e's operator is Y-associative, where
	 *     <code>X != Y</code></li>
	 * </ul>
	 *
	 * Note that the final rule is necessary to preserve syntactical structure, but it is not
	 * required for preserving semantics.
	 *
	 * @param nestedOperator A nested operator.
	 * @return Whether or not to enclose the nested operator in round brackets.
	 */
	private static EncloseInRoundBrackets nestedOperatorRequiresRoundBrackets(CtExpression<?> nestedOperator) {
		if (nestedOperator.getParent() instanceof CtUnaryOperator) {
			return EncloseInRoundBrackets.YES;
		}

		OperatorHelper.OperatorAssociativity associativity = getOperatorAssociativity(nestedOperator);
		OperatorHelper.OperatorAssociativity positionInParent = getPositionInParent(nestedOperator);

		int parentPrecedence = getOperatorPrecedence(nestedOperator.getParent());
		int precedence = getOperatorPrecedence(nestedOperator);
		return precedence < parentPrecedence
				|| (precedence == parentPrecedence && associativity != positionInParent)
				? EncloseInRoundBrackets.YES
				: EncloseInRoundBrackets.NO;
	}

	private static boolean isNestedOperator(CtElement e) {
		return e.isParentInitialized() && isOperator(e) && isOperator(e.getParent());
	}

	private static boolean isOperator(CtElement e) {
		return e instanceof CtBinaryOperator || e instanceof CtUnaryOperator;
	}

	private static int getOperatorPrecedence(CtElement e) {
		if (e instanceof CtBinaryOperator) {
			return OperatorHelper.getOperatorPrecedence(((CtBinaryOperator<?>) e).getKind());
		} else if (e instanceof CtUnaryOperator) {
			return OperatorHelper.getOperatorPrecedence(((CtUnaryOperator<?>) e).getKind());
		} else {
			return 0;
		}
	}

	private static OperatorHelper.OperatorAssociativity getOperatorAssociativity(CtElement e) {
		if (e instanceof CtBinaryOperator) {
			return OperatorHelper.getOperatorAssociativity(((CtBinaryOperator<?>) e).getKind());
		} else if (e instanceof CtUnaryOperator) {
			return OperatorHelper.getOperatorAssociativity(((CtUnaryOperator<?>) e).getKind());
		} else {
			return OperatorHelper.OperatorAssociativity.NONE;
		}
	}

	private static OperatorHelper.OperatorAssociativity getPositionInParent(CtElement e) {
		CtElement parent = e.getParent();
		if (parent instanceof CtBinaryOperator) {
			return ((CtBinaryOperator<?>) parent).getLeftHandOperand() == e
					? OperatorHelper.OperatorAssociativity.LEFT
					: OperatorHelper.OperatorAssociativity.RIGHT;
		} else {
			return OperatorHelper.OperatorAssociativity.NONE;
		}
	}
}
