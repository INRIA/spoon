package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtYieldStatementAssert extends AbstractAssert<CtYieldStatementAssert, CtYieldStatement> {
    public CtYieldStatementAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtYieldStatementAssert(CtYieldStatement actual) {
        super(actual, CtYieldStatementAssert.class);
    }
}