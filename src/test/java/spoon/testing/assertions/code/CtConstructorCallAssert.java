package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtConstructorCallAssert extends AbstractAssert<CtConstructorCallAssert, CtConstructorCall> {
    public CtConstructorCallAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtConstructorCallAssert(CtConstructorCall actual) {
        super(actual, CtConstructorCallAssert.class);
    }
}