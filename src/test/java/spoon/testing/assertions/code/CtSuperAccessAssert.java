package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtSuperAccessAssert extends AbstractAssert<CtSuperAccessAssert, CtSuperAccess> {
    public CtSuperAccessAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtSuperAccessAssert(CtSuperAccess actual) {
        super(actual, CtSuperAccessAssert.class);
    }
}