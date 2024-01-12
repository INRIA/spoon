package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtAssignmentAssert extends AbstractAssert<CtAssignmentAssert, CtAssignment> {
    public CtAssignmentAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtAssignmentAssert(CtAssignment actual) {
        super(actual, CtAssignmentAssert.class);
    }
}