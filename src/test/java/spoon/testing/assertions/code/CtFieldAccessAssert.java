package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtFieldAccessAssert extends AbstractAssert<CtFieldAccessAssert, CtFieldAccess> {
    public CtFieldAccessAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtFieldAccessAssert(CtFieldAccess actual) {
        super(actual, CtFieldAccessAssert.class);
    }
}