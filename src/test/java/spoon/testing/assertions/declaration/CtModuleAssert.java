package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
public class CtModuleAssert extends AbstractAssert<CtModuleAssert, CtModule> {
    public CtModuleAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtModuleAssert(CtModule actual) {
        super(actual, CtModuleAssert.class);
    }
}