package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAnnotationFieldAccess;
public interface CtAnnotationFieldAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotationFieldAccess<?>> extends CtVariableReadAssertInterface<A, W> , SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {
	default CtFieldReferenceAssertInterface<?, ?> getVariable() {
		return SpoonAssertions.assertThat(actual().getVariable());
	}
}
