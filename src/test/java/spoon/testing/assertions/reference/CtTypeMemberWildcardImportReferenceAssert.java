package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtTypeMemberWildcardImportReferenceAssert extends AbstractAssert<CtTypeMemberWildcardImportReferenceAssert, CtTypeMemberWildcardImportReference> {
    public CtTypeMemberWildcardImportReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtTypeMemberWildcardImportReferenceAssert(CtTypeMemberWildcardImportReference actual) {
        super(actual, CtTypeMemberWildcardImportReferenceAssert.class);
    }
}