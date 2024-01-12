package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnumValue;
public class CtEnumValueAssert extends AbstractAssert<CtEnumValueAssert, CtEnumValue> {
    public CtEnumValueAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtEnumValueAssert(CtEnumValue actual) {
        super(actual, CtEnumValueAssert.class);
    }
}