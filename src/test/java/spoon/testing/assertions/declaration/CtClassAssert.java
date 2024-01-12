package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
public class CtClassAssert extends AbstractAssert<CtClassAssert, CtClass> {
    public CtClassAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtClassAssert(CtClass actual) {
        super(actual, CtClassAssert.class);
    }
}