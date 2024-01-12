package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtLambdaAssert extends AbstractAssert<CtLambdaAssert, CtLambda> {
    public CtLambdaAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtLambdaAssert(CtLambda actual) {
        super(actual, CtLambdaAssert.class);
    }
}