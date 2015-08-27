package spoon.support.reflect.code;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtVariableAccessImpl<T> extends CtExpressionImpl<T> implements CtVariableAccess<T> {
	private static final long serialVersionUID = 1L;

	CtVariableReference<T> variable;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtVariableAccess(this);
	}

	@Override
	public CtVariableReference<T> getVariable() {
		return variable;
	}

	@Override
	public <C extends CtVariableAccess<T>> C setVariable(CtVariableReference<T> variable) {
		this.variable = variable;
		return (C) this;
	}
}
