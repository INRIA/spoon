package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtSynchronizedAssert extends AbstractAssert<CtSynchronizedAssert, CtSynchronized> {
    public CtSynchronizedAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtSynchronizedAssert(CtSynchronized actual) {
        super(actual, CtSynchronizedAssert.class);
    }
}