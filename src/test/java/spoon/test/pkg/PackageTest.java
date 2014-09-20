package spoon.test.pkg;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.test.pkg.name.PackageTestClass;

public class PackageTest
{
	@Test
	public void testPackage() throws Exception
	{
		final String classFilePath = "./src/test/java/spoon/test/pkg/name/PackageTestClass.java";
		final String packageInfoFilePath = "./src/test/java/spoon/test/pkg/package-info.java";
		final File packageInfoFile = new File(packageInfoFilePath);

		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(factory, SpoonResourceHelper.resources(classFilePath, packageInfoFilePath)).build();

		CtClass<?> clazz = factory.Class().get(PackageTestClass.class);
		Assert.assertEquals(PackageTestClass.class, clazz.getActualClass());

		CtPackage ctPackage = clazz.getPackage();
		Assert.assertEquals("spoon.test.pkg.name", ctPackage.getQualifiedName());
		Assert.assertNull(ctPackage.getPosition());
		Assert.assertNull(ctPackage.getDocComment());
		Assert.assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		Assert.assertEquals("spoon.test.pkg", ctPackage.getQualifiedName());
		Assert.assertNotNull(ctPackage.getPosition());
		Assert.assertEquals(packageInfoFile.getCanonicalPath(), ctPackage.getPosition().getFile().getCanonicalPath());
		Assert.assertEquals(1, ctPackage.getPosition().getLine());
		Assert.assertEquals(1, ctPackage.getAnnotations().size());
		Assert.assertEquals("\n This is test\n JavaDoc.\n \n", ctPackage.getDocComment());

		CtAnnotation<?> annotation = ctPackage.getAnnotations().get(0);
		Assert.assertEquals(Deprecated.class, annotation.getAnnotationType().getActualClass());
		Assert.assertEquals(packageInfoFile.getCanonicalPath(), annotation.getPosition().getFile().getCanonicalPath());
		Assert.assertEquals(5, annotation.getPosition().getLine());

		Assert.assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		Assert.assertEquals("spoon.test", ctPackage.getQualifiedName());
		Assert.assertNull(ctPackage.getPosition());
		Assert.assertNull(ctPackage.getDocComment());
	}
}
