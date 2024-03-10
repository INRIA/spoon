package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtVariable;
public class CtVariableAssert extends AbstractObjectAssert<CtVariableAssert, CtVariable<?>> implements CtVariableAssertInterface<CtVariableAssert, CtVariable<?>> {
	CtVariableAssert(CtVariable<?> actual) {
		super(actual, CtVariableAssert.class);
	}

	@Override
	public CtVariableAssert self() {
		return this;
	}

	@Override
	public CtVariable<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
