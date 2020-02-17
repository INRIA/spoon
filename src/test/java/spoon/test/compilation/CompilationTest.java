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
package spoon.test.compilation;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.compiler.jdt.JDTBatchCompiler;
import spoon.test.compilation.testclasses.Bar;
import spoon.test.compilation.testclasses.IBar;
import spoon.test.compilation.testclasses.Ifoo;
import spoon.testing.utils.ModelUtils;

public class CompilationTest {

	@Test
	public void compileTestWithImportStaticWildcard() {

		/*
			Test the compilation of a java file with an import static with wildcard
		 */

		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/compilation/");
		launcher.getEnvironment().setShouldCompile(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setBinaryOutputDirectory("target/spooned-classes/");
		launcher.getEnvironment().setSourceOutputDirectory(new File("target/spooned/"));
		launcher.run();

		SpoonModelBuilder compiler = launcher.createCompiler();
		boolean compile = compiler.compile(SpoonModelBuilder.InputType.CTTYPES);
		final String nl = System.getProperty("line.separator");
		assertTrue(
				nl + "the compilation should succeed: " + nl +
						((JDTBasedSpoonCompiler) compiler).getProblems()
								.stream()
								.filter(IProblem::isError)
								.map(CategorizedProblem::toString)
								.collect(Collectors.joining(nl)),
				compile
		);
	}

	@Test
	public void compileCommandLineTest() {
		// the --compile option works, shouldCompile is set

		String sourceFile = "./src/test/resources/noclasspath/Simple.java";
		String compiledFile = "./spooned-classes/Simple.class";

		// ensuring clean state
		new File(compiledFile).delete();

		Launcher launcher = new Launcher();

		launcher.run(new String[]{
				"-i", sourceFile,
				"-o", "target/spooned",
				"--compile",
				"--compliance", "7",
				"--level", "OFF"
		});

		assertTrue(launcher.getEnvironment().shouldCompile());

		assertTrue(new File(compiledFile).exists());
	}

	@Test
	public void compileTest() throws Exception {
		// contract: the modified version of classes is the one that is compiled to binary code
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Simple.java");
		File outputBinDirectory = new File("./target/class-simple");
		if (!outputBinDirectory.exists()) {
			outputBinDirectory.mkdirs();
		}
		launcher.setBinaryOutputDirectory(outputBinDirectory);
		launcher.getEnvironment().setShouldCompile(true);
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CoreFactory core = factory.Core();
		CodeFactory code = factory.Code();

		CtClass simple = factory.Class().get("Simple");

		CtMethod method = core.createMethod();
		method.addModifier(ModifierKind.PUBLIC);
		method.setType(factory.Type().integerPrimitiveType());
		method.setSimpleName("m");

		CtBlock block = core.createBlock();
		CtReturn aReturn = core.createReturn();

		CtBinaryOperator binaryOperator = code.createBinaryOperator(
				code.createLiteral(10),
				code.createLiteral(32),
				BinaryOperatorKind.PLUS);
		aReturn.setReturnedExpression(binaryOperator);

		// return 10 + 32;
		block.addStatement(aReturn);
		method.setBody(block);

		simple.addMethod(method);

		launcher.getModelBuilder().compile();

		final URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{outputBinDirectory.toURL()});

		Class<?> aClass = urlClassLoader.loadClass("Simple");
		Method m = aClass.getMethod("m");
		assertEquals(42, m.invoke(aClass.newInstance()));
	}

	@Test
	public void testNewInstanceFromExistingClass() throws Exception {
		CtClass<Bar> barCtType = (CtClass<Bar>) ModelUtils.buildClass(Bar.class);
		CtReturn<Integer> m = barCtType.getMethod("m").getBody().getStatement(0);
		// we cannot use Bar because it causes a runtime cast exception (2 different Bar from different classloader)
		IBar bar = barCtType.newInstance();
		int value = bar.m();
		assertEquals(1, value);

		// change the return value
		m.setReturnedExpression(m.getFactory().Code().createLiteral(2));

		bar = barCtType.newInstance();
		value = bar.m();
		assertEquals(2, value);

		m.replace(m.getFactory().Code().createCodeSnippetStatement("throw new FooEx()"));
		try {
			bar = barCtType.newInstance();
			value = bar.m();
			fail();
		} catch (Exception ignore) { }

	}

	@Test
	public void testNewInstance() {
		// contract: a ctclass can be instantiated, and each modification results in a new valid object
		Factory factory = new Launcher().getFactory();
		CtClass<Ifoo> c = factory.Code().createCodeSnippetStatement(
				"class X implements spoon.test.compilation.testclasses.Ifoo { public int foo() {int i=0; return i;} }").compile();
		c.addModifier(ModifierKind.PUBLIC); // required otherwise java.lang.IllegalAccessException at runtime when instantiating

		CtBlock body = c.getElements(new TypeFilter<>(CtBlock.class)).get(1);
		Ifoo o = c.newInstance();
		assertEquals(0, o.foo());
		for (int i = 1; i <= 10; i++) {
			body.getStatement(0).replace(factory.Code().createCodeSnippetStatement("int i = " + i + ";"));
			o = c.newInstance();
			// each time this is a new class
			// each time the behavior has changed!
			assertEquals(i, o.foo());
		}

	}

	@Test
	public void testFilterResourcesFile() {
		// shows how to filter input java files, for https://github.com/INRIA/spoon/issues/877
		Launcher launcher = new Launcher() {
			@Override
			public SpoonModelBuilder createCompiler() {
				return new JDTBasedSpoonCompiler(getFactory()) {
					@Override
					protected JDTBatchCompiler createBatchCompiler() {
						return new JDTBatchCompiler(this) {
							@Override
							public CompilationUnit[] getCompilationUnits() {
								List<CompilationUnit> units = new ArrayList<>();
								for (CompilationUnit u : super.getCompilationUnits()) {
									if (new String(u.getMainTypeName()).contains("Foo")) {
										units.add(u);
									}
								}
								return units.toArray(new CompilationUnit[0]);
							}
						};
					}
				};
			}
		};

		launcher.addInputResource("./src/test/java/spoon/test/imports");
		launcher.buildModel();
		int n = 0;
		// we indeed only have types declared in a file called *Foo*
		for (CtType<?> t : launcher.getFactory().getModel().getAllTypes()) {
			n++;
			assertTrue(t.getPosition().getFile().getAbsolutePath().contains("Foo"));
		}
		assertTrue(n >= 2);

	}

	@Test
	public void testFilterResourcesDir() {
		// shows how to filter input java dir
		// only in package called "reference"
		Launcher launcher = new Launcher() {
			@Override
			public SpoonModelBuilder createCompiler() {
				return new JDTBasedSpoonCompiler(getFactory()) {
					@Override
					protected JDTBatchCompiler createBatchCompiler() {
						return new JDTBatchCompiler(this) {
							@Override
							public CompilationUnit[] getCompilationUnits() {
								List<CompilationUnit> units = new ArrayList<>();
								for (CompilationUnit u : super.getCompilationUnits()) {
									if (new String(u.getFileName()).replace('\\', '/').contains("/reference/")) {
										units.add(u);
									}
								}
								return units.toArray(new CompilationUnit[0]);
							}
						};
					}
				};
			}
		};

		launcher.addInputResource("./src/test/java/spoon/test");
		launcher.buildModel();

		// we indeed only have types declared in a file in package reference
		int n = 0;
		for (CtType<?> t : launcher.getModel().getAllTypes()) {
			n++;
			assertTrue(t.getQualifiedName().contains("reference"));
		}
		assertTrue(n >= 2);
	}

	@Test
	public void testPrecompile() {
		// without precompile
		Launcher l = new Launcher();
		l.setArgs(new String[]{"-i", "src/test/resources/compilation/"});
		l.buildModel();
		CtClass klass = l.getFactory().Class().get("compilation.Bar");
		// without precompile, actualClass does not exist (an exception is thrown)
		try {
			klass.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass();
			fail();
		} catch (SpoonClassNotFoundException ignore) { }

		// with precompile
		Launcher l2 = new Launcher();
		l2.setArgs(new String[]{"--precompile", "-i", "src/test/resources/compilation/"});
		l2.buildModel();
		CtClass klass2 = l2.getFactory().Class().get("compilation.Bar");
		// with precompile, actualClass is not null
		Class actualClass = klass2.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass();
		assertNotNull(actualClass);
		assertEquals("IBar", actualClass.getSimpleName());

		// precompile can be used to compile processors on the fly
		Launcher l3 = new Launcher();
		l3.setArgs(new String[]{"--precompile", "-i", "src/test/resources/compilation/", "-p", "compilation.SimpleProcessor"});
		l3.run();
	}

	@Test
	public void testClassLoader() throws Exception {
		// contract: the environment exposes a classloader configured by the spoonclass path
		Launcher launcher = new Launcher();

		// not in the classpath
		try {
			Class.forName("spoontest.a.ClassA");
			fail();
		} catch (ClassNotFoundException expected) {
		}

		// not in the spoon classpath before setting it
		try {
			launcher.getEnvironment().getInputClassLoader().loadClass("spoontest.a.ClassA");
			fail();
		} catch (ClassNotFoundException expected) {
		}

		launcher.getEnvironment().setSourceClasspath(new String[]{"src/test/resources/reference-test-2/ReferenceTest2.jar"});

		Class c = launcher.getEnvironment().getInputClassLoader().loadClass("spoontest.a.ClassA");
		assertEquals("spoontest.a.ClassA", c.getName());
	}

	@Test
	public void testSingleClassLoader() throws Exception {
		/*
		 *  contract: the environment exposes a classloader configured by the spoonclass path,
		 *  there is one class loader, so the loaded classes are compatible
		 */
		Launcher launcher = new Launcher();
		launcher.addInputResource(new FileSystemFolder("./src/test/resources/classloader-test"));
		File outputBinDirectory = new File("./target/classloader-test");
		if (!outputBinDirectory.exists()) {
			outputBinDirectory.mkdirs();
		}
		launcher.setBinaryOutputDirectory(outputBinDirectory);
		launcher.getModelBuilder().build();

		CtTypeReference<?> mIFoo = launcher.getFactory().Type().createReference("spoontest.IFoo");
		CtTypeReference<?> mFoo = launcher.getFactory().Type().createReference("spoontest.Foo");
		assertTrue("Foo subtype of IFoo", mFoo.isSubtypeOf(mIFoo));

		launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);

		//Create new launcher which uses classes compiled by previous launcher.
		//It simulates the classes without sources, which has to be accessed using reflection
		launcher = new Launcher();

		// not in the classpath
		try {
			Class.forName("spoontest.IFoo");
			fail();
		} catch (ClassNotFoundException expected) {
		}

		// not in the spoon classpath before setting it
		try {
			launcher.getEnvironment().getInputClassLoader().loadClass("spoontest.IFoo");
			fail();
		} catch (ClassNotFoundException expected) {
		}

		launcher.getEnvironment().setSourceClasspath(new String[]{outputBinDirectory.getAbsolutePath()});

		mIFoo = launcher.getFactory().Type().createReference("spoontest.IFoo");
		mFoo = launcher.getFactory().Type().createReference("spoontest.Foo");
		//if it fails then it is because each class is loaded by different class loader
		assertTrue("Foo subtype of IFoo", mFoo.isSubtypeOf(mIFoo));

		// not in the spoon classpath before setting it
		Class<?> ifoo = launcher.getEnvironment().getInputClassLoader().loadClass("spoontest.IFoo");
		Class<?> foo = launcher.getEnvironment().getInputClassLoader().loadClass("spoontest.Foo");

		assertTrue(ifoo.isAssignableFrom(foo));
		assertSame(ifoo.getClassLoader(), foo.getClassLoader());
	}

	@Test
	public void testExoticClassLoader() {
		// contract: Spoon uses the exotic class loader

		final List<String> l = new ArrayList<>();
		class MyClassLoader extends ClassLoader {
			@Override
			protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
				l.add(name);
				return super.loadClass(name, resolve);
			}
		}

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setInputClassLoader(new MyClassLoader());
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/reference-test/Foo.java");
		launcher.buildModel();
		launcher.getModel().getRootPackage().accept(new CtScanner() {
			@Override
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				try {
					// forcing loading it
					reference.getTypeDeclaration();
				} catch (SpoonClassNotFoundException ignore) { }
			}
		});

		//JDK 9 has implicit constructor, while JDK 8 has not
		assertTrue(l.size() >= 2);
		assertTrue(l.contains("KJHKY"));
		assertSame(MyClassLoader.class, launcher.getEnvironment().getInputClassLoader().getClass());
	}

	@Test
	public void testURLClassLoader() throws Exception {
		// contract: Spoon handles URLClassLoader and retrieves path elements

		String expected = "target/classes/";

		File f = new File(expected);
		URL[] urls = {f.toURL()};
		URLClassLoader urlClassLoader = new URLClassLoader(urls);
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setInputClassLoader(urlClassLoader);

		String[] sourceClassPath = launcher.getEnvironment().getSourceClasspath();
		assertEquals(1, sourceClassPath.length);
		String tail = sourceClassPath[0].substring(sourceClassPath[0].length() - expected.length());
		assertEquals(expected, tail);
	}

	@Test
	public void testURLClassLoaderWithOtherResourcesThanOnlyFiles() throws Exception {
		// contract: Spoon handles URLClassLoader which contain other resources than only files by not adding anything

		String file = "target/classes/";
		String distantJar = "http://central.maven.org/maven2/fr/inria/gforge/spoon/spoon-core/5.8.0/spoon-core-5.8.0.jar";

		File f = new File(file);
		URL url = new URL(distantJar);
		URL[] urls = {f.toURL(), url};
		URLClassLoader urlClassLoader = new URLClassLoader(urls);
		Launcher launcher = new Launcher();
		try {
			launcher.getEnvironment().setInputClassLoader(urlClassLoader);
			fail();
		} catch (SpoonException e) {
			assertTrue(e.getMessage().contains("Spoon does not support a URLClassLoader containing other resources than local file."));
		}
	}

	@Test
	public void testCompilationInEmptyDir() throws Exception {
		// Contract: Spoon can be launched in an empty folder as a working directory
		// See: https://github.com/INRIA/spoon/pull/1208 and https://github.com/INRIA/spoon/issues/1246
		// This test does not fail (it's not enough to change user.dir we should launch process inside that dir) but it explains the problem
		String userDir = System.getProperty("user.dir");
		File testFile = new File("src/test/resources/compilation/compilation-tests/IBar.java");
		String absoluteTestPath = testFile.getAbsolutePath();

		Path tempDirPath = Files.createTempDirectory("test_compilation");

		System.setProperty("user.dir", tempDirPath.toFile().getAbsolutePath());
		SpoonModelBuilder compiler = new Launcher().createCompiler();
		compiler.addInputSource(new File(absoluteTestPath));
		compiler.setBinaryOutputDirectory(tempDirPath.toFile());
		compiler.compile(SpoonModelBuilder.InputType.FILES);
		System.setProperty("user.dir", userDir);

		assertThat(tempDirPath.toFile().listFiles().length, not(0));
	}
	
	@Test
	public void testCompileUnresolvedFullyQualifiedName() {
		//contract: the unresolved fully qualified type reference must not cause model building problem
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("src/test/resources/compilation2/UnresolvedFullQualifiedType.java");
		l.buildModel();
	}

	@Test
	public void testBuildAstWithSyntheticMethods() {
		File testFile = new File(
				"src/test/resources/syntheticMethods/ClassWithSyntheticEnumParsable.java");
		String absoluteTestPath = testFile.getAbsolutePath();

		Launcher launcher = new Launcher();
		launcher.addInputResource(absoluteTestPath);

		launcher.buildModel();
		CtType t=launcher.getFactory().Type().get("ClassWithSyntheticEnumParsable");
		assertEquals(2, t.getMethods().size());
	}

	@Test
	public void testBuildAstWithSyntheticMethodsSwapOrder() {
    	// contract: we can handle non annotation methods, no exception
		File testFile = new File(
				"src/test/resources/syntheticMethods/ClassWithSyntheticEnumNotParsable.java");
		String absoluteTestPath = testFile.getAbsolutePath();

		Launcher launcher = new Launcher();
		launcher.addInputResource(absoluteTestPath);
		launcher.buildModel();
		CtType t=launcher.getFactory().Type().get("ClassWithSyntheticEnumNotParsable");
		assertEquals(2, t.getMethods().size());
	}

	@Test
	public void buildAstWithDuplicateClass() {
		// contract: one can have inner classes with the same name
		File testFile = new File(
				"src/test/resources/duplicateClass/DuplicateInnerClass.java");
		String absoluteTestPath = testFile.getAbsolutePath();

		Launcher launcher = new Launcher();
		launcher.addInputResource(absoluteTestPath);
		final CtModel model = launcher.buildModel();
		final List<String> pkgNames = model.getElements(new TypeFilter<>(CtPackage.class))
				.stream()
				.map(CtPackage::getQualifiedName)
				.collect(Collectors.toList());
		assertTrue(pkgNames.contains("P.F.G"));
		final List<String> classNames = model.getElements(new TypeFilter<>(CtType.class))
				.stream()
				.map(CtType::getQualifiedName)
				.collect(Collectors.toList());
		assertTrue(classNames.contains("P.F.G.DuplicateInnerClass"));
		assertTrue(classNames.contains("P.F.G.DuplicateInnerClass$B"));
		assertTrue(classNames.contains("P.F.G.DuplicateInnerClass$B$B"));
	}
}
