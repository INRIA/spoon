package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.reference.CtTypeReference;
public interface CtEnumAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtEnum<?>> extends SpoonAssert<A, W> , CtClassAssertInterface<A, W> {
	default ListAssert<CtEnumValue<?>> getEnumValues() {
		return Assertions.assertThat(actual().getEnumValues());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getPermittedTypes() {
		return Assertions.assertThat(actual().getPermittedTypes());
	}
}
