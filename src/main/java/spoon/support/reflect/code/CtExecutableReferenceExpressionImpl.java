package spoon.support.reflect.code;

import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtExecutableReferenceExpressionImpl<T, E extends CtExpression<?>>
		extends CtTargetedExpressionImpl<T, E> implements CtExecutableReferenceExpression<T, E> {
	CtExecutableReference<T> executable;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtExecutableReferenceExpression(this);
	}

	@Override
	public CtExecutableReference<T> getExecutable() {
		return executable;
	}

	@Override
	public <C extends CtExecutableReferenceExpression<T, E>> C setExecutable(CtExecutableReference<T> executable) {
		this.executable = executable;
		return (C) this;
	}
}
