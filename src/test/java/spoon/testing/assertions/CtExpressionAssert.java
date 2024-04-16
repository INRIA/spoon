package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtExpression;
public class CtExpressionAssert extends AbstractObjectAssert<CtExpressionAssert, CtExpression<?>> implements CtExpressionAssertInterface<CtExpressionAssert, CtExpression<?>> {
	CtExpressionAssert(CtExpression<?> actual) {
		super(actual, CtExpressionAssert.class);
	}

	@Override
	public CtExpressionAssert self() {
		return this;
	}

	@Override
	public CtExpression<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
