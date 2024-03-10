package spoon.testing.assertions;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtUsedService;
public interface CtModuleAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtModule> extends SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> {
	default ListAssert<CtPackageExport> getExportedPackages() {
		return Assertions.assertThat(actual().getExportedPackages());
	}

	default ListAssert<CtModuleDirective> getModuleDirectives() {
		return Assertions.assertThat(actual().getModuleDirectives());
	}

	default ListAssert<CtPackageExport> getOpenedPackages() {
		return Assertions.assertThat(actual().getOpenedPackages());
	}

	default ListAssert<CtProvidedService> getProvidedServices() {
		return Assertions.assertThat(actual().getProvidedServices());
	}

	default ListAssert<CtModuleRequirement> getRequiredModules() {
		return Assertions.assertThat(actual().getRequiredModules());
	}

	default CtPackageAssertInterface<?, ?> getRootPackage() {
		return SpoonAssertions.assertThat(actual().getRootPackage());
	}

	default ListAssert<CtUsedService> getUsedServices() {
		return Assertions.assertThat(actual().getUsedServices());
	}

	default AbstractBooleanAssert<?> isOpenModule() {
		return Assertions.assertThat(actual().isOpenModule());
	}
}
