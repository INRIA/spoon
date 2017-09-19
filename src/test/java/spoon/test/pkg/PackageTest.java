package spoon.test.pkg;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.test.pkg.name.PackageTestClass;
import spoon.test.pkg.testclasses.ElementProcessor;
import spoon.testing.utils.ModelUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;

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
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.addInputSource(new File("./src/test/java/spoon/test/pkg/testclasses/"));
		compiler.setSourceOutputDirectory(new File("./target/spooned/"));
		compiler.build();
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

		final SpoonModelBuilder newCompiler = launcher.createCompiler(launcher.createFactory());
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

	@Test
	public void testRenamePackageAndPrettyPrint() throws Exception {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/pkg/testclasses/Foo.java");
		spoon.buildModel();

		CtPackage ctPackage = spoon.getModel().getElements(new NamedElementFilter<CtPackage>(CtPackage.class, "spoon")).get(0);
		ctPackage.setSimpleName("otherName");

		CtClass foo = spoon.getModel().getElements(new NamedElementFilter<CtClass>(CtClass.class, "Foo")).get(0);
		assertEquals("otherName.test.pkg.testclasses.Foo", foo.getQualifiedName());

		PrettyPrinter prettyPrinter = new DefaultJavaPrettyPrinter(spoon.getEnvironment());
		prettyPrinter.calculate(spoon.getFactory().CompilationUnit().create("./src/test/java/spoon/test/pkg/testclasses/Foo.java"), Collections.singletonList(foo));
		String result = prettyPrinter.getResult();

		assertTrue(result.contains("package otherName.test.pkg.testclasses;"));
	}

	@Test
	public void testRenamePackageAndPrettyPrintNoclasspath() throws Exception {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/app/Test.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();

		CtPackage ctPackage = spoon.getModel().getElements(new NamedElementFilter<CtPackage>(CtPackage.class, "app")).get(0);
		ctPackage.setSimpleName("otherName");

		CtClass foo = spoon.getModel().getElements(new NamedElementFilter<CtClass>(CtClass.class, "Test")).get(0);
		assertEquals("otherName.Test", foo.getQualifiedName());

		PrettyPrinter prettyPrinter = new DefaultJavaPrettyPrinter(spoon.getEnvironment());
		prettyPrinter.calculate(spoon.getFactory().CompilationUnit().create("./src/test/resources/noclasspath/app/Test.java"), Collections.singletonList(foo));
		String result = prettyPrinter.getResult();

		assertTrue(result.contains("package otherName;"));
	}

	@Test
	public void testRenamePackageAndPrettyPrintWithProcessor() throws Exception {
		String destPath = "./target/spoon-rename-processor";
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/app/Test.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.addProcessor(new ElementProcessor());
		spoon.setSourceOutputDirectory(destPath);
		spoon.run();

		String fileDir = destPath+"/newtest/Test.java";
		File f = new File(fileDir);
		assertTrue(f.exists());

		BufferedReader reader = new BufferedReader(new FileReader(f));
		assertTrue(reader.lines().anyMatch((s) -> {
			return s.equals("package newtest;");
		}));
	}

	@Test
	public void testRenameRootPackage() throws Exception {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/app/Test.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();

		CtPackage rootPackage = spoon.getFactory().Package().getRootPackage();
		String rootPackageName = rootPackage.getSimpleName();
		rootPackage.setSimpleName("test");
		assertEquals(rootPackageName, rootPackage.getSimpleName());
	}

	@Test
	public void testRenameRootPackageWithNullOrEmpty() throws Exception {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/app/Test.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();

		CtPackage rootPackage = spoon.getFactory().Package().getRootPackage();
		String rootPackageName = rootPackage.getSimpleName();
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME, rootPackageName);

		rootPackage.setSimpleName("");
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME, rootPackageName);

		rootPackage.setSimpleName(null);
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME, rootPackageName);
	}
}
