package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;
interface CtModifiableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtModifiable> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
    default AbstractCollectionAssert<?, Collection<? extends CtExtendedModifier>, CtExtendedModifier, ?> getExtendedModifiers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getExtendedModifiers());
    }

    default AbstractCollectionAssert<?, Collection<? extends ModifierKind>, ModifierKind, ?> getModifiers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getModifiers());
    }
}