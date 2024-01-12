package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtUsedService;
public class CtUsedServiceAssert extends AbstractAssert<CtUsedServiceAssert, CtUsedService> {
    public CtUsedServiceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtUsedServiceAssert(CtUsedService actual) {
        super(actual, CtUsedServiceAssert.class);
    }
}