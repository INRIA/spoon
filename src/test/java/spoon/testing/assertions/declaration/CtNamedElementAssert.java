package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
public class CtNamedElementAssert extends AbstractAssert<CtNamedElementAssert, CtNamedElement> {
    public CtNamedElementAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtNamedElementAssert(CtNamedElement actual) {
        super(actual, CtNamedElementAssert.class);
    }
}