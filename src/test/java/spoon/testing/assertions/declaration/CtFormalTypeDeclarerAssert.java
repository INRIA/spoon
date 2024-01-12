package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
public class CtFormalTypeDeclarerAssert extends AbstractAssert<CtFormalTypeDeclarerAssert, CtFormalTypeDeclarer> {
    public CtFormalTypeDeclarerAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtFormalTypeDeclarerAssert(CtFormalTypeDeclarer actual) {
        super(actual, CtFormalTypeDeclarerAssert.class);
    }
}