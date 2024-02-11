package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableAccess;
public class CtVariableAccessAssert extends AbstractObjectAssert<CtVariableAccessAssert, CtVariableAccess<?>> implements CtVariableAccessAssertInterface<CtVariableAccessAssert, CtVariableAccess<?>> {
	CtVariableAccessAssert(CtVariableAccess<?> actual) {
		super(actual, CtVariableAccessAssert.class);
	}

	@Override
	public CtVariableAccessAssert self() {
		return this;
	}

	@Override
	public CtVariableAccess<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
