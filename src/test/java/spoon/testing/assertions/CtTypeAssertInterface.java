package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
interface CtTypeAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtType<?>> extends CtTypeInformationAssertInterface<A, W> , SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtFormalTypeDeclarerAssertInterface<A, W> , CtShadowableAssertInterface<A, W> , CtTypeMemberAssertInterface<A, W> {
    default ListAssert<CtField<?>> getFields() {
        return org.assertj.core.api.Assertions.assertThat(actual().getFields());
    }

    default AbstractCollectionAssert<?, Collection<? extends CtMethod<?>>, CtMethod<?>, ?> getMethods() {
        return org.assertj.core.api.Assertions.assertThat(actual().getMethods());
    }

    default AbstractCollectionAssert<?, Collection<? extends ModifierKind>, ModifierKind, ?> getModifiers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getModifiers());
    }

    default AbstractCollectionAssert<?, Collection<? extends CtType<?>>, CtType<?>, ?> getNestedTypes() {
        return org.assertj.core.api.Assertions.assertThat(actual().getNestedTypes());
    }

    default AbstractStringAssert<?> getSimpleName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getSimpleName());
    }

    default ListAssert<CtTypeMember> getTypeMembers() {
        return org.assertj.core.api.Assertions.assertThat(actual().getTypeMembers());
    }
}