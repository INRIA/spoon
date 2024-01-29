package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtTypeReference;
public class CtTypeReferenceAssert extends AbstractObjectAssert<CtTypeReferenceAssert, CtTypeReference<?>> implements CtTypeReferenceAssertInterface<CtTypeReferenceAssert, CtTypeReference<?>> {
	CtTypeReferenceAssert(CtTypeReference<?> actual) {
		super(actual, CtTypeReferenceAssert.class);
	}

	@Override
	public CtTypeReferenceAssert self() {
		return this;
	}

	@Override
	public CtTypeReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
