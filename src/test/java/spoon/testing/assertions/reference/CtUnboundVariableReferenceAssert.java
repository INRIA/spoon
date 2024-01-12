package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtUnboundVariableReferenceAssert extends AbstractAssert<CtUnboundVariableReferenceAssert, CtUnboundVariableReference> {
    public CtUnboundVariableReferenceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtUnboundVariableReferenceAssert(CtUnboundVariableReference actual) {
        super(actual, CtUnboundVariableReferenceAssert.class);
    }
}