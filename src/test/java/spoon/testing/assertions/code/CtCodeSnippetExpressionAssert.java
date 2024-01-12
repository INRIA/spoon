package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtCodeSnippetExpressionAssert extends AbstractAssert<CtCodeSnippetExpressionAssert, CtCodeSnippetExpression> {
    public CtCodeSnippetExpressionAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtCodeSnippetExpressionAssert(CtCodeSnippetExpression actual) {
        super(actual, CtCodeSnippetExpressionAssert.class);
    }
}