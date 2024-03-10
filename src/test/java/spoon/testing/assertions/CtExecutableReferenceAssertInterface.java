package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
public interface CtExecutableReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtExecutableReference<?>> extends SpoonAssert<A, W> , CtActualTypeContainerAssertInterface<A, W> , CtReferenceAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getDeclaringType() {
		return SpoonAssertions.assertThat(actual().getDeclaringType());
	}

	default ListAssert<CtTypeReference<?>> getParameters() {
		return Assertions.assertThat(actual().getParameters());
	}

	default CtTypeReferenceAssertInterface<?, ?> getType() {
		return SpoonAssertions.assertThat(actual().getType());
	}

	default AbstractBooleanAssert<?> isStatic() {
		return Assertions.assertThat(actual().isStatic());
	}
}
