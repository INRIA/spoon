package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtAbstractSwitchAssert extends AbstractAssert<CtAbstractSwitchAssert, CtAbstractSwitch> {
    public CtAbstractSwitchAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtAbstractSwitchAssert(CtAbstractSwitch actual) {
        super(actual, CtAbstractSwitchAssert.class);
    }
}