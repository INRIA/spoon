package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
public class CtModifiableAssert extends AbstractAssert<CtModifiableAssert, CtModifiable> {
    public CtModifiableAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtModifiableAssert(CtModifiable actual) {
        super(actual, CtModifiableAssert.class);
    }
}