package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtLocalVariableReference;
public class CtLocalVariableReferenceAssert extends AbstractObjectAssert<CtLocalVariableReferenceAssert, CtLocalVariableReference<?>> implements CtLocalVariableReferenceAssertInterface<CtLocalVariableReferenceAssert, CtLocalVariableReference<?>> {
	CtLocalVariableReferenceAssert(CtLocalVariableReference<?> actual) {
		super(actual, CtLocalVariableReferenceAssert.class);
	}

	@Override
	public CtLocalVariableReferenceAssert self() {
		return this;
	}

	@Override
	public CtLocalVariableReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
