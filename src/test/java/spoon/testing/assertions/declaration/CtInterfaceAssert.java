package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
public class CtInterfaceAssert extends AbstractAssert<CtInterfaceAssert, CtInterface> {
    public CtInterfaceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtInterfaceAssert(CtInterface actual) {
        super(actual, CtInterfaceAssert.class);
    }
}