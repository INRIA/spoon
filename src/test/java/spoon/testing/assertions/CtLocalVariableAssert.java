package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLocalVariable;
public class CtLocalVariableAssert extends AbstractObjectAssert<CtLocalVariableAssert, CtLocalVariable<?>> implements CtLocalVariableAssertInterface<CtLocalVariableAssert, CtLocalVariable<?>> {
	CtLocalVariableAssert(CtLocalVariable<?> actual) {
		super(actual, CtLocalVariableAssert.class);
	}

	@Override
	public CtLocalVariableAssert self() {
		return this;
	}

	@Override
	public CtLocalVariable<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
