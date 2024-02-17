package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.reference.CtModuleReference;
public interface CtPackageExportAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPackageExport> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
	default CtPackageReferenceAssertInterface<?, ?> getPackageReference() {
		return SpoonAssertions.assertThat(actual().getPackageReference());
	}

	default ListAssert<CtModuleReference> getTargetExport() {
		return Assertions.assertThat(actual().getTargetExport());
	}

	default AbstractBooleanAssert<?> isOpenedPackage() {
		return Assertions.assertThat(actual().isOpenedPackage());
	}
}
