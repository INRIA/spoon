package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtArrayReadAssert extends AbstractAssert<CtArrayReadAssert, CtArrayRead> {
    public CtArrayReadAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtArrayReadAssert(CtArrayRead actual) {
        super(actual, CtArrayReadAssert.class);
    }
}