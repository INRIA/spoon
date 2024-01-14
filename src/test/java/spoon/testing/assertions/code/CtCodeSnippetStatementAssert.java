package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtElement;
public class CtCodeSnippetStatementAssert extends AbstractAssert<CtCodeSnippetStatementAssert, CtCodeSnippetStatement> {
	public CtCodeSnippetStatementAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtCodeSnippetStatementAssert(CtCodeSnippetStatement actual) {
		super(actual, CtCodeSnippetStatementAssert.class);
	}
}
