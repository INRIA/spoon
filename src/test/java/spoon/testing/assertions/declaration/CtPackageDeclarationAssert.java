package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackageDeclaration;
public class CtPackageDeclarationAssert extends AbstractAssert<CtPackageDeclarationAssert, CtPackageDeclaration> {
    public CtPackageDeclarationAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtPackageDeclarationAssert(CtPackageDeclaration actual) {
        super(actual, CtPackageDeclarationAssert.class);
    }
}