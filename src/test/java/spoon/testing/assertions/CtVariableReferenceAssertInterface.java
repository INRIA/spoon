package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtVariableReference;
interface CtVariableReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtVariableReference<?>> extends SpoonAssert<A, W> , CtReferenceAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getType() {
		return SpoonAssertions.assertThat(actual().getType());
	}
}
