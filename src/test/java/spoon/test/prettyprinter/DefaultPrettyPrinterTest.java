package spoon.test.prettyprinter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.JavaOutputProcessor;
import spoon.test.prettyprinter.testclasses.AClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class DefaultPrettyPrinterTest {
	private static final String nl = System.lineSeparator();

	@Test
	public void printerCanPrintInvocationWithoutException() throws Exception {
		String packageName = "spoon.test.subclass.prettyprinter";
		String className = "DefaultPrettyPrinterExample";
		String qualifiedName = packageName + "." + className;
		SpoonModelBuilder comp = new Launcher().createCompiler();
		List<SpoonResource> fileToBeSpooned = SpoonResourceHelper.resources("./src/test/resources/printer-test/" + qualifiedName.replace('.', '/') + ".java");
		assertEquals(1, fileToBeSpooned.size());
		comp.addInputSources(fileToBeSpooned);
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/printer-test/DefaultPrettyPrinterDependency.jar");
		assertEquals(1, classpath.size());
		comp.setSourceClasspath(classpath.get(0).getPath());
		comp.build();
		Factory factory = comp.getFactory();
		CtType<?> theClass = factory.Type().get(qualifiedName);
		List<CtInvocation<?>> elements = Query.getElements(theClass, new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		assertEquals(3, elements.size());
		CtInvocation<?> mathAbsInvocation = elements.get(1);
		assertEquals("java.lang.Math.abs(message.length())", mathAbsInvocation.toString());
	}

	@Test
	public void superInvocationWithEnclosingInstance() throws Exception {

		/**
		 * To extend a nested class an enclosing instance must be provided
		 * to call the super constructor.
		 */

		String sourcePath = "./src/test/resources/spoon/test/prettyprinter/NestedSuperCall.java";
		List<SpoonResource> files = SpoonResourceHelper.resources(sourcePath);
		assertEquals(1, files.size());

		SpoonModelBuilder comp = new Launcher().createCompiler();
		comp.addInputSources(files);
		comp.build();

		Factory factory = comp.getFactory();
		CtType<?> theClass = factory.Type().get("spoon.test.prettyprinter.NestedSuperCall");

		assertTrue(theClass.toString().contains("nc.super(\"a\")"));
	}

	@Test
	public void testPrintAClassWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final String expected =
			  "public class AClass {" +nl+
			  "    public List<?> aMethod() {" +nl+
			  "        return new ArrayList<>();" +nl+
			  "    }" +nl+
			  "" +nl+
			  "    public List<? extends ArrayList> aMethodWithGeneric() {" +nl+
			  "        return new ArrayList<>();" +nl+
			  "    }" +nl+
			  "}";
		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		assertEquals(expected, aClass.toString());

		final CtConstructorCall<?> constructorCall = aClass.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class)).get(0);

		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
																  .getActualTypeArguments()
																  .get(0);
		assertTrue(ctTypeReference.isImplicit());
		assertEquals("Object", ctTypeReference.getSimpleName());
	}

	@Test
	public void testPrintAMethodWithImports() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final String expected =
			  "public List<?> aMethod() {" +nl+
			  "    return new ArrayList<>();" +nl+
			  "}";

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		assertEquals(expected, aClass.getMethodsByName("aMethod").get(0).toString());

		final CtConstructorCall<?> constructorCall =
				aClass.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class))
					  .get(0);
		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
																  .getActualTypeArguments()
																  .get(0);
		assertTrue(ctTypeReference.isImplicit());
		assertEquals("Object", ctTypeReference.getSimpleName());
	}

	@Test
	public void testPrintAMethodWithGeneric() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		final String expected = "public List<? extends ArrayList> aMethodWithGeneric() {" + System.lineSeparator()
				+ "    return new ArrayList<>();" + System.lineSeparator()
				+ "}";
		assertEquals(expected, aClass.getMethodsByName("aMethodWithGeneric").get(0).toString());

		final CtConstructorCall<?> constructorCall =
				aClass.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class))
						.get(0);
		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
				.getActualTypeArguments()
				.get(0);
		assertTrue(ctTypeReference.isImplicit());
		assertEquals("Object", ctTypeReference.getSimpleName());
	}

	@Test
	public void testAutoImportWithAmbiguousClassWithEnums() throws IOException {
		String inputFile = "/spoon/test/prettyprinter/testclasses/TypeIdentifierCollision.java";
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.setSourceOutputDirectory("./target/spoon-import-ambiguous");
		launcher.addInputResource("./src/test/java"+inputFile);
		launcher.run();

		List<String> linesInput = Files.readAllLines(new File("./src/test/java"+inputFile).toPath());
		List<String> linesOutput = Files.readAllLines(new File("./target/spoon-import-ambiguous"+inputFile).toPath());
		assertEquals(StringUtils.join(linesInput, "\n").replace("\n",""), StringUtils.join(linesOutput, "\n").replace("\n",""));
	}

	@Test
	public void useFullyQualifiedNamesInCtElementImpl_toString() throws Exception {
		Factory factory = build( AClass.class );
		factory.getEnvironment().setAutoImports(false);

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get( AClass.class );
		String computed = aClass.getMethodsByName("aMethod").get(0).toString();
		final String expected =
			  "public java.util.List<?> aMethod() {" +nl+
			  "    return new java.util.ArrayList<>();" +nl+
			  "}"
		;
		assertEquals( "the toString method of CtElementImpl should not shorten type names as it has no context or import statements", expected, computed );
	}

	@Test
	public void printClassCreatedWithSpoon() throws Exception {

		/* test that spoon is able to print a class that it created without setting manually the output (default configuration) */

		final String nl = System.getProperty("line.separator");

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setSourceOutputDirectory(File.createTempFile("foo","").getParentFile());
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtClass<Object> ctClass = factory.Class().create("foo.Bar");
		PrettyPrinter pp = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		JavaOutputProcessor jop =
				new JavaOutputProcessor(pp);
		jop.setFactory(factory);

		jop.createJavaFile(ctClass);//JavaOutputProcessor is able to create the file even if we do not set the cu manually

		String pathname = System.getProperty("java.io.tmpdir") + "/foo/Bar.java";
		File javaFile = new File(pathname);
		assertTrue(javaFile.exists());

		assertEquals("package foo;" + nl + nl + nl + "class Bar {}" + nl + nl,
				IOUtils.toString(new FileInputStream(javaFile), "UTF-8"));
	}

	@Test
	public void importsFromMultipleTypesSupported() {
		// contract: when printing types without compilation units,
		// DJPP try to get CU from the given types and print imports accordingly
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/A.java");
		launcher.run();
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(launcher.getEnvironment());
		printer.calculate(null, Arrays.asList(
			launcher.getFactory().Class().get("spoon.test.prettyprinter.testclasses.A"),
			launcher.getFactory().Class().get("spoon.test.prettyprinter.testclasses.B")
		));
		assertTrue("Content: "+printer.getResult(), printer.getResult().contains("import java.util.ArrayList;"));
	}

	@Test
	public void testTernaryParenthesesOnLocalVariable() {
		// Spooning the code snippet
		Launcher launcher = new Launcher();
		CtCodeSnippetStatement snippet = launcher.getFactory().Code().createCodeSnippetStatement(
			"final int foo = (new Object() instanceof Object ? new Object().equals(null) : new Object().equals(new Object())) ? 0 : new Object().hashCode();");
		CtStatement compile = snippet.compile();
		// Pretty-printing the Spooned code snippet and compiling the resulting code.
		snippet = launcher.getFactory().Code().createCodeSnippetStatement(compile.toString());
		assertEquals(compile, snippet.compile());
	}

	@Test
	public void testIssue1501() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/orwall/PreferencesActivity.java");
		launcher.addInputResource("./src/test/resources/noclasspath/orwall/BackgroundProcess.java");
		launcher.setSourceOutputDirectory("./target/issue1501");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		assertFalse(launcher.getModel().getAllTypes().isEmpty());
	}

}
