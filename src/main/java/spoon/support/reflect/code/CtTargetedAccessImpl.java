package spoon.support.reflect.code;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtTargetedAccess;

@Deprecated
public class CtTargetedAccessImpl<T> extends CtVariableAccessImpl<T>
		implements CtTargetedAccess<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<?> target;

	public CtExpression<?> getTarget() {
		return target;
	}

	public void setTarget(CtExpression<?> target) {
		target.setParent(this);
		this.target = target;
	}
}
