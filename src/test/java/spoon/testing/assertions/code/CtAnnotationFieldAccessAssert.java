package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtAnnotationFieldAccessAssert extends AbstractAssert<CtAnnotationFieldAccessAssert, CtAnnotationFieldAccess> {
    public CtAnnotationFieldAccessAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtAnnotationFieldAccessAssert(CtAnnotationFieldAccess actual) {
        super(actual, CtAnnotationFieldAccessAssert.class);
    }
}