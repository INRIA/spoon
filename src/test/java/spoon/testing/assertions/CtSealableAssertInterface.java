package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtSealable;
import spoon.reflect.reference.CtTypeReference;
public interface CtSealableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtSealable> extends SpoonAssert<A, W> {
	default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getPermittedTypes() {
		return Assertions.assertThat(actual().getPermittedTypes());
	}
}
