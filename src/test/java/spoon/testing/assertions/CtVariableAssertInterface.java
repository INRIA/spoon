package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtVariable;
public interface CtVariableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtVariable<?>> extends SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtTypedElementAssertInterface<A, W> , CtModifiableAssertInterface<A, W> {
	default CtExpressionAssertInterface<?, ?> getDefaultExpression() {
		return SpoonAssertions.assertThat(actual().getDefaultExpression());
	}
}
