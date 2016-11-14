package spoon.test.lambda;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.lambda.testclasses.Bar;
import spoon.test.lambda.testclasses.Foo;
import spoon.test.lambda.testclasses.Kuu;
import spoon.test.lambda.testclasses.Panini;
import spoon.test.lambda.testclasses.Tacos;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class LambdaTest {
	private Launcher launcher;
	private Factory factory;
	private CtType<Foo> foo;
	private CtType<Bar> bar;
	private CtType<Object> panini;
	private CtType<Object> tacos;
	private SpoonCompiler compiler;

	@Before
	public void setUp() throws Exception {
		launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		this.factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(8);
		compiler = launcher.createCompiler(this.factory);

		compiler.addInputSource(new File("./src/test/java/spoon/test/lambda/testclasses/"));
		compiler.build();

		foo = factory.Type().get(Foo.class);
		bar = factory.Type().get(Bar.class);
		panini = factory.Type().get(Panini.class);
		tacos = factory.Type().get(Tacos.class);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithoutParameter() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(0);

		assertTypedBy(Foo.Check.class, lambda.getType());
		assertParametersSizeIs(0, lambda.getParameters());
		assertHasExpressionBody(lambda);

		assertIsWellPrinted("((spoon.test.lambda.testclasses.Foo.Check) (() -> false))", lambda);
	}

	@Test
	public void testTypeAccessInLambdaNoClassPath() {
		final Launcher runLaunch = new Launcher();
		runLaunch.getEnvironment().setNoClasspath(true);
		runLaunch.addInputResource("./src/test/resources/noclasspath/lambdas/TypeAccessInLambda.java");
		runLaunch.buildModel();

		assertEquals("The token 'Strings' has not been parsed as CtTypeAccess", 1,
				runLaunch.getModel().getElements(new Filter<CtTypeAccess>() {
			@Override
			public boolean matches(final CtTypeAccess element) {
				return element.getAccessedType().getSimpleName().equals("Strings");
			}
		}).size());
	}

	@Test
	public void testFieldAccessInLambdaNoClassPath() {
		final Launcher runLaunch = new Launcher();
		runLaunch.getEnvironment().setNoClasspath(true);
		runLaunch.addInputResource("./src/test/resources/noclasspath/lambdas/FieldAccessInLambda.java");
		runLaunch.addInputResource("./src/test/resources/noclasspath/lambdas/imported/SeparateInterfaceWithField.java");
		runLaunch.buildModel();

		final List<CtFieldAccess> fieldAccesses =
				runLaunch.getModel().getElements(new Filter<CtFieldAccess>() {
			@Override
			public boolean matches(final CtFieldAccess element) {
				final String name = element.getVariable().getSimpleName();
				return name.equals("localField")
						|| name.equals("pathSeparator")
						|| name.equals("fieldInSeparateInterface")
						|| name.equals("fieldInClassBase")
						|| name.equals("fieldInClass")
						|| name.equals("fieldInInterfaceBase")
						|| name.equals("fieldInInterface")
						|| name.equals("iAmToLazyForAnotherFieldName");
			}
		});
		assertEquals(8, fieldAccesses.size());
	}

	@Test
	public void testFieldAccessInLambdaNoClassPathExternal1Example() {
		final Launcher runLaunch = new Launcher();
		runLaunch.getEnvironment().setNoClasspath(true);
		runLaunch.addInputResource("./src/test/resources/noclasspath/lambdas/external1");
		runLaunch.buildModel();

		assertEquals(3, runLaunch.getModel().getElements(new Filter<CtFieldAccess>() {
			@Override
			public boolean matches(final CtFieldAccess element) {
				return element.getVariable().getSimpleName().equals("DEFAULT_RATING");
			}
		}).size());
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithoutTypeForParameter() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(1);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = (CtParameter<?>) lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>) (( p) -> (p.age) > 10))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithMultiParameters() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(2);

		assertTypedBy(Foo.CheckPersons.class, lambda.getType());
		assertParametersSizeIs(2, lambda.getParameters());
		final CtParameter<?> parameter1 = (CtParameter<?>) lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter1);
		assertParameterIsNamedBy("p1", parameter1);
		final CtParameter<?> parameter2 = (CtParameter<?>) lambda.getParameters().get(1);
		assertParameterTypedBy(Foo.Person.class, parameter2);
		assertParameterIsNamedBy("p2", parameter2);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((spoon.test.lambda.testclasses.Foo.CheckPersons) (( p1, p2) -> ((p1.age) - (p2.age)) > 0))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithParameterTyped() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(3);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = (CtParameter<?>) lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>) ((spoon.test.lambda.testclasses.Foo.Person p) -> (p.age) > 10))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithMultiParametersTyped() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(4);

		assertTypedBy(Foo.CheckPersons.class, lambda.getType());
		assertParametersSizeIs(2, lambda.getParameters());
		final CtParameter<?> parameter1 = (CtParameter<?>) lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter1);
		assertParameterIsNamedBy("p1", parameter1);
		final CtParameter<?> parameter2 = (CtParameter<?>) lambda.getParameters().get(1);
		assertParameterTypedBy(Foo.Person.class, parameter2);
		assertParameterIsNamedBy("p2", parameter2);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((spoon.test.lambda.testclasses.Foo.CheckPersons) ((spoon.test.lambda.testclasses.Foo.Person p1,spoon.test.lambda.testclasses.Foo.Person p2) -> ((p1.age) - (p2.age)) > 0))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithStatementBodyAndWithoutParameters() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(5);

		assertTypedBy(Foo.Check.class, lambda.getType());
		assertParametersSizeIs(0, lambda.getParameters());
		assertStatementBody(lambda);

		assertIsWellPrinted("((spoon.test.lambda.testclasses.Foo.Check) (() -> {" + System.lineSeparator()
				+ "    java.lang.System.err.println(\"\");" + System.lineSeparator()
				+ "    return false;" + System.lineSeparator()
				+ "}))", lambda);
	}

	@Test
	public void testLambdaExpressionWithStatementBodyAndWithParameter() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(6);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = (CtParameter<?>) lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertStatementBody(lambda);

		assertIsWellPrinted(
				"((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>) (( p) -> {"
						+ System.lineSeparator()
						+ "    p.doSomething();" + System.lineSeparator()
						+ "    return (p.age) > 10;" + System.lineSeparator()
						+ "}))", lambda);
	}

	@Test
	public void testLambdaExpressionInIfConditional() throws Exception {
		final CtLambda<?> lambda = getLambdaInFooByNumber(7);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = (CtParameter<?>) lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertHasExpressionBody(lambda);

		final CtMethod<?> method = foo.getElements(new NameFilter<CtMethod<?>>("m8")).get(0);
		final CtIf condition = method.getElements(new AbstractFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return true;
			}
		}).get(0);
		final String expected =
				"if (((java.util.function.Predicate<spoon.test.lambda.testclasses.Foo.Person>) (( p) -> (p.age) > 18)).test(new spoon.test.lambda.testclasses.Foo.Person(10))) {"
						+ System.lineSeparator()
						+ "    java.lang.System.err.println(\"Enjoy, you have more than 18.\");" + System
						.lineSeparator()
						+ "}";
		assertEquals("Condition must be well printed", expected, condition.toString());
	}

	@Test
	public void testCompileLambdaGeneratedBySpoon() throws Exception {
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		canBeBuilt(new File("./target/spooned/spoon/test/lambda/testclasses/"), 8);
	}

	@Test
	public void testTypeParameterOfLambdaWithoutType() throws Exception {
		final CtLambda<?> lambda1 = bar.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);
		assertEquals(1, lambda1.getParameters().size());
		final CtParameter<?> ctParameterFirstLambda = lambda1.getParameters().get(0);
		assertEquals("s", ctParameterFirstLambda.getSimpleName());
		assertTrue(ctParameterFirstLambda.getType().isImplicit());
		assertEquals("", ctParameterFirstLambda.getType().toString());
		assertEquals("SingleSubscriber", ctParameterFirstLambda.getType().getSimpleName());
	}
	@Test
    	public void testTypeParameterOfLambdaWithoutType2() throws Exception {
		final CtLambda<?> lambda2 = bar.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(1);
		assertEquals(2, lambda2.getParameters().size());
		final CtParameter<?> ctParameterSecondLambda = lambda2.getParameters().get(0);
		assertEquals("v", ctParameterSecondLambda.getSimpleName());
		assertTrue(ctParameterSecondLambda.getType().isImplicit());
		assertEquals("", ctParameterSecondLambda.getType().toString());
		assertEquals("?", ctParameterSecondLambda.getType().getSimpleName());
	}

	@Test
	public void testTypeParameterWithImplicitArrayType() throws Exception {
		final CtLambda<?> lambda = panini.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);

		assertEquals(1, lambda.getParameters().size());
		final CtParameter<?> ctParameter = lambda.getParameters().get(0);
		assertEquals("a", ctParameter.getSimpleName());
		assertTrue(ctParameter.getType().isImplicit());
		assertEquals("", ctParameter.getType().toString());
		assertEquals("Array", ctParameter.getType().getSimpleName());

		final CtArrayTypeReference typeParameter = (CtArrayTypeReference) ctParameter.getType();
		assertTrue(typeParameter.getComponentType().isImplicit());
		assertEquals("", typeParameter.getComponentType().toString());
		assertEquals("Object", typeParameter.getComponentType().getSimpleName());
	}

	@Test
	public void testLambdaWithPrimitiveParameter() throws Exception {
		final CtLambda<?> lambda = tacos.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);

		assertEquals(2, lambda.getParameters().size());
		final CtParameter<?> firstParam = lambda.getParameters().get(0);
		assertEquals("rs", firstParam.getSimpleName());
		assertTrue(firstParam.getType().isImplicit());
		assertEquals("", firstParam.getType().toString());
		assertEquals("ResultSet", firstParam.getType().getSimpleName());

		final CtParameter<?> secondParam = lambda.getParameters().get(1);
		assertEquals("i", secondParam.getSimpleName());
		assertTrue(secondParam.getType().isImplicit());
		assertEquals("", secondParam.getType().toString());
		assertEquals("int", secondParam.getType().getSimpleName());
	}

	@Test
	public void testBuildExecutableReferenceFromLambda() throws Exception {
		final CtType<Kuu> aType = ModelUtils.buildClass(Kuu.class);
		final CtLambda<?> aLambda = aType.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);

		List<? extends CtParameterReference<?>> collect = null;
		try {
			collect = aLambda.getParameters().stream().map(CtParameter::getReference).collect(Collectors.toList());
		} catch (ClassCastException e) {
			fail();
		}

		assertNotNull(collect);
		assertEquals(1, collect.size());
	}

	private void assertTypedBy(Class<?> expectedType, CtTypeReference<?> type) {
		assertEquals("Lambda must be typed", expectedType, type.getActualClass());
	}

	private void assertParametersSizeIs(int nbParameters, List<CtParameter<?>> parameters) {
		if (nbParameters == 0) {
			assertEquals("Lambda hasn't parameters", nbParameters, parameters.size());
		} else {
			assertEquals("Lambda has parameters", nbParameters, parameters.size());
		}
	}

	private void assertParameterTypedBy(Class<?> expectedType, CtParameter<?> parameter) {
		assertNotNull("Lambda has a parameter typed", parameter.getType());
		assertEquals("Lambda has a parameter typed by", expectedType, parameter.getType().getActualClass());
	}

	private void assertHasExpressionBody(CtLambda<?> lambda) {
		assertNotNull("Lambda has an expression for its body.", lambda.getExpression());
		assertNull("Lambda don't have a list of statements (body) for its body", lambda.getBody());
	}

	private void assertStatementBody(CtLambda<?> lambda) {
		assertNotNull("Lambda has a body with statements.", lambda.getBody());
		assertNull("Lambda don't have an expression for its body", lambda.getExpression());
	}

	private void assertParameterIsNamedBy(String name, CtParameter<?> parameter) {
		assertEquals("Lambda has a parameter with a name", name, parameter.getSimpleName());
	}

	private void assertIsWellPrinted(String expected, CtLambda<?> lambda) {
		assertEquals("Lambda must be well printed", expected, lambda.toString());
	}

	// note that the lambda number in simple name depends on the classloader
	// Eclipse the name is one less than in Maven
	// hence w ehcnage the tests
	private CtLambda<?> getLambdaInFooByNumber(int number) {
		return foo.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(number);
	}
}
