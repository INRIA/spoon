package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtJavaDocAssert extends AbstractAssert<CtJavaDocAssert, CtJavaDoc> {
    public CtJavaDocAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtJavaDocAssert(CtJavaDoc actual) {
        super(actual, CtJavaDocAssert.class);
    }
}