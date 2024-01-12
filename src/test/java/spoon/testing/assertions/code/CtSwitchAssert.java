package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtSwitchAssert extends AbstractAssert<CtSwitchAssert, CtSwitch> {
    public CtSwitchAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtSwitchAssert(CtSwitch actual) {
        super(actual, CtSwitchAssert.class);
    }
}