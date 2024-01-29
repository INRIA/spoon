package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLiteral;
public class CtLiteralAssert extends AbstractObjectAssert<CtLiteralAssert, CtLiteral<?>> implements CtLiteralAssertInterface<CtLiteralAssert, CtLiteral<?>> {
	CtLiteralAssert(CtLiteral<?> actual) {
		super(actual, CtLiteralAssert.class);
	}

	@Override
	public CtLiteralAssert self() {
		return this;
	}

	@Override
	public CtLiteral<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
