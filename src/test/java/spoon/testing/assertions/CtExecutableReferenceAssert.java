package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtExecutableReference;
public class CtExecutableReferenceAssert extends AbstractObjectAssert<CtExecutableReferenceAssert, CtExecutableReference<?>> implements CtExecutableReferenceAssertInterface<CtExecutableReferenceAssert, CtExecutableReference<?>> {
	CtExecutableReferenceAssert(CtExecutableReference<?> actual) {
		super(actual, CtExecutableReferenceAssert.class);
	}

	@Override
	public CtExecutableReferenceAssert self() {
		return this;
	}

	@Override
	public CtExecutableReference<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
