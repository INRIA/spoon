package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCodeSnippetExpression;
public class CtCodeSnippetExpressionAssert extends AbstractObjectAssert<CtCodeSnippetExpressionAssert, CtCodeSnippetExpression<?>> implements CtCodeSnippetExpressionAssertInterface<CtCodeSnippetExpressionAssert, CtCodeSnippetExpression<?>> {
	CtCodeSnippetExpressionAssert(CtCodeSnippetExpression<?> actual) {
		super(actual, CtCodeSnippetExpressionAssert.class);
	}

	@Override
	public CtCodeSnippetExpressionAssert self() {
		return this;
	}

	@Override
	public CtCodeSnippetExpression<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
