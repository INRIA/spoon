package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtLocalVariableReferenceAssert extends AbstractAssert<CtLocalVariableReferenceAssert, CtLocalVariableReference> {
    public CtLocalVariableReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtLocalVariableReferenceAssert(CtLocalVariableReference actual) {
        super(actual, CtLocalVariableReferenceAssert.class);
    }
}