package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtExpression;
public interface CtAbstractInvocationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAbstractInvocation<?>> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default ListAssert<CtExpression<?>> getArguments() {
		return Assertions.assertThat(actual().getArguments());
	}

	default CtExecutableReferenceAssertInterface<?, ?> getExecutable() {
		return SpoonAssertions.assertThat(actual().getExecutable());
	}
}
