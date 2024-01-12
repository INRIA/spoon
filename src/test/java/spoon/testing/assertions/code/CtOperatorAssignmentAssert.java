package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtOperatorAssignmentAssert extends AbstractAssert<CtOperatorAssignmentAssert, CtOperatorAssignment> {
    public CtOperatorAssignmentAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtOperatorAssignmentAssert(CtOperatorAssignment actual) {
        super(actual, CtOperatorAssignmentAssert.class);
    }
}