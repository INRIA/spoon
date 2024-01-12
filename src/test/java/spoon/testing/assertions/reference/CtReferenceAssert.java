package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtReferenceAssert extends AbstractAssert<CtReferenceAssert, CtReference> {
    public CtReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtReferenceAssert(CtReference actual) {
        super(actual, CtReferenceAssert.class);
    }
}