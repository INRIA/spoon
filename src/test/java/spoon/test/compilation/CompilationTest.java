package spoon.test.compilation;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.compiler.jdt.JDTBatchCompiler;
import spoon.support.SpoonClassNotFoundException;
import spoon.test.compilation.testclasses.Bar;
import spoon.test.compilation.testclasses.IBar;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CompilationTest {

	@Test
	public void compileCommandLineTest() throws Exception {
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

		assertEquals(true, launcher.getEnvironment().shouldCompile());

		assertEquals(true, new File(compiledFile).exists());
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
		Assert.assertEquals(42, m.invoke(aClass.newInstance()));
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
		} catch (Exception ignore) {}

	}

	@Test
	public void testNewInstance() throws Exception {
		// contract: a ctclass can be instantiated, and each modification results in a new valid object
		Factory factory = new Launcher().getFactory();
		CtClass<Ifoo> c = factory.Code().createCodeSnippetStatement(
				"class X implements spoon.test.compilation.Ifoo { public int foo() {int i=0; return i;} }").compile();
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
	public void testFilterResourcesFile() throws Exception {
		// shows how to filter input java files, for https://github.com/INRIA/spoon/issues/877
		Launcher launcher = new Launcher() {
			@Override
			public SpoonCompiler createCompiler() {
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

		launcher.addInputResource("./src/test/java");
		launcher.buildModel();

		// we indeed only have types declared in a file called *Foo*
		for (CtType<?> t : launcher.getFactory().getModel().getAllTypes()) {

			assertTrue(t.getPosition().getFile().getAbsolutePath().contains("Foo"));
		}

	}

	@Test
	public void testFilterResourcesDir() throws Exception {
		// shows how to filter input java dir
		// only in package called "reference"
		Launcher launcher = new Launcher() {
			@Override
			public SpoonCompiler createCompiler() {
				return new JDTBasedSpoonCompiler(getFactory()) {
					@Override
					protected JDTBatchCompiler createBatchCompiler() {
						return new JDTBatchCompiler(this) {
							@Override
							public CompilationUnit[] getCompilationUnits() {
								List<CompilationUnit> units = new ArrayList<>();
								for (CompilationUnit u : super.getCompilationUnits()) {
									if (new String(u.getFileName()).contains("/reference/")) {
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

		launcher.addInputResource("./src/test/java");
		launcher.buildModel();

		// we indeed only have types declared in a file in package reference
		for (CtType<?> t : launcher.getFactory().getModel().getAllTypes()) {
			assertTrue(t.getQualifiedName().contains("reference"));
		}

	}

	@Test
	public void testPrecompile() {
		// without precompile
		Launcher l = new Launcher();
		l.setArgs(new String[] {"--noclasspath", "-i", "src/test/resources/compilation/"});
		l.buildModel();
		CtClass klass = l.getFactory().Class().get("compilation.Bar");
		// without precompile, actualClass does not exist (an exception is thrown)
		try {
			klass.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass();
			fail();
		} catch (SpoonClassNotFoundException ignore) {}

		// with precompile
		Launcher l2 = new Launcher();
		l2.setArgs(new String[] {"--precompile", "--noclasspath", "-i", "src/test/resources/compilation/"});
		l2.buildModel();
		CtClass klass2 = l2.getFactory().Class().get("compilation.Bar");
		// with precompile, actualClass is not null
		Class actualClass = klass2.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass();
		assertNotNull(actualClass);
		assertEquals("IBar", actualClass.getSimpleName());

		// precompile can be used to compile processors on the fly
		Launcher l3 = new Launcher();
		l3.setArgs(new String[] {"--precompile", "--noclasspath", "-i", "src/test/resources/compilation/", "-p", "compilation.SimpleProcessor"});
		l3.run();
	}


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
	public void testExoticClassLoader() throws Exception {
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
				} catch (SpoonClassNotFoundException ignore) {}
			}
		});

		assertEquals(3, l.size());
		assertTrue(l.contains("KJHKY"));
	}
}
