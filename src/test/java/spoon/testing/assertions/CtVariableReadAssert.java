package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableRead;
public class CtVariableReadAssert extends AbstractObjectAssert<CtVariableReadAssert, CtVariableRead<?>> implements CtVariableReadAssertInterface<CtVariableReadAssert, CtVariableRead<?>> {
	CtVariableReadAssert(CtVariableRead<?> actual) {
		super(actual, CtVariableReadAssert.class);
	}

	@Override
	public CtVariableReadAssert self() {
		return this;
	}

	@Override
	public CtVariableRead<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
