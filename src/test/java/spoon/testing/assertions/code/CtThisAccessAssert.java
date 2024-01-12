package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtThisAccessAssert extends AbstractAssert<CtThisAccessAssert, CtThisAccess> {
    public CtThisAccessAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtThisAccessAssert(CtThisAccess actual) {
        super(actual, CtThisAccessAssert.class);
    }
}