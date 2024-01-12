package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
public class CtFieldAssert extends AbstractAssert<CtFieldAssert, CtField> {
    public CtFieldAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtFieldAssert(CtField actual) {
        super(actual, CtFieldAssert.class);
    }
}