package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
public class CtCompilationUnitAssert extends AbstractAssert<CtCompilationUnitAssert, CtCompilationUnit> {
    public CtCompilationUnitAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtCompilationUnitAssert(CtCompilationUnit actual) {
        super(actual, CtCompilationUnitAssert.class);
    }
}