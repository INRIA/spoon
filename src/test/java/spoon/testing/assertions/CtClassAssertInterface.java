package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
interface CtClassAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtClass<?>> extends CtSealableAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtTypeAssertInterface<A, W> {
    default ListAssert<CtAnonymousExecutable> getAnonymousExecutables() {
        return org.assertj.core.api.Assertions.assertThat(actual().getAnonymousExecutables());
    }

    default AbstractCollectionAssert<?, Collection<? extends CtConstructor<?>>, CtConstructor<?>, ?> getConstructors() {
        return org.assertj.core.api.Assertions.assertThat(actual().getConstructors());
    }
}