package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
public class CtTypeMemberWildcardImportReferenceAssert extends AbstractObjectAssert<CtTypeMemberWildcardImportReferenceAssert, CtTypeMemberWildcardImportReference> implements CtTypeMemberWildcardImportReferenceAssertInterface<CtTypeMemberWildcardImportReferenceAssert, CtTypeMemberWildcardImportReference> {
	CtTypeMemberWildcardImportReferenceAssert(CtTypeMemberWildcardImportReference actual) {
		super(actual, CtTypeMemberWildcardImportReferenceAssert.class);
	}

	@Override
	public CtTypeMemberWildcardImportReferenceAssert self() {
		return this;
	}

	@Override
	public CtTypeMemberWildcardImportReference actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
