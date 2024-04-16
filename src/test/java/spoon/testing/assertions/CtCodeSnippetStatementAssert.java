package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCodeSnippetStatement;
public class CtCodeSnippetStatementAssert extends AbstractObjectAssert<CtCodeSnippetStatementAssert, CtCodeSnippetStatement> implements CtCodeSnippetStatementAssertInterface<CtCodeSnippetStatementAssert, CtCodeSnippetStatement> {
	CtCodeSnippetStatementAssert(CtCodeSnippetStatement actual) {
		super(actual, CtCodeSnippetStatementAssert.class);
	}

	@Override
	public CtCodeSnippetStatementAssert self() {
		return this;
	}

	@Override
	public CtCodeSnippetStatement actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
