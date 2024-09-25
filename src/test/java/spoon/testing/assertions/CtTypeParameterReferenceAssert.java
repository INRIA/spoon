package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtTypeParameterReference;
public class CtTypeParameterReferenceAssert extends AbstractObjectAssert<CtTypeParameterReferenceAssert, CtTypeParameterReference> implements CtTypeParameterReferenceAssertInterface<CtTypeParameterReferenceAssert, CtTypeParameterReference> {
	CtTypeParameterReferenceAssert(CtTypeParameterReference actual) {
		super(actual, CtTypeParameterReferenceAssert.class);
	}

	@Override
	public CtTypeParameterReferenceAssert self() {
		return this;
	}

	@Override
	public CtTypeParameterReference actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
