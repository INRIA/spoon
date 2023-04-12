package spoon.support.reflect.code;

import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtPattern;
import spoon.reflect.visitor.CtVisitor;

public class CtCasePatternImpl extends CtExpressionImpl<Void> implements CtCasePattern {
	private CtPattern pattern;
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
}
