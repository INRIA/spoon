package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtExecutableReferenceExpression;
public class CtExecutableReferenceExpressionAssert extends AbstractObjectAssert<CtExecutableReferenceExpressionAssert, CtExecutableReferenceExpression<?, ?>> implements CtExecutableReferenceExpressionAssertInterface<CtExecutableReferenceExpressionAssert, CtExecutableReferenceExpression<?, ?>> {
	CtExecutableReferenceExpressionAssert(CtExecutableReferenceExpression<?, ?> actual) {
		super(actual, CtExecutableReferenceExpressionAssert.class);
	}

	@Override
	public CtExecutableReferenceExpressionAssert self() {
		return this;
	}

	@Override
	public CtExecutableReferenceExpression<?, ?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
