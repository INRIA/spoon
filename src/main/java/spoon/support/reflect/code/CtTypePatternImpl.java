package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

public class CtTypePatternImpl<T> extends CtExpressionImpl<Void> implements CtTypePattern<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.VARIABLE)
    private CtLocalVariable<?> variable;

	@Override
	public CtLocalVariable<?> getVariable() {
		return this.variable;
	}

	@Override
	public <C extends CtTypePattern<?>> C setVariable(CtLocalVariable<?> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener()
				// TODO is this the proper role? New role needed?
				.onObjectUpdate(this, CtRole.VARIABLE, variable, this.variable);
		this.variable = variable;
		return (C) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypePattern(this);
	}

	@Override
	public CtTypePattern<T> clone() {
		return CtTypePattern.class.cast(super.clone());
	}
}
