package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtPackageReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtPackageReferenceAssert extends AbstractAssert<CtPackageReferenceAssert, CtPackageReference> {
    public CtPackageReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtPackageReferenceAssert(CtPackageReference actual) {
        super(actual, CtPackageReferenceAssert.class);
    }
}