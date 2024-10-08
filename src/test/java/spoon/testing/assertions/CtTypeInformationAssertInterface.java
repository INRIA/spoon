package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
public interface CtTypeInformationAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeInformation> extends SpoonAssert<A, W> {
	default AbstractCollectionAssert<?, Collection<? extends ModifierKind>, ModifierKind, ?> getModifiers() {
		return Assertions.assertThat(actual().getModifiers());
	}

	default AbstractStringAssert<?> getQualifiedName() {
		return Assertions.assertThat(actual().getQualifiedName());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getSuperInterfaces() {
		return Assertions.assertThat(actual().getSuperInterfaces());
	}

	default CtTypeReferenceAssertInterface<?, ?> getSuperclass() {
		return SpoonAssertions.assertThat(actual().getSuperclass());
	}
}
