package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
public interface CtTypeReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeReference<?>> extends CtTypeInformationAssertInterface<A, W> , SpoonAssert<A, W> , CtActualTypeContainerAssertInterface<A, W> , CtReferenceAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
	default CtTypeReferenceAssertInterface<?, ?> getDeclaringType() {
		return SpoonAssertions.assertThat(actual().getDeclaringType());
	}

	default AbstractCollectionAssert<?, Collection<? extends ModifierKind>, ModifierKind, ?> getModifiers() {
		return Assertions.assertThat(actual().getModifiers());
	}

	default CtPackageReferenceAssertInterface<?, ?> getPackage() {
		return SpoonAssertions.assertThat(actual().getPackage());
	}

	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getSuperInterfaces() {
		return Assertions.assertThat(actual().getSuperInterfaces());
	}

	default CtTypeReferenceAssertInterface<?, ?> getSuperclass() {
		return SpoonAssertions.assertThat(actual().getSuperclass());
	}
}
