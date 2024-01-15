package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
interface CtTypeReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeReference<?>> extends CtTypeInformationAssertInterface<A, W> , SpoonAssert<A, W> , CtActualTypeContainerAssertInterface<A, W> , CtReferenceAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {
    default CtTypeReferenceAssertInterface<?, ?> getDeclaringType() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getDeclaringType());
    }

    default AbstractCollectionAssert<?, Collection<? extends ModifierKind>, ModifierKind, ?> getModifiers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getModifiers());
    }

    default CtPackageReferenceAssertInterface<?, ?> getPackage() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getPackage());
    }

    default AbstractStringAssert<?> getSimpleName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSimpleName());
    }

    default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getSuperInterfaces() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSuperInterfaces());
    }

    default CtTypeReferenceAssertInterface<?, ?> getSuperclass() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getSuperclass());
    }
}