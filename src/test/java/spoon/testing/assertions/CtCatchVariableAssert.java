package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCatchVariable;
public class CtCatchVariableAssert extends AbstractObjectAssert<CtCatchVariableAssert, CtCatchVariable<?>> implements CtCatchVariableAssertInterface<CtCatchVariableAssert, CtCatchVariable<?>> {
	CtCatchVariableAssert(CtCatchVariable<?> actual) {
		super(actual, CtCatchVariableAssert.class);
	}

	@Override
	public CtCatchVariableAssert self() {
		return this;
	}

	@Override
	public CtCatchVariable<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
