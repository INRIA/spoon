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
package spoon.test.prettyprinter;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.JavaOutputProcessor;
import spoon.support.compiler.SpoonPom;
import spoon.test.imports.ImportTest;
import spoon.test.prettyprinter.testclasses.AClass;
import spoon.test.prettyprinter.testclasses.ClassUsingStaticMethod;
import spoon.testing.utils.LineSeparatorExtension;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static spoon.testing.utils.ModelUtils.build;

public class DefaultPrettyPrinterTest {
	private static final String nl = System.lineSeparator();

	@Test
	public void testPrintClassWithStaticImportOfMethod() {
		// contract: prettyprinter does not add static variables as imports after generating code
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/ClassWithStaticMethod.java"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/ClassUsingStaticMethod.java"));
		compiler.build();

		final String expected =
				"package spoon.test.prettyprinter.testclasses;" + nl +
				"import static spoon.test.prettyprinter.testclasses.ClassWithStaticMethod.findFirst;" + nl +
				"public class ClassUsingStaticMethod {" + nl +
				"    public void callFindFirst() {" + nl +
				"        findFirst();" + nl +
                "        new ClassWithStaticMethod().notStaticFindFirst();" + nl +
				"    }" + nl +
				"}" + nl;

		final CtClass<?> classUsingStaticMethod = (CtClass<?>) factory.Type().get(ClassUsingStaticMethod.class);
		final String printed = factory.getEnvironment().createPrettyPrinter().printTypes(classUsingStaticMethod);
		assertEquals(expected, printed);
	}

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
		List<CtInvocation<?>> elements = Query.getElements(theClass, new TypeFilter<>(CtInvocation.class));
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
	public void testPrintAClassWithImports() {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final String expected =
			"public class AClass {" + nl
			+ "    public List<?> aMethod() {" + nl
			+ "        return new ArrayList<>();" + nl
			+ "    }" + nl
			+ "" + nl
			+ "    public List<? extends ArrayList> aMethodWithGeneric() {" + nl
			+ "        return new ArrayList<>();" + nl
			+ "    }" + nl
			+ "}";

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		//TODO remove that after implicit is set correctly for these cases
		assertTrue(factory.getEnvironment().createPrettyPrinter().printTypes(aClass).contains(expected));

		assertEquals(expected, aClass.prettyprint());

		final CtConstructorCall<?> constructorCall = aClass.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class)).get(0);

		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
																.getActualTypeArguments()
																.get(0);
		assertTrue(ctTypeReference.isImplicit());
		assertEquals("Object", ctTypeReference.getSimpleName());
	}

	@Test
	public void testPrintAMethodWithImports() {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/"));
		compiler.build();

		final String expected =
			"public List<?> aMethod() {" + nl
			+ "    return new ArrayList<>();" + nl
			+ "}";

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		assertEquals(expected, printByPrinter(aClass.getMethodsByName("aMethod").get(0)));

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
	public void testPrintAMethodWithGeneric() {
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
		assertEquals(expected, printByPrinter(aClass.getMethodsByName("aMethodWithGeneric").get(0)));

		final CtConstructorCall<?> constructorCall =
				aClass.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class))
						.get(0);
		final CtTypeReference<?> ctTypeReference = constructorCall.getType()
				.getActualTypeArguments()
				.get(0);
		assertTrue(ctTypeReference.isImplicit());
		assertEquals("Object", ctTypeReference.getSimpleName());
	}

	private static String printByPrinter(CtElement element) {
		return ImportTest.printByPrinter(element);
	}

	@Test
	public void autoImportUsesFullyQualifiedNameWhenImportedNameAlreadyPresent() {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/sub/TypeIdentifierCollision.java"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/prettyprinter/testclasses/TypeIdentifierCollision.java"));
		compiler.build();

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(spoon.test.prettyprinter.testclasses.TypeIdentifierCollision.class);

		String expected =
			"public void setFieldUsingExternallyDefinedEnumWithSameNameAsLocal() {" + nl
			+ "    localField = spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1.ordinal();" + nl
			+ "}";

		String computed = aClass.getMethodsByName("setFieldUsingExternallyDefinedEnumWithSameNameAsLocal").get(0).toString();
		assertEquals(expected, computed, "We use FQN for E1");

		expected =
			"public void setFieldUsingLocallyDefinedEnum() {" + nl
			+ "    localField = ENUM.E1.ordinal();" + nl
			+ "}";

		computed = aClass.getMethodsByName("setFieldUsingLocallyDefinedEnum").get(0).prettyprint();
		assertEquals(expected, computed);

		expected =
			"public void setFieldOfClassWithSameNameAsTheCompilationUnitClass() {" + nl
			+ "    TypeIdentifierCollision.globalField = localField;" + nl
			+ "}";

		computed = aClass.getMethodsByName("setFieldOfClassWithSameNameAsTheCompilationUnitClass").get(0).toString();
		assertEquals(expected, computed, "The static field of an external type with the same identifier as the compilation unit is printed with FQN");

		expected =
			"public void referToTwoInnerClassesWithTheSameName() {" + nl
			+ "    ClassA.VAR0 = ClassA.getNum();" + nl
			+ "    Class1.ClassA.VAR1 = Class1.ClassA.getNum();" + nl
			+ "}";

		//Ensure the ClassA of Class0 takes precedence over an import statement for ClassA in Class1, and its identifier can be the short version.

		computed = aClass.getMethodsByName("referToTwoInnerClassesWithTheSameName").get(0).prettyprint();
		assertEquals(expected, computed, "where inner types have the same identifier only one may be shortened and the other should be fully qualified");

		expected =
			"public enum ENUM {" + nl + nl
			+ "    E1(spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.globalField, spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1);" + nl + nl
			+ "    final int NUM;" + nl + nl
			+ "    final Enum<?> e;" + nl + nl
			+ "    private ENUM(int num, Enum<?> e) {" + nl
			+ "        NUM = num;" + nl
			+ "        this.e = e;" + nl
			+ "    }" + nl
			+ "}";

		computed = aClass.getNestedType("ENUM").toString();
		assertEquals(expected, computed);
	}

	@Test
	public void useFullyQualifiedNamesInCtElementImpl_toString() throws Exception {
		Factory factory = build(AClass.class);
		factory.getEnvironment().setAutoImports(false);

		final CtClass<?> aClass = (CtClass<?>) factory.Type().get(AClass.class);
		String computed = aClass.getMethodsByName("aMethod").get(0).toString();
		final String expected =
			"public java.util.List<?> aMethod() {" + nl
			+ "    return new java.util.ArrayList<>();" + nl
			+ "}";
		assertEquals(expected, computed, "the toString method of CtElementImpl should not shorten type names as it has no context or import statements");
	}

	@Test
	public void printClassCreatedWithSpoon() throws Exception {

		/* test that spoon is able to print a class that it created without setting manually the output (default configuration) */

		final String nl = System.getProperty("line.separator");

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setSourceOutputDirectory(File.createTempFile("foo", "").getParentFile());
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtClass<Object> ctClass = factory.Class().create("foo.Bar");
		JavaOutputProcessor jop = launcher.createOutputWriter();
		jop.setFactory(factory);

		jop.createJavaFile(ctClass); //JavaOutputProcessor is able to create the file even if we do not set the cu manually

		String pathname = System.getProperty("java.io.tmpdir") + "/foo/Bar.java";
		File javaFile = new File(pathname);
		assertTrue(javaFile.exists());

		assertEquals("package foo;" + nl + "class Bar {}" + nl,
				Files.readString(javaFile.toPath(), StandardCharsets.UTF_8));
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

	@Test
	public void testIssue2130() {
		// contract: varargs parameters should always be CtArrayTypeReference

		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/LogService.java");
		launcher.setSourceOutputDirectory("./target/issue2130");
		launcher.getEnvironment().setComplianceLevel(8);
		CtModel model = launcher.buildModel();

		CtMethod<?> machin = model.getElements(new NamedElementFilter<>(CtMethod.class, "machin")).get(0);
		assertEquals("machin", machin.getSimpleName());

		List<CtParameter<?>> parameters = machin.getParameters();
		assertEquals(1, parameters.size());

		CtParameter<?> ctParameter = parameters.get(0);
		assertTrue(ctParameter.isVarArgs());
		assertTrue(ctParameter.getType() instanceof CtArrayTypeReference);

		launcher.prettyprint();
	}

	@Test
	public void testThisConstructorCall() throws Exception {
		// contract: the this(...) call of another constructor is printed well

		CtClass<?> type = (CtClass) ModelUtils.buildClass(spoon.test.prettyprinter.testclasses.ArrayRealVector.class);
		CtConstructor<?> constr = type.getConstructors().stream().filter(c -> c.getParameters().size() == 1).findFirst().get();
		assertEquals("this(v, true)", constr.getBody().getStatement(0).toString());
	}

	@Test
	public void testThisConstructorCall2() throws Exception {
		// contract: the this(...) call of another constructor is printed well
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/ArrayRealVector.java");
		CtModel model = launcher.buildModel();
		Factory f = launcher.getFactory();

		CtClass<?> type = (CtClass) f.Class().get("org.apache.commons.math4.linear.ArrayRealVector");
		{
			CtConstructor<?> constr = type.getConstructors().stream()
					.filter(c ->
						c.getParameters().size() == 1
						&& "ArrayRealVector".equals(c.getParameters().get(0).getType().getSimpleName())
					).findFirst().get();
			assertEquals("this(v, true)", constr.getBody().getStatement(0).toString());
		}
		{
			String printed = type.toString();
			Pattern re = Pattern.compile("public ArrayRealVector\\(org.apache.commons.math4.linear.ArrayRealVector v\\)[^\\{]*\\{\\s*([^;]*)");
			Matcher m = re.matcher(printed);
			assertTrue(m.find());
			assertEquals("this(v, true)", m.group(1));
		}
	}

	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testElseIf() {
		//contract: else if statements should be printed without break else and if
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/A6.java");
		launcher.getEnvironment().setAutoImports(true);
		CtModel model = launcher.buildModel();
		CtType a5 = model.getRootPackage().getType("A6");
		String result = a5.toStringWithImports();
		String expected = "public class A6 {\n" +
				"    public static void main(String[] args) {\n" +
				"        int a = 1;\n" +
				"        if (a == 1) {\n" +
				"        } else if (a == 2) {\n" +
				"        } else if (a == 3) {\n" +
				"        }\n" +
				"    }\n" +
				"}\n";
		assertEquals(expected, result);
	}

	/**
	 * This test parses Spoon sources (src/main/java) and pretty prints them in a temporary directory
	 * to check the compliance of the pretty printer to the set of checkstyle rules used by the Spoon repo.
	 * As the test takes a long time to run, it is only meant to detect exemples of violation that can, then, be
	 * used as unit test.
	 * Note that this test can be reused to check the compliance of any pretty printer with any set of styling rules.
	*/
	@Disabled // disabled as long as 1) it is too long 2) we don't implement a SpoonCompliantPrettyPrinter
	@Test
	public void testCheckstyleCompliance() throws IOException, XmlPullParserException {
		File tmpDir = new File("./target/tmp-checkstyle");
		if(tmpDir.exists()) {
			FileUtils.deleteDirectory(tmpDir);
		}

		//Build spoon AST and pretty print it in tmpDir
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java");
		launcher.setSourceOutputDirectory(tmpDir.getPath() + "/src/main/java");
		launcher.buildModel();
		launcher.prettyprint();

		//copy pom and modify relative path
		File originalPom = new File("pom.xml");
		File tmpPom = new File(tmpDir, "pom.xml");

		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		try (FileReader reader = new FileReader(originalPom)) {
			Model model = pomReader.read(reader);
			model.getParent().setRelativePath("../../" + model.getParent().getRelativePath());
			Plugin checkstyle = null;
			for(Plugin p : model.getBuild().getPlugins()) {
				if(p.getArtifactId().equals("maven-checkstyle-plugin")) {
					checkstyle = p;
					break;
				}
			}
			assertNotNull(checkstyle);
			Xpp3Dom config = (Xpp3Dom) checkstyle.getConfiguration();
			config.getChild("configLocation").setValue("../../checkstyle.xml");
			//config.setAttribute("configLocation", "../../checkstyle.xml");

			MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileOutputStream(tmpPom), model);

			//run checkstyle
			//contract: PrettyPrinted sources should not contain errors
			assertTrue(runCheckstyle(new File(SpoonPom.guessMavenHome()),tmpPom));

		} catch (FileNotFoundException e) {
			throw new IOException("Pom does not exists.");
		}

	}

	private boolean runCheckstyle(File mvnHome, File pomFile) {
		InvocationRequest request = new DefaultInvocationRequest();
		request.setBatchMode(true);
		request.setPomFile(pomFile);
		request.setGoals(Collections.singletonList("checkstyle:checkstyle"));

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(mvnHome);
		invoker.setWorkingDirectory(pomFile.getParentFile());

		Map<String, List<String>> errors = new HashMap<>();
		Pattern checkstylViolationPattern = Pattern.compile("\\[ERROR] [^\\s]* .* \\[[^\\s]*]");


		invoker.setOutputHandler(s -> {
			Matcher m = checkstylViolationPattern.matcher(s);
			//System.out.println("r: " + s);
			if(m.matches()) {
				String fileName = s.split(" ")[1];
				String violationName = "[" + s.split("\\[")[2];
				List<String> files = errors.computeIfAbsent(violationName, str -> new LinkedList<>());
				files.add(fileName);
			}
		});


		try {
			InvocationResult result = invoker.execute(request);


			System.err.println("Violations: ");
			for(String violationName: errors.keySet()) {
				System.err.println("V: " + violationName + " -> " + errors.get(violationName).size());
				System.err.println("   Ex: " +  errors.get(violationName).get(0));
			}

			return result.getExitCode() == 0;
		} catch (MavenInvocationException e) {
			return false;
		}
	}
}
