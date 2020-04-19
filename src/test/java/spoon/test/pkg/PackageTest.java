/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.pkg;

import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.support.JavaOutputProcessor;
import spoon.test.annotation.testclasses.GlobalAnnotation;
import spoon.test.pkg.name.PackageTestClass;
import spoon.test.pkg.processors.ElementProcessor;
import spoon.test.pkg.testclasses.Foo;
import spoon.testing.utils.ModelUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
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
		assertSame(PackageTestClass.class, clazz.getActualClass());

		CtPackage ctPackage = clazz.getPackage();
		assertEquals("spoon.test.pkg.name", ctPackage.getQualifiedName());
		assertEquals("", ctPackage.getDocComment());
		assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));
		assertFalse(ctPackage.hasPackageInfo());
		assertFalse(ctPackage.isEmpty());

		ctPackage = (CtPackage) ctPackage.getParent();
		assertEquals("spoon.test.pkg", ctPackage.getQualifiedName());
		assertNotNull(ctPackage.getPosition());
		assertEquals(packageInfoFile.getCanonicalPath(), ctPackage.getPosition().getFile().getCanonicalPath());
		assertEquals(1, ctPackage.getPosition().getLine());
		assertEquals(0, ctPackage.getPosition().getSourceStart());
		assertEquals(71, ctPackage.getPosition().getSourceEnd());
		assertEquals(1, ctPackage.getAnnotations().size());
		assertEquals("This is test\nJavaDoc.", ctPackage.getComments().get(0).getContent());
		assertTrue(ctPackage.hasPackageInfo());

		CtAnnotation<?> annotation = ctPackage.getAnnotations().get(0);
		assertSame(Deprecated.class, annotation.getAnnotationType().getActualClass());
		assertEquals(packageInfoFile.getCanonicalPath(), annotation.getPosition().getFile().getCanonicalPath());
		assertEquals(5, annotation.getPosition().getLine());

		assertTrue(CtPackage.class.isAssignableFrom(ctPackage.getParent().getClass()));

		ctPackage = (CtPackage) ctPackage.getParent();
		assertEquals("spoon.test", ctPackage.getQualifiedName());
		assertEquals("", ctPackage.getDocComment());

		//contract: a freshly created package is empty
		assertTrue(factory.createPackage().isEmpty());
	}

	@Test
	public void testAnnotationOnPackage() {
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();

		factory.getEnvironment().setAutoImports(false);
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		launcher.setSourceOutputDirectory("./target/spooned/");
		compiler.addInputSource(new File("./src/test/java/spoon/test/pkg/testclasses/"));
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
	public void testPrintPackageInfoWhenNothingInPackage() {
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
		launcher.addTemplateResource(SpoonResourceHelper.createResource(new File("./src/test/java/spoon/test/pkg/test_templates/FakeTemplate.java")));
		launcher.buildModel();
		launcher.prettyprint();
		canBeBuilt("./target/spooned/packageAndTemplate/spoon/test/pkg/package-info.java", 8);
	}

	@Test
	public void testRenamePackageAndPrettyPrint() {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/pkg/testclasses/Foo.java");
		spoon.buildModel();

		CtPackage ctPackage = spoon.getModel().getElements(new NamedElementFilter<>(CtPackage.class, "spoon")).get(0);
		ctPackage.setSimpleName("otherName");

		CtClass foo = spoon.getModel().getElements(new NamedElementFilter<>(CtClass.class, "Foo")).get(0);
		assertEquals("otherName.test.pkg.testclasses.Foo", foo.getQualifiedName());

		PrettyPrinter prettyPrinter = new DefaultJavaPrettyPrinter(spoon.getEnvironment());
		prettyPrinter.calculate(spoon.getFactory().CompilationUnit().getOrCreate("./src/test/java/spoon/test/pkg/testclasses/Foo.java"), Collections.singletonList(foo));
		String result = prettyPrinter.getResult();

		assertTrue(result.contains("package otherName.test.pkg.testclasses;"));
	}

	@Test
	public void testRenamePackageAndPrettyPrintNoclasspath() {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/app/Test.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();

		CtPackage ctPackage = spoon.getModel().getElements(new NamedElementFilter<>(CtPackage.class, "app")).get(0);
		ctPackage.setSimpleName("otherName");

		CtClass foo = spoon.getModel().getElements(new NamedElementFilter<>(CtClass.class, "Test")).get(0);
		assertEquals("otherName.Test", foo.getQualifiedName());

		PrettyPrinter prettyPrinter = new DefaultJavaPrettyPrinter(spoon.getEnvironment());
		prettyPrinter.calculate(spoon.getFactory().CompilationUnit().getOrCreate("./src/test/resources/noclasspath/app/Test.java"), Collections.singletonList(foo));
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

		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			assertTrue(reader.lines().anyMatch((s) -> {
				return "package newtest;".equals(s);
			}));
		}
	}

	@Test
	public void testRenameRootPackage() {
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
	public void testRenameRootPackageWithNullOrEmpty() {
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

	@Test
	public void testAddAnnotationToPackage() throws Exception {
		// contract: Created package-info should used imports in auto-import
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/pkg/testclasses/Foo.java");
		spoon.getEnvironment().setAutoImports(true);
		File outputDir = new File("./target/spoon-packageinfo");
		spoon.getEnvironment().setSourceOutputDirectory(outputDir);

		spoon.buildModel();

		CtAnnotationType annotationType = (CtAnnotationType)spoon.getFactory().Annotation().get(GlobalAnnotation.class);
		CtAnnotation annotation = spoon.getFactory().Core().createAnnotation();
		annotation.setAnnotationType(annotationType.getReference());
		CtPackage ctPackage = spoon.getFactory().Package().get("spoon.test.pkg.testclasses");
		ctPackage.addAnnotation(annotation);


		JavaOutputProcessor outputProcessor = spoon.createOutputWriter();
		outputProcessor.process(ctPackage);

		File packageInfo = new File(outputDir, "spoon/test/pkg/testclasses/package-info.java");
		assertTrue(packageInfo.exists());

		canBeBuilt(packageInfo, 8);

		List<String> lines = Files.readAllLines(packageInfo.toPath());

		assertFalse(lines.isEmpty());

		for (String s : lines) {
			if (s.trim().startsWith("import")) {
				assertEquals("import spoon.test.annotation.testclasses.GlobalAnnotation;", s.trim());
			}
			if (s.trim().startsWith("@")) {
				assertEquals("@GlobalAnnotation", s.trim());
			}
		}
	}

	@Test
	public void testNoPackageAssumptionWithStarImportNoClasspath() {
		// contract: The package of a type not on the classpath should not guessed if there is an unresolved star import
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/TypeAccessStarImport.java");
		CtModel model = launcher.buildModel();

		List<CtTypeAccess<?>> typeAccesses = model.getElements(e -> e.getAccessedType().getSimpleName().equals("SomeClass"));
		assertEquals("There should only be a single type access in the test source code", 1, typeAccesses.size());
		CtPackageReference pkgRef = typeAccesses.get(0).getAccessedType().getPackage();

		assertTrue(pkgRef.getSimpleName().isEmpty());
		assertTrue(pkgRef.isImplicit());
	}

	@Test
	public void testGetFQNSimple() {
		// contract: CtPackageReference simple name is also the fully qualified name of its referenced package
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/pkg/testclasses/Foo.java");
		spoon.buildModel();

		CtClass fooClass = spoon.getFactory().Class().get(Foo.class);
		CtField field = fooClass.getField("fieldList");
		CtPackageReference fieldPkg = field.getType().getPackage();

		assertEquals("java.util", fieldPkg.getSimpleName());
		assertEquals("java.util", fieldPkg.getQualifiedName());
	}

	@Test
	public void testGetFQNInNoClassPath() {
		// contract: CtPackageReference simple name is also the fully qualified name of its referenced package, even in noclasspath
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/noclasspath/TorIntegration.java");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.buildModel();

		CtClass torClass = spoon.getFactory().Class().get("com.duckduckgo.mobile.android.util.TorIntegration");

		CtField field = torClass.getField("orbotHelper");
		CtPackageReference fieldPkg = field.getType().getPackage();

		assertEquals("info.guardianproject.onionkit.ui", fieldPkg.getSimpleName());
		assertEquals("info.guardianproject.onionkit.ui", fieldPkg.getQualifiedName());
	}
}
