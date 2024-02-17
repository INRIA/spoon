package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSuperAccess;
public interface CtSuperAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtSuperAccess<?>> extends CtVariableReadAssertInterface<A, W> , SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getType() {
		return SpoonAssertions.assertThat(actual().getType());
	}
}
