package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtSealable;
import spoon.reflect.reference.CtTypeReference;
interface CtSealableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtSealable> extends SpoonAssert<A, W> {
    default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getPermittedTypes() {
        return org.assertj.core.api.Assertions.assertThat(actual().getPermittedTypes());
    }
}