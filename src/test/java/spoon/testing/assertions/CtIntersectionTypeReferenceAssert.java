package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtIntersectionTypeReference;
public class CtIntersectionTypeReferenceAssert extends AbstractObjectAssert<CtIntersectionTypeReferenceAssert, CtIntersectionTypeReference<?>> implements CtIntersectionTypeReferenceAssertInterface<CtIntersectionTypeReferenceAssert, CtIntersectionTypeReference<?>> {
	CtIntersectionTypeReferenceAssert(CtIntersectionTypeReference<?> actual) {
		super(actual, CtIntersectionTypeReferenceAssert.class);
	}

	@Override
	public CtIntersectionTypeReferenceAssert self() {
		return this;
	}

	@Override
	public CtIntersectionTypeReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
