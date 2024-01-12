package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtAbstractInvocationAssert extends AbstractAssert<CtAbstractInvocationAssert, CtAbstractInvocation> {
    public CtAbstractInvocationAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtAbstractInvocationAssert(CtAbstractInvocation actual) {
        super(actual, CtAbstractInvocationAssert.class);
    }
}