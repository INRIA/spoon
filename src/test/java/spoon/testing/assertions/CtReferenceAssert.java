package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtReference;
public class CtReferenceAssert extends AbstractObjectAssert<CtReferenceAssert, CtReference> implements CtReferenceAssertInterface<CtReferenceAssert, CtReference> {
	CtReferenceAssert(CtReference actual) {
		super(actual, CtReferenceAssert.class);
	}

	@Override
	public CtReferenceAssert self() {
		return this;
	}

	@Override
	public CtReference actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
