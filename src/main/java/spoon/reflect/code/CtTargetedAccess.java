package spoon.reflect.code;

/**
 * @deprecated Methods defined in CtTargetedAccess have been moved in
 * {@link spoon.reflect.code.CtFieldAccess} and it will be replaced by
 * {@link spoon.reflect.code.CtTargetedExpression} in a future version.
 * Think to update our usage of this class for the next release of Spoon.
 */
@Deprecated
public interface CtTargetedAccess<T>
		extends CtVariableRead<T>, CtTargetedExpression<T, CtExpression<?>> {
}
