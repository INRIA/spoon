package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.PATTERN;

/*
TODO
 - Qualified Enum Constants
 - case null[, default]

 */

/**
 * This code element represents a case pattern, introduced in Java 21
 * by <a href=https://openjdk.java.net/jeps/441>JEP 441</a>.
 * <p>
 * Example:
 * <pre>
 * Number num = myNum();
 * switch (num) {
 *    case Integer i -> handleInt(i);
 *    case Double d when d > 0.5 -> handleDouble(d);
 *    case null, default -> handleOther();
 * }
 * </pre>
 */
public interface CtCasePattern extends CtExpression<Void> {

	@PropertyGetter(role = PATTERN)
	CtPattern getPattern();

	@PropertySetter(role = PATTERN)
	CtCasePattern setPattern(CtPattern pattern);

	@PropertyGetter(role = CONDITION)
	CtExpression<?> getGuard();

	@PropertySetter(role = CONDITION)
	CtCasePattern setGuard(CtExpression<?> guard);

	@Override
	CtCasePattern clone();
}
