/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.SpoonException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Internal;

import java.util.Optional;
import java.util.Set;

/**
 * Computes source code representation of the operator
 */
@Internal
public final class OperatorHelper {

	public enum OperatorAssociativity {
		LEFT, RIGHT, NONE
	}

	private OperatorHelper() {
	}

	/**
	 * Checks if the operator is a prefix operator.
	 * @param o the operator
	 * @return true if it is a prefix operator, false otherwise
	 */
	public static boolean isPrefixOperator(UnaryOperatorKind o) {
		return !isSufixOperator(o);
	}

	/**
	 * Checks if the operator is a suffix operator.
	 * @param o the operator
	 * @return true if it is a suffix operator, false otherwise
	 */
	public static boolean isSufixOperator(UnaryOperatorKind o) {
		return o.name().startsWith("POST");
	}

	/**
	 * Gets the representation of the operator in the source code. For example, POS will return "+".
	 *
	 * @param o the operator
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
	 * Gets the representation of the operator in the source code. For example, OR will return "||".
	 *
	 * @param o the operator
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

	private static final Set<Class<?>> WHOLE_NUMBERS = Set.of(
		byte.class,
		short.class,
		int.class,
		long.class
	);

	private static final Set<Class<?>> NUMBERS_PROMOTED_TO_INT = Set.of(
		byte.class,
		short.class,
		char.class
	);

	private static boolean isIntegralType(CtTypeReference<?> ctTypeReference) {
		return ctTypeReference.isPrimitive()
			// see https://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.2.1
			&& (WHOLE_NUMBERS.contains(ctTypeReference.getActualClass()) || ctTypeReference.getActualClass().equals(char.class));
	}

	private static boolean isNumericType(CtTypeReference<?> ctTypeReference) {
		return ctTypeReference.isPrimitive() && !ctTypeReference.getActualClass().equals(boolean.class);
	}

	/**
	 * When using an unary-operator on an operand, the operand type might be changed before the operator is applied.
	 * For example, the result of {@code ~((short) 1)} will be of type {@code int} and not {@code short}.
	 *
	 * @param operand the operand to apply the operator on
	 * @return the type after applying the operator or {@link Optional#empty()} if promotion does not apply
	 */
	private static Optional<CtTypeReference<?>> unaryNumericPromotion(CtExpression<?> operand) {
		// if the operand is of type Byte, Short, Character, Integer, Long, Float, or Double it is subject
		// to unboxing (§5.1.8)
		CtTypeReference<?> operandType = operand.getType().unbox();
		// check if unary numeric promotion applies
		if (!isNumericType(operandType)) {
			return Optional.empty();
		}

		// if the operand is of type byte, short, or char, it is promoted to a value of type int by a widening
		// primitive conversion (§5.1.2).
		if (NUMBERS_PROMOTED_TO_INT.contains(operandType.getActualClass())) {
			return Optional.of(operandType.getFactory().Type().integerPrimitiveType());
		}

		// otherwise, the operand is not converted at all.
		return Optional.of(operandType);
	}

	private static Optional<CtTypeReference<?>> binaryNumericPromotion(
		CtExpression<?> left,
		CtExpression<?> right
	) {
		// If any operand is of a reference type, it is subjected to unboxing conversion (§5.1.8).
		CtTypeReference<?> leftType = left.getType().unbox();
		CtTypeReference<?> rightType = right.getType().unbox();
		TypeFactory typeFactory = leftType.getFactory().Type();

		// each of which must denote a value that is convertible to a numeric type
		if (!isNumericType(leftType) || !isNumericType(rightType)) {
			return Optional.empty();
		}

		CtTypeReference<?> doubleType = typeFactory.doublePrimitiveType();
		// If either operand is of type double, the other is converted to double.
		if (leftType.equals(doubleType) || rightType.equals(doubleType)) {
			return Optional.of(doubleType);
		}

		// Otherwise, if either operand is of type float, the other is converted to float.
		CtTypeReference<?> floatType = typeFactory.floatPrimitiveType();
		if (leftType.equals(floatType) || rightType.equals(floatType)) {
			return Optional.of(floatType);
		}

		// Otherwise, if either operand is of type long, the other is converted to long.
		CtTypeReference<?> longType = typeFactory.longPrimitiveType();
		if (leftType.equals(longType) || rightType.equals(longType)) {
			return Optional.of(longType);
		}

		// Otherwise, both operands are converted to type int.
		return Optional.of(typeFactory.integerPrimitiveType());
	}

	/**
	 * Get the promoted type of the binary operator, as defined by the Java Language Specification.
	 * <p>
	 * Before an operator is applied, the type of the operands might be changed.
	 * This is called <i>promotion</i>.
	 * For example {@code 1 + 1.0} has an int and a double as operands.
	 * The left operand is promoted to a double, so that the left and right operand have the same type.
	 *
	 * @param operator the operator
	 * @param left the left operand, {@link CtExpression#getFactory()} must not return {@code null}.
	 * @param right the right operand
	 * @return the promoted type or {@link Optional#empty()} if promotion does not apply or the operation is invalid.
	 *         Not every operator is defined for every combination of operands.
	 *         For example {@code 1 << 1.0} is invalid.
	 *         In this case, {@link Optional#empty()} is returned.
	 * @throws UnsupportedOperationException if the operator is {@link BinaryOperatorKind#INSTANCEOF} or an unknown operator.
	 * @see <a href="https://docs.oracle.com/javase/specs/jls/se11/html/jls-5.html#jls-5.6.2">JLS 5.6.2</a>
	 */
	public static Optional<CtTypeReference<?>> getPromotedType(
		BinaryOperatorKind operator,
		CtExpression<?> left,
		CtExpression<?> right
	) {
		TypeFactory typeFactory = left.getFactory().Type();
		switch (operator) {
			// logical operators
			case AND:
			case OR: {
				CtTypeReference<?> booleanType = typeFactory.booleanPrimitiveType();
				if (!left.getType().equals(booleanType) || !right.getType().equals(booleanType)) {
					return Optional.empty();
				}

				return Optional.of(booleanType);
			}
			// shift operators are special:
			case SL:
			case SR:
			case USR: {
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.19
				// on each operand unary numeric promotion is performed
				CtTypeReference<?> promotedLeft = unaryNumericPromotion(left).orElse(null);
				CtTypeReference<?> promotedRight = unaryNumericPromotion(right).orElse(null);

				if (promotedLeft == null || promotedRight == null) {
					return Optional.empty();
				}

				// after promotion, both operands have to be an integral type:
				if (!isIntegralType(promotedLeft) || !isIntegralType(promotedRight)) {
					return Optional.empty();
				}

				// The type of the shift expression is the promoted type of the left-hand operand.
				return Optional.of(promotedLeft);
			}
			case INSTANCEOF:
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.20.2
				// Not implemented, because it is not necessary for the current use case.
				throw new UnsupportedOperationException("instanceof is not yet implemented");
			// on the following operators binary numeric promotion is performed:
			case EQ:
			case NE: {
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.21
				CtTypeReference<?> leftType = left.getType().unbox();
				CtTypeReference<?> rightType = right.getType().unbox();

				// The equality operators may be used to compare two operands that are convertible (§5.1.8)
				// to numeric type, or two operands of type boolean or Boolean, or two operands that are each
				// of either reference type or the null type. All other cases result in a compile-time error.
				CtTypeReference<?> booleanType = typeFactory.booleanPrimitiveType();
				return binaryNumericPromotion(left, right).or(() -> {
					// check if both operands are of type boolean or Boolean
					// if so they will be promoted to the primitive type boolean
					if (leftType.equals(rightType) && leftType.equals(booleanType)) {
						return Optional.of(booleanType);
					}

					// if both operands are of a reference type
					if (!leftType.isPrimitive() && !rightType.isPrimitive()) {
						// It is a compile-time error if it is impossible to convert the type of
						// either operand to the type of the other by a casting conversion (§5.5).
						// The run-time values of the two operands would necessarily be unequal
						// (ignoring the case where both values are null).
						CtTypeReference<?> nullType = typeFactory.nullType();
						if (leftType.equals(nullType)) {
							return Optional.of(rightType);
						}

						if (rightType.equals(nullType)) {
							return Optional.of(leftType);
						}

						if (leftType.isSubtypeOf(rightType)) {
							return Optional.of(rightType);
						}

						if (rightType.isSubtypeOf(leftType)) {
							return Optional.of(rightType);
						}

						return Optional.empty();
					}

					return Optional.empty();
				});
			}
			case LT:
			case LE:
			case GT:
			case GE:
			case MUL:
			case DIV:
			case MOD:
			case MINUS:
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.20
				return binaryNumericPromotion(left, right);
			case PLUS:
				return binaryNumericPromotion(left, right).or(() -> {
					// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.18.1
					//
					// If the type of either operand of a + operator is String, then the operation is
					// string concatenation.
					CtTypeReference<?> stringType = typeFactory.stringType();
					if (left.getType().equals(stringType) || right.getType().equals(stringType)) {
						return Optional.of(stringType);
					}

					return Optional.empty();
				});
			case BITAND:
			case BITXOR:
			case BITOR: {
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.22
				CtTypeReference<?> leftType = left.getType().unbox();
				CtTypeReference<?> rightType = right.getType().unbox();

				Set<CtTypeReference<?>> floatingPointNumbers = Set.of(
					typeFactory.floatPrimitiveType(),
					typeFactory.doublePrimitiveType()
				);
				if (floatingPointNumbers.contains(leftType) || floatingPointNumbers.contains(rightType)) {
					return Optional.empty();
				}

				if (leftType.equals(rightType) && leftType.equals(typeFactory.booleanPrimitiveType())) {
					return Optional.of(leftType);
				}

				return binaryNumericPromotion(left, right);
			}
			default:
				throw new UnsupportedOperationException("Unknown operator: " + operator);
		}
	}

	/**
	 * Gets the promoted type of the unary operator, as defined by the Java Language Specification.
	 * <p>
	 * Before an operator is applied, the type of the operand might be changed.
	 * This is called <i>promotion</i>.
	 * For example {@code -((short) 1)} has an operand of type short.
	 * The operand is promoted to an int, before the operator is applied.
	 *
	 * @param operator the operator
	 * @param operand the operand, {@link CtExpression#getFactory()} must not return {@code null}.
	 * @return the promoted type or {@link Optional#empty()} if promotion does not apply or the operation is invalid.
	 *         Not every operator is defined for every combination of operands.
	 *         For example {@code !1} is invalid.
	 *         In this case, {@link Optional#empty()} is returned.
	 * @throws UnsupportedOperationException if the operator is an unknown operator.
	 * @see <a href="https://docs.oracle.com/javase/specs/jls/se11/html/jls-5.html#jls-5.6.1">JLS 5.6.1</a>
	 */
	public static Optional<CtTypeReference<?>> getPromotedType(
		UnaryOperatorKind operator,
		CtExpression<?> operand
	) {
		TypeFactory typeFactory = operand.getFactory().Type();
		CtTypeReference<?> operandType = operand.getType();
		switch (operator) {
			case COMPL:
				if (isIntegralType(operandType.unbox())) {
					return unaryNumericPromotion(operand);
				}

				return Optional.empty();
			case POS:
			case NEG:
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.15.3
				return unaryNumericPromotion(operand);
			case NOT:
				if (operand.getType().unbox().equals(typeFactory.booleanPrimitiveType())) {
					return Optional.of(typeFactory.booleanPrimitiveType());
				}

				return Optional.empty();
			case PREINC:
			case PREDEC:
			case POSTINC:
			case POSTDEC:
				// See: https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.15.2
				// (documentation is very similar for all four operators)

				// The type of the operand must be a variable that is convertible to a numeric type.
				if (!(operand instanceof CtVariableRead<?>) || !isNumericType(operandType.unbox())) {
					return Optional.empty();
				}

				// The type of the expression is the type of the variable.
				return Optional.of(operandType);
			default:
				throw new UnsupportedOperationException("Unknown operator: " + operator);
		}
	}
}
