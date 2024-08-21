package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldAccess;
public interface CtFieldAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFieldAccess<?>> extends CtVariableAccessAssertInterface<A, W> , SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
	default CtFieldReferenceAssertInterface<?, ?> getVariable() {
		return SpoonAssertions.assertThat(actual().getVariable());
	}
}
