package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtBinaryOperatorAssert extends AbstractAssert<CtBinaryOperatorAssert, CtBinaryOperator> {
    public CtBinaryOperatorAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtBinaryOperatorAssert(CtBinaryOperator actual) {
        super(actual, CtBinaryOperatorAssert.class);
    }
}