package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtExecutableReferenceAssert extends AbstractAssert<CtExecutableReferenceAssert, CtExecutableReference> {
    public CtExecutableReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtExecutableReferenceAssert(CtExecutableReference actual) {
        super(actual, CtExecutableReferenceAssert.class);
    }
}