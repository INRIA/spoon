package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.reference.CtArrayTypeReference;
public interface CtArrayTypeReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtArrayTypeReference<?>> extends CtTypeReferenceAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getComponentType() {
		return SpoonAssertions.assertThat(actual().getComponentType());
	}

	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}
}
