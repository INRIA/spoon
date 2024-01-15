package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.reference.CtModuleReference;
interface CtPackageExportAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPackageExport> extends SpoonAssert<A, W> , CtModuleDirectiveAssertInterface<A, W> {
    default CtPackageReferenceAssertInterface<?, ?> getPackageReference() {
        return spoon.testing.assertions.SpoonAssertions.assertThat(actual().getPackageReference());
    }

    default ListAssert<CtModuleReference> getTargetExport() {
        return org.assertj.core.api.Assertions.assertThat(actual().getTargetExport());
    }

    default AbstractBooleanAssert<?> isOpenedPackage() {
        return org.assertj.core.api.Assertions.assertThat(actual().isOpenedPackage());
    }
}