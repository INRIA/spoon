package spoon.test.lambda;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.test.lambda.testclasses.Foo;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LambdaTest {
	private Factory factory;
	private CtSimpleType<Foo> foo;

	@Before
	public void setUp() throws Exception {
		final Launcher launcher = new Launcher();
		this.factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		final SpoonCompiler compiler = launcher.createCompiler(this.factory);

		compiler.setDestinationDirectory(new File("./target/spooned/"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/lambda/testclasses/"));
		compiler.build();
		compiler.compileInputSources();
		foo = factory.Type().get(Foo.class);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithoutParameter() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$1");

		assertType(lambda, Foo.Check.class);
		assertParametersSize(0, lambda.getParameters());
		assertExpressionBody(lambda);

		assertPrintLambda(lambda, "((spoon.test.lambda.testclasses.Foo.Check)(() -> false))");
		System.err.println(foo.toString());
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithoutTypeForParameter() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$2");

		assertType(lambda, Predicate.class);
		assertParametersSize(1, lambda.getParameters());
		assertParameterTyped((CtParameter<?>) lambda.getParameters().get(0), Foo.Person.class, "p");
		assertExpressionBody(lambda);

		assertPrintLambda(lambda, "((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>)((spoon.test.lambda.testclasses.Foo.Person p) -> (p.age) > 10))");
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithMultiParameters() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$3");

		assertType(lambda, Foo.CheckPersons.class);
		assertParametersSize(2, lambda.getParameters());
		assertParameterTyped((CtParameter) lambda.getParameters().get(0), Foo.Person.class, "p1");
		assertParameterTyped((CtParameter) lambda.getParameters().get(1), Foo.Person.class, "p2");
		assertExpressionBody(lambda);

		assertPrintLambda(lambda,
				"((spoon.test.lambda.testclasses.Foo.CheckPersons)((spoon.test.lambda.testclasses.Foo.Person p1,spoon.test.lambda.testclasses.Foo.Person p2) -> ((p1.age) - (p2.age)) > 0))");
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithParameterTyped() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$4");

		assertType(lambda, Predicate.class);
		assertParametersSize(1, lambda.getParameters());
		assertParameterTyped((CtParameter) lambda.getParameters().get(0), Foo.Person.class, "p");
		assertExpressionBody(lambda);

		assertPrintLambda(lambda, "((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>)((spoon.test.lambda.testclasses.Foo.Person p) -> (p.age) > 10))");
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithMultiParametersTyped() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$5");

		assertType(lambda, Foo.CheckPersons.class);
		assertParametersSize(2, lambda.getParameters());
		assertParameterTyped((CtParameter) lambda.getParameters().get(0), Foo.Person.class, "p1");
		assertParameterTyped((CtParameter) lambda.getParameters().get(1), Foo.Person.class, "p2");
		assertExpressionBody(lambda);

		assertPrintLambda(lambda,
				"((spoon.test.lambda.testclasses.Foo.CheckPersons)((spoon.test.lambda.testclasses.Foo.Person p1,spoon.test.lambda.testclasses.Foo.Person p2) -> ((p1.age) - (p2.age)) > 0))");
	}

	@Test
	public void testLambdaExpressionWithStatementBodyAndWithoutParameters() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$6");

		assertType(lambda, Foo.Check.class);
		assertParametersSize(0, lambda.getParameters());
		assertStatementBody(lambda);

		assertPrintLambda(lambda, "((spoon.test.lambda.testclasses.Foo.Check)(() -> {\n"
				+ "    java.lang.System.err.println(\"\");\n"
				+ "    return false;\n"
				+ "}))");
	}

	@Test
	public void testLambdaExpressionWithStatementBodyAndWithParameter() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$7");

		assertType(lambda, Predicate.class);
		assertParametersSize(1, lambda.getParameters());
		assertParameterTyped((CtParameter) lambda.getParameters().get(0), Foo.Person.class, "p");
		assertStatementBody(lambda);

		assertPrintLambda(lambda, "((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>)((spoon.test.lambda.testclasses.Foo.Person p) -> {\n"
				+ "    p.doSomething();\n"
				+ "    return (p.age) > 10;\n"
				+ "}))");
	}

	@Test
	public void testLambdaExpressionInIfConditional() throws Exception {
		final CtLambda lambda = getLambdaByName("lambda$8");

		assertType(lambda, Predicate.class);
		assertParametersSize(1, lambda.getParameters());
		assertParameterTyped((CtParameter<?>) lambda.getParameters().get(0), Foo.Person.class, "p");
		assertExpressionBody(lambda);

		final CtMethod method = foo.getElements(new NameFilter<CtMethod>("m8")).get(0);
		final CtIf condition = method.getElements(new AbstractFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return true;
			}
		}).get(0);
		final String expected =
				"if (((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>)((spoon.test.lambda.testclasses.Foo.Person p) -> (p.age) > 18)).test(new spoon.test.lambda.testclasses.Foo.Person(10))) {\n"
						+ "    java.lang.System.err.println(\"Enjoy, you have more than 18.\");\n"
						+ "} ";
		assertEquals("Condition must be well printed", expected, condition.toString());
	}

	@Test
	public void testCompileLambdaGeneratedBySpoon() throws Exception {
		final File testDirectory = new File("./target/spooned/spoon/test/lambda/testclasses/");

		Launcher launcher = new Launcher();

		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		SpoonCompiler compiler = launcher.createCompiler(factory);

		compiler.addInputSource(testDirectory);
		try {
			assertTrue(compiler.build());
		} catch (Exception e) {
			fail();
		}
	}

	private void assertParameterTyped(CtParameter parameter, Class<Foo.Person> expectedType, String name) {
		assertNotNull("Lambda has a parameter typed", parameter.getType());
		assertEquals("Lambda has a parameter typed by", expectedType, parameter.getType().getActualClass());
		assertNameParameter(parameter, name);
	}

	private void assertNameParameter(CtParameter parameter, String name) {
		assertEquals("Lambda has a parameter with a name", name, parameter.getSimpleName());
	}

	private void assertPrintLambda(CtLambda lambda, String expected) {
		assertEquals("Lambda must be well printed", expected, lambda.toString());
	}

	private void assertParametersSize(int nbParameters, List<CtParameter<?>> parameters) {
		if (nbParameters == 0) {
			assertEquals("Lambda hasn't parameters", nbParameters, parameters.size());
		} else {
			assertEquals("Lambda has parameters", nbParameters, parameters.size());
		}
	}

	private void assertExpressionBody(CtLambda lambda) {
		assertNotNull("Lambda has an expression for its body.", lambda.getExpression());
		assertNull("Lambda don't have a list of statements (body) for its body", lambda.getBody());
	}

	private void assertStatementBody(CtLambda lambda) {
		assertNotNull("Lambda has a body with statements.", lambda.getBody());
		assertNull("Lambda don't have an expression for its body", lambda.getExpression());
	}

	private void assertType(CtLambda lambda, Class<?> expectedType) {
		assertEquals("Lambda must be typed", expectedType, lambda.getType().getActualClass());
	}

	private CtLambda getLambdaByName(String name) {
		return foo.getElements(new NameFilter<CtLambda>(name)).get(0);
	}
}
