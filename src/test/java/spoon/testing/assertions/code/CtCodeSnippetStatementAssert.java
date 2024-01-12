package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtCodeSnippetStatementAssert extends AbstractAssert<CtCodeSnippetStatementAssert, CtCodeSnippetStatement> {
    public CtCodeSnippetStatementAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtCodeSnippetStatementAssert(CtCodeSnippetStatement actual) {
        super(actual, CtCodeSnippetStatementAssert.class);
    }
}