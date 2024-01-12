package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtProvidedService;
public class CtProvidedServiceAssert extends AbstractAssert<CtProvidedServiceAssert, CtProvidedService> {
    public CtProvidedServiceAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtProvidedServiceAssert(CtProvidedService actual) {
        super(actual, CtProvidedServiceAssert.class);
    }
}