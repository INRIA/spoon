package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtTargetedExpressionAssert extends AbstractAssert<CtTargetedExpressionAssert, CtTargetedExpression> {
    public CtTargetedExpressionAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtTargetedExpressionAssert(CtTargetedExpression actual) {
        super(actual, CtTargetedExpressionAssert.class);
    }
}