package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtCodeSnippet;
public class CtCodeSnippetAssert extends AbstractObjectAssert<CtCodeSnippetAssert, CtCodeSnippet> implements CtCodeSnippetAssertInterface<CtCodeSnippetAssert, CtCodeSnippet> {
	CtCodeSnippetAssert(CtCodeSnippet actual) {
		super(actual, CtCodeSnippetAssert.class);
	}

	@Override
	public CtCodeSnippetAssert self() {
		return this;
	}

	@Override
	public CtCodeSnippet actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
