package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtTypeReferenceAssert extends AbstractAssert<CtTypeReferenceAssert, CtTypeReference> {
    public CtTypeReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtTypeReferenceAssert(CtTypeReference actual) {
        super(actual, CtTypeReferenceAssert.class);
    }
}