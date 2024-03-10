package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtCodeSnippet;
public interface CtCodeSnippetAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCodeSnippet> extends SpoonAssert<A, W> {
	default AbstractStringAssert<?> getValue() {
		return Assertions.assertThat(actual().getValue());
	}
}
