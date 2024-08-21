package spoon.testing.assertions;
import java.lang.annotation.Annotation;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
public interface CtTypeMemberWildcardImportReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeMemberWildcardImportReference> extends SpoonAssert<A, W> , CtReferenceAssertInterface<A, W> {
	default ListAssert<CtAnnotation<? extends Annotation>> getAnnotations() {
		return Assertions.assertThat(actual().getAnnotations());
	}

	default AbstractStringAssert<?> getSimpleName() {
		return Assertions.assertThat(actual().getSimpleName());
	}

	default CtTypeReferenceAssertInterface<?, ?> getTypeReference() {
		return SpoonAssertions.assertThat(actual().getTypeReference());
	}

	default AbstractBooleanAssert<?> isImplicit() {
		return Assertions.assertThat(actual().isImplicit());
	}
}
