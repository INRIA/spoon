package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtVariableAccessAssert extends AbstractAssert<CtVariableAccessAssert, CtVariableAccess> {
    public CtVariableAccessAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtVariableAccessAssert(CtVariableAccess actual) {
        super(actual, CtVariableAccessAssert.class);
    }
}