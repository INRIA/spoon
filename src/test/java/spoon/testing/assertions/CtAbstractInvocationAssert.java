package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAbstractInvocation;
public class CtAbstractInvocationAssert extends AbstractObjectAssert<CtAbstractInvocationAssert, CtAbstractInvocation<?>> implements CtAbstractInvocationAssertInterface<CtAbstractInvocationAssert, CtAbstractInvocation<?>> {
	CtAbstractInvocationAssert(CtAbstractInvocation<?> actual) {
		super(actual, CtAbstractInvocationAssert.class);
	}

	@Override
	public CtAbstractInvocationAssert self() {
		return this;
	}

	@Override
	public CtAbstractInvocation<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
