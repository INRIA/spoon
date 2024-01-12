package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtVariableWriteAssert extends AbstractAssert<CtVariableWriteAssert, CtVariableWrite> {
    public CtVariableWriteAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtVariableWriteAssert(CtVariableWrite actual) {
        super(actual, CtVariableWriteAssert.class);
    }
}