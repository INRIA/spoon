package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtWildcardReference;
public class CtWildcardReferenceAssert extends AbstractObjectAssert<CtWildcardReferenceAssert, CtWildcardReference> implements CtWildcardReferenceAssertInterface<CtWildcardReferenceAssert, CtWildcardReference> {
	CtWildcardReferenceAssert(CtWildcardReference actual) {
		super(actual, CtWildcardReferenceAssert.class);
	}

	@Override
	public CtWildcardReferenceAssert self() {
		return this;
	}

	@Override
	public CtWildcardReference actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
