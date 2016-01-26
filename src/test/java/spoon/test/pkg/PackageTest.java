package spoon.test.pkg;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.test.pkg.name.PackageTestClass;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PackageTest {
	@Test
	public void testPackage() throws Exception {
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
		assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		Assert.assertEquals("spoon.test.pkg", ctPackage.getQualifiedName());
		Assert.assertNotNull(ctPackage.getPosition());
		Assert.assertEquals(packageInfoFile.getCanonicalPath(), ctPackage.getPosition().getFile().getCanonicalPath());
		Assert.assertEquals(1, ctPackage.getPosition().getLine());
		Assert.assertEquals(1, ctPackage.getAnnotations().size());
		Assert.assertEquals("This is test\n JavaDoc.", ctPackage.getDocComment());

		CtAnnotation<?> annotation = ctPackage.getAnnotations().get(0);
		Assert.assertEquals(Deprecated.class, annotation.getAnnotationType().getActualClass());
		Assert.assertEquals(packageInfoFile.getCanonicalPath(), annotation.getPosition().getFile().getCanonicalPath());
		Assert.assertEquals(5, annotation.getPosition().getLine());

		assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		Assert.assertEquals("spoon.test", ctPackage.getQualifiedName());
		Assert.assertNull(ctPackage.getPosition());
		Assert.assertNull(ctPackage.getDocComment());
	}

	@Test
	public void testAnnotationOnPackage() throws Exception {
		Launcher launcher = new Launcher();
		Factory factory = launcher.createFactory();

		factory.getEnvironment().setDefaultFileGenerator(launcher.createOutputWriter(new File("./target/spooned/"), factory.getEnvironment()));
		factory.getEnvironment().setAutoImports(false);
		SpoonCompiler compiler = launcher.createCompiler(factory);
		compiler.addInputSource(new File("./src/test/java/spoon/test/pkg/testclasses/"));
		compiler.setSourceOutputDirectory(new File("./target/spooned/"));
		compiler.build();
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

		final SpoonCompiler newCompiler = launcher.createCompiler(launcher.createFactory());
		newCompiler.addInputSource(new File("./target/spooned/spoon/test/pkg/testclasses/"));

		try {
			assertTrue(newCompiler.build());
		} catch (Exception ignore) {
			fail();
		}
	}
}
