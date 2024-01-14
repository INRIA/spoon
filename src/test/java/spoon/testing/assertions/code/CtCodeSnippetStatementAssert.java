package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCodeSnippetStatement;
public class CtCodeSnippetStatementAssert extends AbstractAssert<CtCodeSnippetStatementAssert, CtCodeSnippetStatement> {
	public CtCodeSnippetStatementAssert(CtCodeSnippetStatement actual) {
		super(actual, CtCodeSnippetStatementAssert.class);
	}
}
