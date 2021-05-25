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
package spoon.test.api;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.compiler.InvalidClassPathException;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DerivedProperty;
import spoon.support.JavaOutputProcessor;
import spoon.support.OutputDestinationHandler;
import spoon.support.UnsettableProperty;
import spoon.support.compiler.SnippetCompilationError;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.template.Local;
import spoon.template.TemplateMatcher;
import spoon.template.TemplateParameter;
import spoon.test.api.processors.AwesomeProcessor;
import spoon.test.api.testclasses.Bar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class APITest {

	@Test
	public void testBasicAPIUsage() {
		// this test shows a basic usage of the Launcher API without command line
		// and asserts there is no exception
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--compile", "--output-type", "compilationunits" });
		spoon.addInputResource("src/test/resources/spoon/test/api");
		spoon.run();
		Factory factory = spoon.getFactory();
		for (CtPackage p : factory.Package().getAll()) {
			spoon.getEnvironment().debugMessage("package: " + p.getQualifiedName());
		}
		for (CtType<?> s : factory.Class().getAll()) {
			spoon.getEnvironment().debugMessage("class: " + s.getQualifiedName());
		}
	}

	@Test
	public void testOverrideOutputWriter() {
		// this test that we can correctly set the Java output processor
		final List<Object> l = new ArrayList<>();
		Launcher spoon = new Launcher() {
			@Override
			public JavaOutputProcessor createOutputWriter() {
				return new JavaOutputProcessor() {
					@Override
					public void process(CtNamedElement e) {
						l.add(e);
					}
					@Override
					public void init() {
						// we do nothing
					}

				};
			}

		};
		spoon.run(new String[] {
				"-i", "src/test/resources/spoon/test/api/",
				"-o", "fancy/fake/apitest" // we shouldn't write anything anyway
		});
		assertEquals(3, l.size());
	}

	@Test
	public void testDuplicateEntry() throws Exception {
		// it's possible to pass twice the same file as parameter
		// the virtual folder removes the duplicate before passing to JDT
		try {
			String duplicateEntry = "src/test/resources/spoon/test/api/Foo.java";

			// check on the JDK API
			// this is later use by FileSystemFile
			assertTrue(new File(duplicateEntry).getCanonicalFile().equals(new File("./" + duplicateEntry).getCanonicalFile()));

			Launcher.main(new String[] {
					"-i",
					// note the nasty ./
					duplicateEntry + File.pathSeparator + "./" + duplicateEntry,
					"-o", "target/spooned/apitest" });
		} catch (IllegalArgumentException e) { // from JDT
			fail();
		}
	}

	@Test
	public void testDuplicateFolder() {
		// it's possible to pass twice the same folder as parameter
		// the virtual folder removes the duplicate before passing to JDT
		try {
			String duplicateEntry = "src/test/resources/spoon/test/api/";
			Launcher.main(new String[] {
					"-i",
					duplicateEntry + File.pathSeparator + "./" + duplicateEntry,
					"-o", "target/spooned/apitest" });
		} catch (IllegalArgumentException e) { // from JDT
			fail();
		}
	}

	@Test
	public void testDuplicateFilePlusFolder() {
		// more complex case: a file is given, together with the enclosing folder
		try {
			Launcher.main(new String[] {
					"-i",
					"src/test/resources/spoon/test/api/" + File.pathSeparator
							+ "src/test/resources/spoon/test/api/Foo.java",
					"-o", "target/spooned/apitest" });
		} catch (IllegalArgumentException e) { // from JDT
			fail();
		}
	}

	@Test(expected = Exception.class)
	public void testNotValidInput() {
		String invalidEntry = "does/not/exists//Foo.java";
		Launcher.main(new String[] { "-i",
				invalidEntry,
				"-o",
				"target/spooned/apitest" });
	}

	@Test
	public void testAddProcessorMethodInSpoonAPI() {
		final SpoonAPI launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses");
		launcher.setSourceOutputDirectory("./target/spooned");
		final AwesomeProcessor processor = new AwesomeProcessor();
		launcher.addProcessor(processor);
		launcher.run();

		assertEquals(1, processor.getElements().size());
		final CtClass<Bar> actual = processor.getElements().get(0);
		assertEquals(2, actual.getMethods().size());
		assertNotNull(actual.getMethodsByName("prepareMojito").get(0));
		assertNotNull(actual.getMethodsByName("makeMojito").get(0));
	}

	@Test
	public void testOutputOfSpoon() {
		final File sourceOutput = new File("./target/spoon/test/output/");
		final SpoonAPI launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses");
		launcher.setSourceOutputDirectory(sourceOutput);
		launcher.run();

		assertTrue(sourceOutput.exists());
	}

	@Test
	public void testDestinationOfSpoon() {
		final File binaryOutput = new File("./target/spoon/test/binary/");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setShouldCompile(true);
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses");
		launcher.setSourceOutputDirectory("./target/spooned");
		launcher.setBinaryOutputDirectory(binaryOutput);
		launcher.run();

		assertTrue(binaryOutput.exists());
	}

	@Test
	public void testPrintNotAllSourcesWithFilter() {
		// contract: setOutputFilter can take an arbitrary filter
		final File target = new File("./target/print-not-all/default");
		final SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/main/java/spoon/template/");
		launcher.setSourceOutputDirectory(target);
		launcher.setOutputFilter(new AbstractFilter<CtType<?>>(CtType.class) {
			@Override
			public boolean matches(CtType<?> element) {
				return "spoon.template.Parameter".equals(element.getQualifiedName())
						|| "spoon.template.AbstractTemplate".equals(element.getQualifiedName());
			}
		});
		launcher.run();

		List<File> list = new ArrayList<>(FileUtils.listFiles(target, new String[] {"java"}, true));
		final List<String> filesName = list.stream().map(File::getName).sorted().collect(Collectors.<String>toList());

		assertEquals(2, filesName.size());
		assertEquals("AbstractTemplate.java", filesName.get(0));
		assertEquals("Parameter.java", filesName.get(1));
	}

	@Test
	public void testPrintNotAllSourcesWithNames() {
		// contract: setOutputFilter can take a list of fully-qualified classes to be pretty-printed
		final File target = new File("./target/print-not-all/array");
		final SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/main/java/spoon/template/");
		launcher.setSourceOutputDirectory(target);
		launcher.setOutputFilter("spoon.template.Parameter", "spoon.template.AbstractTemplate");
		launcher.run();

		List<File> list = new ArrayList<>(FileUtils.listFiles(target, new String[] {"java"}, true));
		final List<String> filesName = list.stream().map(File::getName).sorted().collect(Collectors.<String>toList());

		assertEquals(2, filesName.size());
		assertEquals("AbstractTemplate.java", filesName.get(0));
		assertEquals("Parameter.java", filesName.get(1));
	}

	@Test
	public void testPrintNotAllSourcesInCommandLine() {
		final File target = new File("./target/print-not-all/command");
		final SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/main/java", //
				"-o", "./target/print-not-all/command", //
				"-f", "spoon.Launcher:spoon.template.AbstractTemplate"
		});

		List<File> list = new ArrayList<>(FileUtils.listFiles(target, new String[] {"java"}, true));
		final List<String> filesName = list.stream().map(File::getName).sorted().collect(Collectors.<String>toList());

		assertEquals(2, filesName.size());
		assertEquals("AbstractTemplate.java", filesName.get(0));
		assertEquals("Launcher.java", filesName.get(1));
	}

	@Test
	public void testInvalidateCacheOfCompiler() {
		final Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
		spoon.setSourceOutputDirectory("./target/api");
		spoon.getEnvironment().setNoClasspath(true);
		spoon.run();

		assertTrue(spoon.getModelBuilder().compile());

		final CtClass<Bar> aClass = spoon.getFactory().Class().get(Bar.class);

		final CtMethod aMethod = spoon.getFactory().Core().createMethod();
		aMethod.setSimpleName("foo");
		aMethod.setType(spoon.getFactory().Type().BOOLEAN_PRIMITIVE);
		aMethod.setBody(spoon.getFactory().Core().createBlock());
		aClass.addMethod(aMethod);

		// contract: compilation errors are not silent
		try {
			spoon.getModelBuilder().compile();
			fail();
		} catch (SnippetCompilationError expected) {}


		aClass.removeMethod(aMethod);

		assertTrue(spoon.getModelBuilder().compile());
	}

	@Test
	public void testSetterInNodes() {
		// contract: Check that all setters of an object have a condition to check
		// that the new value is != null to avoid NPE when we set the parent.
		class SetterMethodWithoutCollectionsFilter extends TypeFilter<CtMethod<?>> {
			private final List<CtTypeReference<?>> collections = new ArrayList<>(4);

			SetterMethodWithoutCollectionsFilter(Factory factory) {
				super(CtMethod.class);
				for (Class<?> aCollectionClass : Arrays.asList(Collection.class, List.class, Map.class, Set.class)) {
					collections.add(factory.Type().createReference(aCollectionClass));
				}
			}

			@Override
			public boolean matches(CtMethod<?> element) {
				boolean isSetter = isSetterMethod(element);
				boolean isNotSubType = !isSubTypeOfCollection(element);
				// setter with unsettableProperty should not respect the contract, as well as derived properties
				boolean doesNotHaveUnsettableAnnotation = doesNotHaveUnsettableAnnotation(element);
				boolean isNotSetterForADerivedProperty = isNotSetterForADerivedProperty(element);
				boolean superMatch = super.matches(element);
				return isSetter && doesNotHaveUnsettableAnnotation && isNotSetterForADerivedProperty && isNotSubType && superMatch;
			}

			private boolean isNotSetterForADerivedProperty(CtMethod<?> method) {
				String methodName = method.getSimpleName();
				String getterName = methodName.replace("set", "get");

				if (getterName.equals(methodName)) {
					return false;
				}

				CtType<?> zeClass = (CtType) method.getParent();
				List<CtMethod<?>> getterMethods = zeClass.getMethodsByName(getterName);

				if (getterMethods.size() != 1) {
					return false;
				}
				CtMethod<?> getterMethod = getterMethods.get(0);

				return (getterMethod.getAnnotation(DerivedProperty.class) == null);
			}

			private boolean doesNotHaveUnsettableAnnotation(CtMethod<?> element) {
				return (element.getAnnotation(UnsettableProperty.class) == null);
			}

			private boolean isSubTypeOfCollection(CtMethod<?> element) {
				final List<CtParameter<?>> parameters = element.getParameters();
				if (parameters.size() != 1) {
					return false;
				}
				final CtTypeReference<?> type = parameters.get(0).getType();
				for (CtTypeReference<?> aCollectionRef : collections) {
					if (type.isSubtypeOf(aCollectionRef) || type.equals(aCollectionRef)) {
						return true;
					}
				}
				return false;
			}

			private boolean isSetterMethod(CtMethod<?> element) {
				final List<CtParameter<?>> parameters = element.getParameters();
				if (parameters.size() != 1) {
					return false;
				}
				final CtTypeReference<?> typeParameter = parameters.get(0).getType();
				final CtTypeReference<CtElement> ctElementRef = element.getFactory().Type().createReference(CtElement.class);

				// isSubtypeOf will return true in case of equality
				boolean isSubtypeof = typeParameter.isSubtypeOf(ctElementRef);
				if (!isSubtypeof) {
					return false;
				}
				return element.getSimpleName().startsWith("set") && element.getDeclaringType().getSimpleName().startsWith("Ct") && element.getBody() != null;
			}
		}
		class CheckNotNullToSetParentMatcher extends CtElementImpl {
			public TemplateParameter<CtVariableAccess<?>> _parameter_access_;

			public void matcher() {
				if (_parameter_access_.S() != null) {
					_parameter_access_.S().setParent(this);
				}
			}

			@Override
			@Local
			public void accept(CtVisitor visitor) {
			}
		}

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		// Implementations
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/test/java/" + this.getClass().getCanonicalName().replace(".", "/") + ".java");
		// Needed for #isSubTypeOf method.
		launcher.addInputResource("./src/main/java/spoon/reflect/");
		launcher.buildModel();

		// Template matcher.
		CtClass<CheckNotNullToSetParentMatcher> matcherCtClass = launcher.getFactory().Class().get(CheckNotNullToSetParentMatcher.class);
		CtIf templateRoot = matcherCtClass.getMethod("matcher").getBody().getStatement(0);

		final List<CtMethod<?>> setters = Query.getElements(launcher.getFactory(), new SetterMethodWithoutCollectionsFilter(launcher.getFactory()));
		assertTrue("Number of setters found null", !setters.isEmpty());

		for (CtStatement statement : setters.stream().map((Function<CtMethod<?>, CtStatement>) ctMethod -> ctMethod.getBody().getStatement(0)).collect(Collectors.toList())) {

			// First statement should be a condition to protect the setter of the parent.
			assertTrue("Check the method " + statement.getParent(CtMethod.class).getSignature() + " in the declaring class " + statement.getParent(CtType.class).getQualifiedName(), statement instanceof CtIf);
			CtIf ifCondition = (CtIf) statement;
			TemplateMatcher matcher = new TemplateMatcher(templateRoot);

			assertEquals("Check the number of if in method " + statement.getParent(CtMethod.class).getSignature() + " in the declaring class " + statement.getParent(CtType.class).getQualifiedName(), 1, matcher.find(ifCondition).size());
		}
	}

	@Test
	public void testOneLinerIntro() {
		// contract: spoon can be used with a single line of code with Launcher.parseClass
		CtClass<?> l = Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
		assertEquals("A", l.getSimpleName());
		assertEquals(1, l.getMethods().size());
		assertEquals("m", l.getMethodsByName("m").get(0).getSimpleName());
		assertEquals("System.out.println(\"yeah\")", l.getMethodsByName("m").get(0).getBody().getStatement(0).toString());
	}

	@Test
	public void testSourceClasspathDoesNotAcceptDotClass() {
		// contract: setSourceClassPath does not accept .class files
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
		launcher.setBinaryOutputDirectory("./target/spoon-setscp");
		launcher.getEnvironment().setShouldCompile(true);
		launcher.run();

		final Launcher launcher2 = new Launcher();
		try {
			launcher2.getEnvironment().setSourceClasspath(new String[] {"./target/spoon-setscp/spoon/test/api/testclasses/Bar.class"});
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof InvalidClassPathException);
			assertTrue(e.getMessage().contains(".class files are not accepted in source classpath."));
		}
	}

	@Test
	public void testOutputDestinationHandler() throws IOException {
		// contract: files are created in the directory determined by the output destination handler

		final File outputDest = Files.createTempDirectory("spoon").toFile();

		final OutputDestinationHandler outputDestinationHandler = new OutputDestinationHandler() {
			@Override
			public Path getOutputPath(CtModule module, CtPackage pack, CtType type) {
				String path = "";
				if (module != null) {
					path += module.getSimpleName() + "_";
				}
				if (pack != null) {
					path += pack.getQualifiedName() + "_";
				}
				if (type != null) {
					path += type.getSimpleName() + ".java";
				}
				return new File(outputDest, path).toPath();
			}

			@Override
			public File getDefaultOutputDirectory() {
				return outputDest;
			}
		};

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
		launcher.getEnvironment().setOutputDestinationHandler(outputDestinationHandler);
		launcher.run();

		File generatedFile = new File(outputDest, "unnamed module_spoon.test.api.testclasses_Bar.java");
		assertTrue(generatedFile.exists());
	}

	@Test
	public void testOutputDestinationHandlerWithCUFactory() throws IOException {
		// contract: when creating a new CU, its destination is consistent with output destination handler

		final File outputDest = Files.createTempDirectory("spoon").toFile();

		final OutputDestinationHandler outputDestinationHandler = new OutputDestinationHandler() {
			@Override
			public Path getOutputPath(CtModule module, CtPackage pack, CtType type) {
				String path = "";
				if (module != null) {
					path += module.getSimpleName() + "_";

					if (pack == null && type == null) {
						path += "module-info.java";
					}
				}
				if (pack != null) {
					path += pack.getQualifiedName() + "_";

					if (type == null) {
						path += "package-info.java";
					}
				}
				if (type != null) {
					path += type.getSimpleName() + ".java";
				}
				return new File(outputDest, path).toPath();
			}

			@Override
			public File getDefaultOutputDirectory() {
				return outputDest;
			}
		};

		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(9);
		launcher.getEnvironment().setOutputDestinationHandler(outputDestinationHandler);
		Factory factory = launcher.getFactory();

		CtModule module = factory.Module().getOrCreate("simplemodule");
		CompilationUnit cuModule = factory.CompilationUnit().getOrCreate(module);

		CtPackage ctPackage = factory.Package().getOrCreate("my.beautiful.pack");
		module.setRootPackage(factory.Package().get("my"));

		CtType ctType = factory.Class().create("my.beautiful.pack.SuperClass");

		CompilationUnit cuClass = factory.CompilationUnit().getOrCreate(ctType);
		CompilationUnit cuPackage = factory.CompilationUnit().getOrCreate(ctPackage);

		File moduleFile = new File(outputDest.getCanonicalPath(), "simplemodule_module-info.java");
		File packageFile = new File(outputDest.getCanonicalPath(), "simplemodule_my.beautiful.pack_package-info.java");
		File classFile = new File(outputDest.getCanonicalPath(), "simplemodule_my.beautiful.pack_SuperClass.java");

		assertEquals(moduleFile, cuModule.getFile());
		assertEquals(packageFile, cuPackage.getFile());
		assertEquals(classFile, cuClass.getFile());

		Set<String> units = launcher.getFactory().CompilationUnit().getMap().keySet();
		assertEquals(3, units.size());

		assertTrue("Module file not contained (" + moduleFile.getCanonicalPath() + "). \nContent: " + StringUtils.join(units, "\n"), units.contains(moduleFile.getCanonicalPath()));
		assertTrue("Package file not contained (" + packageFile.getCanonicalPath() + "). \nContent: " + StringUtils.join(units, "\n"), units.contains(packageFile.getCanonicalPath()));
		assertTrue("Class file not contained (" + classFile.getCanonicalPath() + "). \nContent: " + StringUtils.join(units, "\n"), units.contains(classFile.getCanonicalPath()));
	}

	@Test
	public void testOutputWithNoOutputProduceNoFolder() {
		// contract: when using "NO_OUTPUT" output type, no output folder shoud be created
		String destPath = "./target/nooutput_" + UUID.randomUUID().toString();
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
		launcher.setSourceOutputDirectory(destPath);
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.run();
		File outputDir = new File(destPath);
		System.out.println(destPath);
		assertFalse("Output dir should not exist: " + outputDir.getAbsolutePath(), outputDir.exists());
	}

	@Test
	public void testGetOneLinerMainClassFromCU() {
		// contract: when using Spoon with a snippet, we can still use properly CompilationUnit methods
		CtClass<?> l = Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
		CompilationUnit compilationUnit = l.getPosition().getCompilationUnit();

		assertNotNull(compilationUnit);
		CtType<?> mainType = compilationUnit.getMainType();
		assertSame(l, mainType);
	}

	@Test
	public void testLauncherDefaultValues() {
		// contract: check default value for classpath and comments in Launcher

		Launcher launcher = new Launcher();
		Environment environment = launcher.getEnvironment();

		assertTrue(environment.getNoClasspath());
		assertTrue(environment.isCommentsEnabled());
	}

	@Test
	public void testBuildModelReturnThatTheModelIsBuilt() {
		// contract: when a model is built, a flag is available in the environment to say it's built
		// and this flag won't change if something is modified in the model afterwards
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");

		assertNotNull(launcher.getFactory().getModel());
		assertFalse(launcher.getFactory().getModel().isBuildModelFinished());
		launcher.buildModel();
		assertTrue(launcher.getModel().isBuildModelFinished());

		launcher.getFactory().createClass("my.fake.Klass");
		assertTrue(launcher.getModel().isBuildModelFinished());
	}

	@Test
	public void testProcessModelsTwice() {
		// contract: the launcher cannot be processed twice
		Launcher launcher = new Launcher();
		launcher.setArgs(new String[]{"-i", "./src/test/java/spoon/test/api/testclasses/Bar.java"});

		try {
			launcher.setArgs(new String[] {"-i", "./src/test/java/spoon/test/arrays/testclasses/Foo.java"});
			fail();
		} catch (SpoonException e) {
			assertEquals("You cannot process twice the same launcher instance.", e.getMessage());
		}
	}

}
