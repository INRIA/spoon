package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.PATTERN;
import static spoon.reflect.path.CtRole.VARIABLE;

/*
TODO
 - Qualified Enum Constants
 - case null[, default]

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
