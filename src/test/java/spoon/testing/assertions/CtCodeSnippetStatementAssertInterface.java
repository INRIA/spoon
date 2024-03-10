package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCodeSnippetStatement;
public interface CtCodeSnippetStatementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCodeSnippetStatement> extends CtCodeSnippetAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {}
