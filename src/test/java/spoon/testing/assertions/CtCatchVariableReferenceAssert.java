package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtCatchVariableReference;
public class CtCatchVariableReferenceAssert extends AbstractObjectAssert<CtCatchVariableReferenceAssert, CtCatchVariableReference<?>> implements CtCatchVariableReferenceAssertInterface<CtCatchVariableReferenceAssert, CtCatchVariableReference<?>> {
	CtCatchVariableReferenceAssert(CtCatchVariableReference<?> actual) {
		super(actual, CtCatchVariableReferenceAssert.class);
	}

	@Override
	public CtCatchVariableReferenceAssert self() {
		return this;
	}

	@Override
	public CtCatchVariableReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
