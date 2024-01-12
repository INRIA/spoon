package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtCatchVariableAssert extends AbstractAssert<CtCatchVariableAssert, CtCatchVariable> {
    public CtCatchVariableAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtCatchVariableAssert(CtCatchVariable actual) {
        super(actual, CtCatchVariableAssert.class);
    }
}