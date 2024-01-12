package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtNewArrayAssert extends AbstractAssert<CtNewArrayAssert, CtNewArray> {
    public CtNewArrayAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtNewArrayAssert(CtNewArray actual) {
        super(actual, CtNewArrayAssert.class);
    }
}