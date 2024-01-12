package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
public class CtTypeAssert extends AbstractAssert<CtTypeAssert, CtType> {
    public CtTypeAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtTypeAssert(CtType actual) {
        super(actual, CtTypeAssert.class);
    }
}