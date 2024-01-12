package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtNewClassAssert extends AbstractAssert<CtNewClassAssert, CtNewClass> {
    public CtNewClassAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtNewClassAssert(CtNewClass actual) {
        super(actual, CtNewClassAssert.class);
    }
}