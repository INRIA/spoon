package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypedElement;
public interface CtTypedElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypedElement<?>> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getType() {
		return SpoonAssertions.assertThat(actual().getType());
	}
}
