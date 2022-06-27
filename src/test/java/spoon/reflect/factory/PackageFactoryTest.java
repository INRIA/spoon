package spoon.reflect.factory;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import spoon.Launcher;
import spoon.reflect.declaration.CtPackage;

class PackageFactoryTest {

	@Test
	void getOrCreate_returnsNestedPackageStructure_whenQualifiedNameRepeatsSimpleName() {
		// contract: A qualified name that is simply repetitions of a single simple name results in the expected
		// nested package structure

		PackageFactory factory = new Launcher().getFactory().Package();
		String topLevelPackageName = "spoon";
		String nestedPackageName = topLevelPackageName + "." + topLevelPackageName;

		CtPackage packageWithDuplicatedSimpleNames = factory.getOrCreate(nestedPackageName);
		CtPackage topLevelPackage = factory.get(topLevelPackageName);

		assertThat(topLevelPackage.getQualifiedName(), equalTo(topLevelPackageName));
		assertThat(packageWithDuplicatedSimpleNames.getQualifiedName(), equalTo(nestedPackageName));

		assertThat(topLevelPackage.getPackage(topLevelPackageName), sameInstance(packageWithDuplicatedSimpleNames));
		assertThat(packageWithDuplicatedSimpleNames.getParent(), sameInstance(topLevelPackage));
	}
}