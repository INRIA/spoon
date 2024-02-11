package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtArrayTypeReference;
public class CtArrayTypeReferenceAssert extends AbstractObjectAssert<CtArrayTypeReferenceAssert, CtArrayTypeReference<?>> implements CtArrayTypeReferenceAssertInterface<CtArrayTypeReferenceAssert, CtArrayTypeReference<?>> {
	CtArrayTypeReferenceAssert(CtArrayTypeReference<?> actual) {
		super(actual, CtArrayTypeReferenceAssert.class);
	}

	@Override
	public CtArrayTypeReferenceAssert self() {
		return this;
	}

	@Override
	public CtArrayTypeReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
