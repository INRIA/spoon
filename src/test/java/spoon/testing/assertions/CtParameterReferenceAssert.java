package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtParameterReference;
public class CtParameterReferenceAssert extends AbstractObjectAssert<CtParameterReferenceAssert, CtParameterReference<?>> implements CtParameterReferenceAssertInterface<CtParameterReferenceAssert, CtParameterReference<?>> {
	CtParameterReferenceAssert(CtParameterReference<?> actual) {
		super(actual, CtParameterReferenceAssert.class);
	}

	@Override
	public CtParameterReferenceAssert self() {
		return this;
	}

	@Override
	public CtParameterReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
