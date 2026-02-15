package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
public interface CtImportAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtImport> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtReferenceAssertInterface<?, ?> getReference() {
		return SpoonAssertions.assertThat(actual().getReference());
	}

	default ObjectAssert<CtImportKind> getImportKind() {
		return Assertions.assertThatObject(actual().getImportKind());
	}
}
