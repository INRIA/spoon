package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
public class CtTypeMemberWildcardImportReferenceAssert extends AbstractAssert<CtTypeMemberWildcardImportReferenceAssert, CtTypeMemberWildcardImportReference> {
	public CtTypeMemberWildcardImportReferenceAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtTypeMemberWildcardImportReferenceAssert(CtTypeMemberWildcardImportReference actual) {
		super(actual, CtTypeMemberWildcardImportReferenceAssert.class);
	}
}
