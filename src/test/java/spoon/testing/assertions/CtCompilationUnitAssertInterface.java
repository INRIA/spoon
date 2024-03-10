package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
public interface CtCompilationUnitAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCompilationUnit> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default CtModuleAssertInterface<?, ?> getDeclaredModule() {
		return SpoonAssertions.assertThat(actual().getDeclaredModule());
	}

	default CtModuleReferenceAssertInterface<?, ?> getDeclaredModuleReference() {
		return SpoonAssertions.assertThat(actual().getDeclaredModuleReference());
	}

	default ListAssert<CtTypeReference<?>> getDeclaredTypeReferences() {
		return Assertions.assertThat(actual().getDeclaredTypeReferences());
	}

	default ListAssert<CtType<?>> getDeclaredTypes() {
		return Assertions.assertThat(actual().getDeclaredTypes());
	}

	default ListAssert<CtImport> getImports() {
		return Assertions.assertThat(actual().getImports());
	}

	default CtPackageDeclarationAssertInterface<?, ?> getPackageDeclaration() {
		return SpoonAssertions.assertThat(actual().getPackageDeclaration());
	}
}
