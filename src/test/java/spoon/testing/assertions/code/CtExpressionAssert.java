package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtExpressionAssert extends AbstractAssert<CtExpressionAssert, CtExpression> {
    public CtExpressionAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtExpressionAssert(CtExpression actual) {
        super(actual, CtExpressionAssert.class);
    }
}