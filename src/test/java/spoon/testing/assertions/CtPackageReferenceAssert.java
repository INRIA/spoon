package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtPackageReference;
public class CtPackageReferenceAssert extends AbstractObjectAssert<CtPackageReferenceAssert, CtPackageReference> implements CtPackageReferenceAssertInterface<CtPackageReferenceAssert, CtPackageReference> {
	CtPackageReferenceAssert(CtPackageReference actual) {
		super(actual, CtPackageReferenceAssert.class);
	}

	@Override
	public CtPackageReferenceAssert self() {
		return this;
	}

	@Override
	public CtPackageReference actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
