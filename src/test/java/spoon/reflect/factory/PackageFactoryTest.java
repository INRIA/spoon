package spoon.reflect.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.testing.utils.GitHubIssue;

class PackageFactoryTest {

	@Test
	@GitHubIssue(issueNumber = 4764, fixed = true)
	void getOrCreate_returnsNestedPackageStructure_whenQualifiedNameRepeatsSimpleName() {
		// contract: A qualified name that is simply repetitions of a single simple name results in the expected
		// nested package structure

		PackageFactory factory = new Launcher().getFactory().Package();
		String topLevelPackageName = "spoon";
		String nestedPackageName = topLevelPackageName + "." + topLevelPackageName;

		CtPackage packageWithDuplicatedSimpleNames = factory.getOrCreate(nestedPackageName);
		CtPackage topLevelPackage = factory.get(topLevelPackageName);

		assertThat(topLevelPackage.getQualifiedName()).isEqualTo(topLevelPackageName);
		assertThat(packageWithDuplicatedSimpleNames.getQualifiedName()).isEqualTo(nestedPackageName);

		assertThat(topLevelPackage.getPackage(topLevelPackageName)).isSameAs(packageWithDuplicatedSimpleNames);
		assertThat(packageWithDuplicatedSimpleNames.getParent()).isSameAs(topLevelPackage);
	}
	
	@Test
	@GitHubIssue(issueNumber = 5140, fixed = true)
	void testGetPackageWithNameContainingDollarSign() {
		// contract: A package with a name containing a dollar sign can be retrieved using the PackageFactory
		// Create a package with a name containing a dollar sign
		String packageName = "com.example.package$with$dollar$sign";
		CtClass<?> clazz = Launcher.parseClass("package " + packageName + ";" + "\n" + "enum Foo { }");

		// Get the package using the PackageFactory
		CtPackage ctPackage = clazz.getFactory().Package().get(packageName);

		// Verify that the package was found
		assertNotNull(ctPackage);
		assertEquals(packageName, ctPackage.getQualifiedName());
	}
}