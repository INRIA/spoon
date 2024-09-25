package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtUnboundVariableReference;
public class CtUnboundVariableReferenceAssert extends AbstractObjectAssert<CtUnboundVariableReferenceAssert, CtUnboundVariableReference<?>> implements CtUnboundVariableReferenceAssertInterface<CtUnboundVariableReferenceAssert, CtUnboundVariableReference<?>> {
	CtUnboundVariableReferenceAssert(CtUnboundVariableReference<?> actual) {
		super(actual, CtUnboundVariableReferenceAssert.class);
	}

	@Override
	public CtUnboundVariableReferenceAssert self() {
		return this;
	}

	@Override
	public CtUnboundVariableReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
