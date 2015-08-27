package spoon.support.reflect.code;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtTargetedAccess;
import spoon.reflect.code.CtTargetedExpression;

@Deprecated
public abstract class CtTargetedAccessImpl<T> extends CtVariableAccessImpl<T>
		implements CtTargetedAccess<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<?> target;

	@Override
	public CtExpression<?> getTarget() {
		return target;
	}

	@Override
	public <C extends CtTargetedExpression<T, CtExpression<?>>> C setTarget(CtExpression<?> target) {
		if (target != null)
			target.setParent(this);
		this.target = target;
		return null;
	}
}
