package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtElement;
public class CtAnnotationMethodAssert extends AbstractAssert<CtAnnotationMethodAssert, CtAnnotationMethod> {
    public CtAnnotationMethodAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtAnnotationMethodAssert(CtAnnotationMethod actual) {
        super(actual, CtAnnotationMethodAssert.class);
    }
}