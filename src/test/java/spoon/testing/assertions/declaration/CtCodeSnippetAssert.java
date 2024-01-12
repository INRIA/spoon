package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtCodeSnippet;
public class CtCodeSnippetAssert extends AbstractAssert<CtCodeSnippetAssert, CtCodeSnippet> {
    CtCodeSnippetAssert(CtCodeSnippet actual) {
        super(actual, CtCodeSnippetAssert.class);
    }
}