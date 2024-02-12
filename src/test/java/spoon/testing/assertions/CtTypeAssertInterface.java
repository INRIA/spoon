package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.description.Description;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;

import static org.assertj.core.api.Assertions.*;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

public interface CtTypeAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtType<?>> extends CtTypeInformationAssertInterface<A, W> , SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtFormalTypeDeclarerAssertInterface<A, W> , CtShadowableAssertInterface<A, W> , CtTypeMemberAssertInterface<A, W> {
	default ListAssert<CtField<?>> getFields() {
		return Assertions.assertThat(actual().getFields());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtMethod<?>>, CtMethod<?>, ?> getMethods() {
		return Assertions.assertThat(actual().getMethods());
	}

	default AbstractCollectionAssert<?, Collection<? extends ModifierKind>, ModifierKind, ?> getModifiers() {
		return Assertions.assertThat(actual().getModifiers());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtType<?>>, CtType<?>, ?> getNestedTypes() {
		return Assertions.assertThat(actual().getNestedTypes());
	}

	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}

	default ListAssert<CtTypeMember> getTypeMembers() {
		return Assertions.assertThat(actual().getTypeMembers());
	}

	default CtFieldAssert hasField(String fieldName) {
		self().isNotNull();
		return assertThat(actual().getField(fieldName))
				.withFailMessage("Field %s does not exist in %s", fieldName, actual().getSimpleName())
				.isNotNull();
	}
}
