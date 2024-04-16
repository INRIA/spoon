package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtReceiverParameter;
public class CtReceiverParameterAssert extends AbstractObjectAssert<CtReceiverParameterAssert, CtReceiverParameter> implements CtReceiverParameterAssertInterface<CtReceiverParameterAssert, CtReceiverParameter> {
	CtReceiverParameterAssert(CtReceiverParameter actual) {
		super(actual, CtReceiverParameterAssert.class);
	}

	@Override
	public CtReceiverParameterAssert self() {
		return this;
	}

	@Override
	public CtReceiverParameter actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
