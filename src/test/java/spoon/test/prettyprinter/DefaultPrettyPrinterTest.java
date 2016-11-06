package spoon.test.prettyprinter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class DefaultPrettyPrinterTest {
	private static final String nl = System.lineSeparator();

	@Test
	public void printerCanPrintInvocationWithoutException() throws Exception {
		String packageName = "spoon.test.subclass.prettyprinter";
		String className = "DefaultPrettyPrinterExample";
		String qualifiedName = packageName + "." + className;
		SpoonCompiler comp = new Launcher().createCompiler();
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

		SpoonCompiler comp = new Launcher().createCompiler();
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
		final SpoonCompiler compiler = launcher.createCompiler();
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
		final SpoonCompiler compiler = launcher.createCompiler();
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
		final SpoonCompiler compiler = launcher.createCompiler();
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
	public void autoImportUsesFullyQualifiedNameWhenImportedNameAlreadyPresent() throws Exception {
		Factory factory = build( spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.class, spoon.test.prettyprinter.testclasses.TypeIdentifierCollision.class );
		factory.getEnvironment().setAutoImports(true);

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get( spoon.test.prettyprinter.testclasses.TypeIdentifierCollision.class );

		String expected =
			"public void setFieldUsingExternallyDefinedEnumWithSameNameAsLocal() {" +nl+
			"    localField = spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1.ordinal();" +nl+
			"}"
		;
		String computed = aClass.getMethodsByName("setFieldUsingExternallyDefinedEnumWithSameNameAsLocal").get(0).toString();
		assertEquals( "the externally defined enum should be fully qualified to avoid a name clash with the local enum of the same name", expected, computed );

		expected = //This is what is expected
			"public void setFieldUsingLocallyDefinedEnum() {" +nl+
			"    localField = ENUM.E1.ordinal();" +nl+
			"}"
		;
		expected = //This is correct however it could be more concise.
			"public void setFieldUsingLocallyDefinedEnum() {" +nl+
			"    localField = TypeIdentifierCollision.ENUM.E1.ordinal();" +nl+
			"}"
		;
		computed = aClass.getMethodsByName("setFieldUsingLocallyDefinedEnum").get(0).toString();
		assertEquals( expected, computed );

		expected =
			"public void setFieldOfClassWithSameNameAsTheCompilationUnitClass() {" +nl+
			"    TypeIdentifierCollision.globalField = localField;" +nl+
			"}"
		;
		computed = aClass.getMethodsByName("setFieldOfClassWithSameNameAsTheCompilationUnitClass").get(0).toString();
		assertEquals( "The static field of an external type with the same identifier as the compilation unit should be fully qualified", expected, computed );

		expected = //This is what is expected
				"public void referToTwoInnerClassesWithTheSameName() {" +nl+
				"    ClassA.VAR0 = ClassA.getNum();" +nl+
				"    Class1.ClassA.VAR1 = Class1.ClassA.getNum();" +nl+
				"}"
			;
		expected = //This is correct however it could be more concise.
			"public void referToTwoInnerClassesWithTheSameName() {" +nl+
			"    TypeIdentifierCollision.Class0.ClassA.VAR0 = TypeIdentifierCollision.Class0.ClassA.getNum();" +nl+
			"    TypeIdentifierCollision.Class1.ClassA.VAR1 = TypeIdentifierCollision.Class1.ClassA.getNum();" +nl+
			"}"
		;

		//Ensure the ClassA of Class0 takes precedence over an import statement for ClassA in Class1, and it's identifier can be the short version.

		computed = aClass.getMethodsByName("referToTwoInnerClassesWithTheSameName").get(0).toString();
		assertEquals( "where inner types have the same identifier only one may be shortened and the other should be fully qualified", expected, computed );

		expected =
			"public enum ENUM {" +nl+
			"E1(spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.globalField,spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1);" +nl+
			"    final int NUM;" +nl+nl+
			"    final Enum<?> e;" +nl+nl+
			"    private ENUM(int num, Enum<?> e) {" +nl+
			"        NUM = num;" +nl+
			"        this.e = e;" +nl+
			"    }" +nl+
			"}"
		;
		computed = aClass.getNestedType("ENUM").toString();
		assertEquals( "Parameters in an enum constructor should be fully typed when they refer to externally defined static field of a class with the same identifier as another locally defined type", expected, computed );
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
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtClass<Object> ctClass = factory.Class().create("foo.Bar");
		PrettyPrinter pp = new DefaultJavaPrettyPrinter(factory.getEnvironment());
		JavaOutputProcessor jop =
				new JavaOutputProcessor(File.createTempFile("foo","").getParentFile(),pp);
		jop.setFactory(factory);

		jop.createJavaFile(ctClass);//JavaOutputProcessor is able to create the file even if we do not set the cu manually

		String pathname = System.getProperty("java.io.tmpdir") + "/foo/Bar.java";
		File javaFile = new File(pathname);
		assertTrue(javaFile.exists());

		assertEquals(nl + nl + "package foo;" + nl + nl + nl + "class Bar {}" + nl + nl,
				IOUtils.toString(new FileInputStream(javaFile), "UTF-8"));
	}

	@Test
	public void importsFromMultipleTypesSupported() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/A.java");
		launcher.run();
		Environment env = launcher.getEnvironment();
		env.setAutoImports(true);
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env);
		printer.calculate(null, Arrays.asList(
			launcher.getFactory().Class().get("spoon.test.prettyprinter.testclasses.A"),
			launcher.getFactory().Class().get("spoon.test.prettyprinter.testclasses.B")
		));
		assertTrue(printer.getResult().contains("import java.util.ArrayList;"));
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
}
