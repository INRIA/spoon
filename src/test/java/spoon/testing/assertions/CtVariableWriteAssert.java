package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableWrite;
public class CtVariableWriteAssert extends AbstractObjectAssert<CtVariableWriteAssert, CtVariableWrite<?>> implements CtVariableWriteAssertInterface<CtVariableWriteAssert, CtVariableWrite<?>> {
	CtVariableWriteAssert(CtVariableWrite<?> actual) {
		super(actual, CtVariableWriteAssert.class);
	}

	@Override
	public CtVariableWriteAssert self() {
		return this;
	}

	@Override
	public CtVariableWrite<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
