package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCodeSnippetExpression;
public interface CtCodeSnippetExpressionAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCodeSnippetExpression<?>> extends CtCodeSnippetAssertInterface<A, W> , SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {}
