package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtExecutableReferenceExpressionAssert extends AbstractAssert<CtExecutableReferenceExpressionAssert, CtExecutableReferenceExpression> {
    public CtExecutableReferenceExpressionAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtExecutableReferenceExpressionAssert(CtExecutableReferenceExpression actual) {
        super(actual, CtExecutableReferenceExpressionAssert.class);
    }
}