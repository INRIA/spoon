package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypeParameter;
public class CtTypeParameterAssert extends AbstractObjectAssert<CtTypeParameterAssert, CtTypeParameter> implements CtTypeParameterAssertInterface<CtTypeParameterAssert, CtTypeParameter> {
	CtTypeParameterAssert(CtTypeParameter actual) {
		super(actual, CtTypeParameterAssert.class);
	}

	@Override
	public CtTypeParameterAssert self() {
		return this;
	}

	@Override
	public CtTypeParameter actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
