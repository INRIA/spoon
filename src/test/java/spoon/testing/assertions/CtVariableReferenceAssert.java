package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtVariableReference;
public class CtVariableReferenceAssert extends AbstractObjectAssert<CtVariableReferenceAssert, CtVariableReference<?>> implements CtVariableReferenceAssertInterface<CtVariableReferenceAssert, CtVariableReference<?>> {
	CtVariableReferenceAssert(CtVariableReference<?> actual) {
		super(actual, CtVariableReferenceAssert.class);
	}

	@Override
	public CtVariableReferenceAssert self() {
		return this;
	}

	@Override
	public CtVariableReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
