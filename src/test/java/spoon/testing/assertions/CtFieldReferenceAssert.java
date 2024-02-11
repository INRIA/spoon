package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtFieldReference;
public class CtFieldReferenceAssert extends AbstractObjectAssert<CtFieldReferenceAssert, CtFieldReference<?>> implements CtFieldReferenceAssertInterface<CtFieldReferenceAssert, CtFieldReference<?>> {
	CtFieldReferenceAssert(CtFieldReference<?> actual) {
		super(actual, CtFieldReferenceAssert.class);
	}

	@Override
	public CtFieldReferenceAssert self() {
		return this;
	}

	@Override
	public CtFieldReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
