package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTargetedExpression;
public class CtTargetedExpressionAssert extends AbstractObjectAssert<CtTargetedExpressionAssert, CtTargetedExpression<?, ?>> implements CtTargetedExpressionAssertInterface<CtTargetedExpressionAssert, CtTargetedExpression<?, ?>> {
	CtTargetedExpressionAssert(CtTargetedExpression<?, ?> actual) {
		super(actual, CtTargetedExpressionAssert.class);
	}

	@Override
	public CtTargetedExpressionAssert self() {
		return this;
	}

	@Override
	public CtTargetedExpression<?, ?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
