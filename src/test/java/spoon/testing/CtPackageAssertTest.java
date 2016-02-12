package spoon.testing;

import org.junit.Test;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import java.io.File;

import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtPackageAssertTest {
	@Test
	public void testEqualityBetweenTwoCtPackage() throws Exception {
		final Factory factory = createFactory();
		final CtPackage aRootPackage = factory.Package().getOrCreate("");
		aRootPackage.addType(factory.Class().create("spoon.testing.testclasses.Foo").addModifier(ModifierKind.PUBLIC));
		aRootPackage.addType(factory.Class().create("spoon.testing.testclasses.Bar").addModifier(ModifierKind.PUBLIC));
		assertThat(build(new File("./src/test/java/spoon/testing/testclasses/")).Package().getRootPackage()).isEqualTo(aRootPackage);
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoDifferentCtPackage() throws Exception {
		assertThat(build(new File("./src/test/java/spoon/testing/testclasses/")).Package().getRootPackage()).isEqualTo(createFactory().Package().getOrCreate("another.package"));
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoCtPackageWithDifferentTypes() throws Exception {
		final Factory factory = createFactory();
		final CtPackage aRootPackage = factory.Package().getOrCreate("");
		aRootPackage.addType(factory.Class().create("spoon.testing.testclasses.Foo").addModifier(ModifierKind.PUBLIC));
		assertThat(build(new File("./src/test/java/spoon/testing/testclasses/")).Package().getRootPackage()).isEqualTo(aRootPackage);
	}
}
