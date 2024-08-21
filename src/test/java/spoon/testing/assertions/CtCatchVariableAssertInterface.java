package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCatchVariable;
public interface CtCatchVariableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCatchVariable<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> , CtMultiTypedElementAssertInterface<A, W> , CtCodeElementAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getType() {
		return SpoonAssertions.assertThat(actual().getType());
	}
}
