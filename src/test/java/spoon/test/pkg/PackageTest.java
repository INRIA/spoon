package spoon.test.pkg;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.test.pkg.name.PackageTestClass;
import spoon.testing.utils.ModelUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class PackageTest {
	@Test
	public void testPackage() throws Exception {
		final String classFilePath = "./src/test/java/spoon/test/pkg/name/PackageTestClass.java";
		final String packageInfoFilePath = "./src/test/java/spoon/test/pkg/package-info.java";
		final File packageInfoFile = new File(packageInfoFilePath);

		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setCommentEnabled(true);
		spoon.createCompiler(factory, SpoonResourceHelper.resources(classFilePath, packageInfoFilePath)).build();

		CtClass<?> clazz = factory.Class().get(PackageTestClass.class);
		Assert.assertEquals(PackageTestClass.class, clazz.getActualClass());

		CtPackage ctPackage = clazz.getPackage();
		Assert.assertEquals("spoon.test.pkg.name", ctPackage.getQualifiedName());
		Assert.assertNull(ctPackage.getDocComment());
		assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		Assert.assertEquals("spoon.test.pkg", ctPackage.getQualifiedName());
		Assert.assertNotNull(ctPackage.getPosition());
		Assert.assertEquals(packageInfoFile.getCanonicalPath(), ctPackage.getPosition().getFile().getCanonicalPath());
		Assert.assertEquals(1, ctPackage.getPosition().getLine());
		Assert.assertEquals(1, ctPackage.getAnnotations().size());
		Assert.assertEquals("This is test\nJavaDoc.", ctPackage.getComments().get(0).getContent());

		CtAnnotation<?> annotation = ctPackage.getAnnotations().get(0);
		Assert.assertEquals(Deprecated.class, annotation.getAnnotationType().getActualClass());
		Assert.assertEquals(packageInfoFile.getCanonicalPath(), annotation.getPosition().getFile().getCanonicalPath());
		Assert.assertEquals(5, annotation.getPosition().getLine());

		assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		Assert.assertEquals("spoon.test", ctPackage.getQualifiedName());
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

	@Test
	public void testPrintPackageInfoWhenNothingInPackage() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/pkg/testclasses/internal");
		launcher.setSourceOutputDirectory("./target/spooned/package");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.run();

		final CtPackage aPackage = launcher.getFactory().Package().get("spoon.test.pkg.testclasses.internal");
		assertEquals(1, aPackage.getAnnotations().size());
		assertEquals(3, aPackage.getComments().size());
		assertEquals(CtComment.CommentType.JAVADOC, aPackage.getComments().get(0).getCommentType());
		assertEquals(CtComment.CommentType.BLOCK, aPackage.getComments().get(1).getCommentType());
		assertEquals(CtComment.CommentType.INLINE, aPackage.getComments().get(2).getCommentType());

		assertThat(aPackage).isEqualTo(ModelUtils.build(new File("./target/spooned/package/spoon/test/pkg/testclasses/internal")).Package().get("spoon.test.pkg.testclasses.internal"));
	}
	
	@Test
	public void testAnnotationInPackageInfoWhenTemplatesCompiled() throws Exception {
		final Launcher launcher = new Launcher();
		Environment environment = launcher.getEnvironment();
		
		environment.setAutoImports(true);
		environment.setCommentEnabled(true);
		launcher.addInputResource("./src/test/java/spoon/test/pkg/package-info.java");
		launcher.setSourceOutputDirectory("./target/spooned/packageAndTemplate");
//		SpoonResourceHelper.resources("./src/test/java/spoon/test/pkg/test_templates").forEach(r->launcher.addTemplateResource(r));
		launcher.addTemplateResource(SpoonResourceHelper.createResource(new File("./src/test/java/spoon/test/pkg/test_templates/FakeTemplate.java")));
		launcher.buildModel();
		launcher.prettyprint();
		canBeBuilt("./target/spooned/packageAndTemplate/spoon/test/pkg/package-info.java", 8);
	}
}
