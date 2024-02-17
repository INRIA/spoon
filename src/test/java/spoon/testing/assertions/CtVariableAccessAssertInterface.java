package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableAccess;
public interface CtVariableAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtVariableAccess<?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getType() {
		return SpoonAssertions.assertThat(actual().getType());
	}

	default CtVariableReferenceAssertInterface<?, ?> getVariable() {
		return SpoonAssertions.assertThat(actual().getVariable());
	}
}
