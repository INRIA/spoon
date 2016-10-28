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
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.FileCompiler;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompilationTest {

	@Test
	public void compileCommandLineTest() throws Exception {
		// the --compile option works, shouldCompile is set

		String sourceFile = "./src/test/resources/noclasspath/Simple.java";
		String compiledFile = "./spooned-classes/Simple.class";

		// ensuring clean state
		new File(compiledFile).delete();

		Launcher launcher = new Launcher();

		launcher.run(new String[] {
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

		final URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { outputBinDirectory.toURL() });

		Class<?> aClass = urlClassLoader.loadClass("Simple");
		Method m = aClass.getMethod("m");
		Assert.assertEquals(42, m.invoke(aClass.newInstance()));
	}

	@Test
	public void modifyCLassTest() throws Exception {
		// contract: one can modify classes that are both in the Java classpath and in the Spoon model
		// bug found with F. Fouquet during the Spirals seminar

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/compilation/Simple.java");
		launcher.buildModel();

		Factory factory = launcher.getFactory();
		CoreFactory core = factory.Core();
		CodeFactory code = factory.Code();

		CtClass simple = factory.Class().get("spoon.test.compilation.Simple");

		// adding a new method
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

		// checking the new behavior
		Class<?> aClass2 = simple.newInstance().getClass();
		Method m2 = aClass2.getMethod("m");
		Assert.assertEquals(42, m2.invoke(aClass2.newInstance()));

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
		for (int i=1; i<=10; i++) {
			body.getStatement(0).replace(factory.Code().createCodeSnippetStatement("int i = "+i+";"));
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
					protected FileCompiler createBatchCompiler(InputType... args) {
						return new FileCompiler(this) {
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
		for(CtType<?> t : launcher.getFactory().getModel().getAllTypes()) {

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
					protected FileCompiler createBatchCompiler(InputType... args) {
						return new FileCompiler(this) {
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
		for(CtType<?> t : launcher.getFactory().getModel().getAllTypes()) {
			assertTrue(t.getQualifiedName().contains("reference"));
		}

	}

}
