package spoon.reflect.code;
/*
TODO
 - Qualified Enum Constants
 - case null[, default]

 */
public interface CtCasePattern extends CtExpression<Void> {

	CtPattern getPattern();

	CtCasePattern setPattern(CtPattern pattern);

	CtExpression<?> getGuard();

	CtCasePattern setGuard(CtExpression<?> guard);
}
