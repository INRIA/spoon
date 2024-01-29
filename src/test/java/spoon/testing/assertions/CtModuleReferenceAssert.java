package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtModuleReference;
public class CtModuleReferenceAssert extends AbstractObjectAssert<CtModuleReferenceAssert, CtModuleReference> implements CtModuleReferenceAssertInterface<CtModuleReferenceAssert, CtModuleReference> {
	CtModuleReferenceAssert(CtModuleReference actual) {
		super(actual, CtModuleReferenceAssert.class);
	}

	@Override
	public CtModuleReferenceAssert self() {
		return this;
	}

	@Override
	public CtModuleReference actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
