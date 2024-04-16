package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.reference.CtFieldReference;
public interface CtFieldReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFieldReference<?>> extends CtVariableReferenceAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getDeclaringType() {
		return SpoonAssertions.assertThat(actual().getDeclaringType());
	}

	default AbstractBooleanAssert<?> isFinal() {
		return Assertions.assertThat(actual().isFinal());
	}

	default AbstractBooleanAssert<?> isStatic() {
		return Assertions.assertThat(actual().isStatic());
	}
}
