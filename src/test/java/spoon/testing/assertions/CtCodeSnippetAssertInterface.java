package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.declaration.CtCodeSnippet;
interface CtCodeSnippetAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCodeSnippet> extends SpoonAssert<A, W> {
    default AbstractStringAssert<?> getValue() {
        return org.assertj.core.api.Assertions.assertThat(actual().getValue());
    }
}