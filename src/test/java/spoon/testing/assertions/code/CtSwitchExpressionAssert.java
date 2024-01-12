package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtSwitchExpressionAssert extends AbstractAssert<CtSwitchExpressionAssert, CtSwitchExpression> {
    public CtSwitchExpressionAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtSwitchExpressionAssert(CtSwitchExpression actual) {
        super(actual, CtSwitchExpressionAssert.class);
    }
}