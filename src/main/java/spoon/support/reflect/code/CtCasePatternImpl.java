package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtPattern;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import java.io.Serial;

// TODO model listeners
public class CtCasePatternImpl extends CtExpressionImpl<Void> implements CtCasePattern {
	@Serial
	private static final long serialVersionUID = 1L;
	@MetamodelPropertyField(role = CtRole.PATTERN)
	private CtPattern pattern;
	@MetamodelPropertyField(role = CtRole.CONDITION)
	private CtExpression<?> guard;

	@Override
	public CtPattern getPattern() {
		return pattern;
	}

	@Override
	public CtCasePattern setPattern(CtPattern pattern) {
		this.pattern = pattern;
		return this;
	}

	@Override
	public CtExpression<?> getGuard() {
		return guard;
	}

	@Override
	public CtCasePattern setGuard(CtExpression<?> guard) {
		this.guard = guard;
		return this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCasePattern(this);
	}

	@Override
	public CtCasePattern clone() {
		return (CtCasePattern) super.clone();
	}
}
