package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSwitchExpression;
public class CtSwitchExpressionAssert extends AbstractObjectAssert<CtSwitchExpressionAssert, CtSwitchExpression<?, ?>> implements CtSwitchExpressionAssertInterface<CtSwitchExpressionAssert, CtSwitchExpression<?, ?>> {
	CtSwitchExpressionAssert(CtSwitchExpression<?, ?> actual) {
		super(actual, CtSwitchExpressionAssert.class);
	}

	@Override
	public CtSwitchExpressionAssert self() {
		return this;
	}

	@Override
	public CtSwitchExpression<?, ?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
