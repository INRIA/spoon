package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtParameter;
public class CtParameterAssert extends AbstractObjectAssert<CtParameterAssert, CtParameter<?>> implements CtParameterAssertInterface<CtParameterAssert, CtParameter<?>> {
	CtParameterAssert(CtParameter<?> actual) {
		super(actual, CtParameterAssert.class);
	}

	@Override
	public CtParameterAssert self() {
		return this;
	}

	@Override
	public CtParameter<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
