package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtInvocation;
public class CtInvocationAssert extends AbstractObjectAssert<CtInvocationAssert, CtInvocation<?>> implements CtInvocationAssertInterface<CtInvocationAssert, CtInvocation<?>> {
	CtInvocationAssert(CtInvocation<?> actual) {
		super(actual, CtInvocationAssert.class);
	}

	@Override
	public CtInvocationAssert self() {
		return this;
	}

	@Override
	public CtInvocation<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
