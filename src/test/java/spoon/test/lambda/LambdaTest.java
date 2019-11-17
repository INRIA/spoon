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
package spoon.test.lambda;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.LambdaFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.imports.ImportTest;
import spoon.test.lambda.testclasses.Bar;
import spoon.test.lambda.testclasses.Foo;
import spoon.test.lambda.testclasses.Intersection;
import spoon.test.lambda.testclasses.Kuu;
import spoon.test.lambda.testclasses.LambdaRxJava;
import spoon.test.lambda.testclasses.Panini;
import spoon.test.lambda.testclasses.Tacos;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
	private CtType<LambdaRxJava> lambdaRxJava;
	private CtType<Intersection> intersection;
	private SpoonModelBuilder compiler;

	@Before
	public void setUp() {
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
		lambdaRxJava = factory.Type().get(LambdaRxJava.class);
		intersection = factory.Type().get(Intersection.class);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithoutParameter() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(0);

		assertTypedBy(Foo.Check.class, lambda.getType());
		assertParametersSizeIs(0, lambda.getParameters());
		assertHasExpressionBody(lambda);

		assertIsWellPrinted("((Check) (() -> false))", lambda);
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
				return "Strings".equals(element.getAccessedType().getSimpleName());
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
				return "localField".equals(name)
						|| "pathSeparator".equals(name)
						|| "fieldInSeparateInterface".equals(name)
						|| "fieldInClassBase".equals(name)
						|| "fieldInClass".equals(name)
						|| "fieldInInterfaceBase".equals(name)
						|| "fieldInInterface".equals(name)
						|| "iAmToLazyForAnotherFieldName".equals(name);
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
				return "DEFAULT_RATING".equals(element.getVariable().getSimpleName());
			}
		}).size());
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithoutTypeForParameter() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(1);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((Predicate<Person>) (( p) -> p.age > 10))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithMultiParameters() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(2);

		assertTypedBy(Foo.CheckPersons.class, lambda.getType());
		assertParametersSizeIs(2, lambda.getParameters());
		final CtParameter<?> parameter1 = lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter1);
		assertParameterIsNamedBy("p1", parameter1);
		final CtParameter<?> parameter2 = lambda.getParameters().get(1);
		assertParameterTypedBy(Foo.Person.class, parameter2);
		assertParameterIsNamedBy("p2", parameter2);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((CheckPersons) (( p1, p2) -> (p1.age - p2.age) > 0))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithParameterTyped() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(3);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((Predicate<Person>) ((Person p) -> p.age > 10))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithExpressionBodyAndWithMultiParametersTyped() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(4);

		assertTypedBy(Foo.CheckPersons.class, lambda.getType());
		assertParametersSizeIs(2, lambda.getParameters());
		final CtParameter<?> parameter1 = lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter1);
		assertParameterIsNamedBy("p1", parameter1);
		final CtParameter<?> parameter2 = lambda.getParameters().get(1);
		assertParameterTypedBy(Foo.Person.class, parameter2);
		assertParameterIsNamedBy("p2", parameter2);
		assertHasExpressionBody(lambda);

		assertIsWellPrinted(
				"((CheckPersons) ((Person p1,Person p2) -> (p1.age - p2.age) > 0))",
				lambda);
	}

	@Test
	public void testLambdaExpressionWithStatementBodyAndWithoutParameters() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(5);

		assertTypedBy(Foo.Check.class, lambda.getType());
		assertParametersSizeIs(0, lambda.getParameters());
		assertStatementBody(lambda);

		assertIsWellPrinted("((Check) (() -> {" + System.lineSeparator()
				+ "    System.err.println(\"\");" + System.lineSeparator()
				+ "    return false;" + System.lineSeparator()
				+ "}))", lambda);
	}

	@Test
	public void testLambdaExpressionWithStatementBodyAndWithParameter() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(6);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertStatementBody(lambda);

		assertIsWellPrinted(
				"((Predicate<Person>) (( p) -> {"
						+ System.lineSeparator()
						+ "    p.doSomething();" + System.lineSeparator()
						+ "    return p.age > 10;" + System.lineSeparator()
						+ "}))", lambda);
	}

	@Test
	public void testLambdaExpressionInIfConditional() {
		final CtLambda<?> lambda = getLambdaInFooByNumber(7);

		assertTypedBy(Predicate.class, lambda.getType());
		assertParametersSizeIs(1, lambda.getParameters());
		final CtParameter<?> parameter = lambda.getParameters().get(0);
		assertParameterTypedBy(Foo.Person.class, parameter);
		assertParameterIsNamedBy("p", parameter);
		assertHasExpressionBody(lambda);

		final CtMethod<?> method = foo.getElements(new NamedElementFilter<>(CtMethod.class,"m8")).get(0);
		final CtIf condition = method.getElements(new AbstractFilter<CtIf>(CtIf.class) {
			@Override
			public boolean matches(CtIf element) {
				return true;
			}
		}).get(0);
		final String expected =
				"if (((Predicate<Person>) (( p) -> p.age > 18)).test(new Person(10))) {"
						+ System.lineSeparator()
						+ "    System.err.println(\"Enjoy, you have more than 18.\");" + System
						.lineSeparator()
						+ "}";
		assertEquals("Condition must be well printed", expected, printByPrinter(condition));
	}

	@Test
	public void testCompileLambdaGeneratedBySpoon() {
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		canBeBuilt(new File("./target/spooned/spoon/test/lambda/testclasses/"), 8);
	}

	@Test
	public void testTypeParameterOfLambdaWithoutType() {
		final CtLambda<?> lambda1 = bar.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);
		assertEquals(1, lambda1.getParameters().size());
		final CtParameter<?> ctParameterFirstLambda = lambda1.getParameters().get(0);
		assertEquals("s", ctParameterFirstLambda.getSimpleName());
		assertTrue(ctParameterFirstLambda.getType().isImplicit());
		assertEquals("", ctParameterFirstLambda.getType().prettyprint());
		assertEquals("spoon.test.lambda.testclasses.Bar.SingleSubscriber<? super T>", ctParameterFirstLambda.getType().toString());
		assertEquals("SingleSubscriber", ctParameterFirstLambda.getType().getSimpleName());
	}
	@Test
    	public void testTypeParameterOfLambdaWithoutType2() {
		final CtLambda<?> lambda2 = bar.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(1);
		assertEquals(2, lambda2.getParameters().size());
		final CtParameter<?> ctParameterSecondLambda = lambda2.getParameters().get(0);
		assertEquals("v", ctParameterSecondLambda.getSimpleName());
		assertTrue(ctParameterSecondLambda.getType().isImplicit());
		assertEquals("", ctParameterSecondLambda.getType().toString());
		assertEquals("?", ctParameterSecondLambda.getType().getSimpleName());
	}

	@Test
	public void testTypeParameterWithImplicitArrayType() {
		final CtLambda<?> lambda = panini.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);

		assertEquals(1, lambda.getParameters().size());
		final CtParameter<?> ctParameter = lambda.getParameters().get(0);
		assertEquals("a", ctParameter.getSimpleName());
		assertTrue(ctParameter.getType().isImplicit());
		assertEquals("", ctParameter.getType().toString());
		assertEquals("Object[]", ctParameter.getType().getSimpleName());

		final CtArrayTypeReference typeParameter = (CtArrayTypeReference) ctParameter.getType();
		assertTrue(typeParameter.getComponentType().isImplicit());
		assertEquals("", typeParameter.getComponentType().prettyprint());
		assertEquals("java.lang.Object", typeParameter.getComponentType().toString());
		assertEquals("Object", typeParameter.getComponentType().getSimpleName());
	}

	@Test
	public void testLambdaWithPrimitiveParameter() {
		final CtLambda<?> lambda = tacos.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);

		assertEquals(2, lambda.getParameters().size());
		final CtParameter<?> firstParam = lambda.getParameters().get(0);
		assertEquals("rs", firstParam.getSimpleName());
		assertTrue(firstParam.getType().isImplicit());
		assertEquals("", firstParam.getType().prettyprint());
		assertEquals("java.sql.ResultSet", firstParam.getType().toString());
		assertEquals("ResultSet", firstParam.getType().getSimpleName());

		final CtParameter<?> secondParam = lambda.getParameters().get(1);
		assertEquals("i", secondParam.getSimpleName());
		assertTrue(secondParam.getType().isImplicit());
		assertEquals("", secondParam.getType().prettyprint());
		assertEquals("int", secondParam.getType().toString());
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
	
	@Test
	public void testEqualsLambdaParameterRef() {
		CtLambda<?> lambda = getLambdaInFooByNumber(8);
		CtParameter<?> param = lambda.getParameters().get(0);
		CtParameterReference paramRef1 = param.getReference();
		CtParameterReference paramRef2 = lambda.filterChildren(new TypeFilter<>(CtParameterReference.class)).first();
		assertTrue(paramRef1.equals(paramRef2));
	}

	@Test
	public void testLambdaMethod() {
		CtLambda<?> lambda = getLambdaInFooByNumber(8);
		CtMethod<?> method = lambda.getOverriddenMethod();
		CtTypeReference<?> iface = lambda.getType();
		assertEquals(Consumer.class.getName(), iface.getQualifiedName());
		assertEquals(iface.getTypeDeclaration().getMethodsByName("accept").get(0), method);
/* This assertion fails now		
		CtExecutableReference<?> lambdaRef = lambda.getReference();
		CtExecutableReference<?> methodRef = lambdaRef.getOverridingExecutable();
// because methodRef is null
		CtExecutable<?> method2 = methodRef.getDeclaration();
		assertEquals("The lambda.getMethod() != lambda.getReference().getOverridingExecutable().getDeclaration()", method, method2);
*/
	}

	@Test
	public void testGetOverriddenMethodWithFunction() {
		List<CtLambda<?>> allLambdas = lambdaRxJava.getElements(new TypeFilter<>(CtLambda.class));
		assertEquals(1, allLambdas.size());
		CtLambda<?> lambda = allLambdas.get(0);
		CtMethod<?> method = lambda.getOverriddenMethod();
		CtTypeReference<?> iface = lambda.getType();
		assertEquals(LambdaRxJava.NbpOperator.class.getName(), iface.getQualifiedName());
	}

	@Test
	public void testLambdaFilter() {
		//check constructor with CtInterface
		List<String> methodNames = foo.filterChildren(new LambdaFilter((CtInterface<?>) foo.getNestedType("CheckPerson"))).map((CtLambda l)->l.getParent(CtMethod.class).getSimpleName()).list();
		assertHasStrings(methodNames);
		//check constructor with CtTypeReference
		methodNames = foo.filterChildren(new LambdaFilter(foo.getNestedType("Check").getReference())).map((CtLambda l)->l.getParent(CtMethod.class).getSimpleName()).list();
		assertHasStrings(methodNames, "m", "m6");
		//check empty constructor and addImplementingInterface with Interface
		methodNames = foo.filterChildren(new LambdaFilter().addImplementingInterface((CtInterface<?>) foo.getNestedType("CheckPersons"))).map((CtLambda l)->l.getParent(CtMethod.class).getSimpleName()).list();
		assertHasStrings(methodNames, "m3", "m5");
		//check empty constructor and addImplementingInterface with CtTypeReference
		methodNames = foo.filterChildren(new LambdaFilter().addImplementingInterface(factory.createCtTypeReference(Predicate.class))).map((CtLambda l)->l.getParent(CtMethod.class).getSimpleName()).list();
		assertHasStrings(methodNames, "m2", "m4", "m7", "m8");
	}

	@Test
	public void testInterfaceWithObjectMethods() {
		// contract Lambda expression works on interfaces with methods inherited from java.lang.Object
		CtInterface<?> checkPersons = factory.Interface().get(Foo.CheckPersons.class);
		List<CtLambda<?>> lambdas = foo.filterChildren(new LambdaFilter(checkPersons)).list();
		assertEquals(2, lambdas.size());
		CtLambda<?> lambda = lambdas.get(0);
		assertEquals(2, lambda.getParameters().size());
		CtMethod<?> method = lambda.getOverriddenMethod();
		assertTrue(checkPersons.getMethods().contains(method));
		assertEquals("test", method.getSimpleName());
	}

	@Test
	public void testLambdaWithGenericExtendingMultipleInterfaces() {
		final CtLambda<?> lambda = intersection.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(0);
		assertEquals(1, lambda.getParameters().size());
		final CtParameter<?> parameter = lambda.getParameters().get(0);
		assertEquals("elt", parameter.getSimpleName());
		assertTrue(parameter.getType().isImplicit());
		assertEquals("", parameter.getType().toString());
		CtIntersectionTypeReference typeReference = (CtIntersectionTypeReference) parameter.getType();

		assertParameterIsNamedBy("elt", parameter);
		assertTrue(typeReference.getBounds().size() == 2);
		assertHasExpressionBody(lambda);
		assertIsWellPrinted("( elt) -> elt.test()", lambda);
	}

	private void assertHasStrings(List<String> methodNames, String... strs) {
		for (String str : strs) {
			assertTrue("List should contain "+str+" but it is missing.", methodNames.remove(str));
		}
		if(!methodNames.isEmpty()) {
			fail("List shouldn't contain "+methodNames);
		}
	}

	private void assertTypedBy(Class<?> expectedType, CtTypeReference<?> type) {
		assertSame("Lambda must be typed", expectedType, type.getActualClass());
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
		assertSame("Lambda has a parameter typed by", expectedType, parameter.getType().getActualClass());
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
		assertEquals("Lambda must be well printed", expected, printByPrinter(lambda));
	}
	
	private static String printByPrinter(CtElement element) {
		return ImportTest.printByPrinter(element);
	}

	// note that the lambda number in simple name depends on the classloader
	// Eclipse the name is one less than in Maven
	// hence w ehcnage the tests
	private CtLambda<?> getLambdaInFooByNumber(int number) {
		return foo.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class)).get(number);
	}

	@Test
	public void testGetDeclarationOnTypeParameterFromLambda() {
		List<CtTypeParameterReference> listCtTPR  = launcher.getModel().getElements(new TypeFilter<>(CtTypeParameterReference.class));

		for (CtTypeParameterReference typeParameterReference : listCtTPR) {
			if (!(typeParameterReference instanceof CtWildcardReference) && typeParameterReference.getDeclaration() == null) {
				System.err.println(typeParameterReference.getSimpleName()+" from parent "+typeParameterReference.getParent(CtClass.class).getPosition()+"  has null declaration");
				typeParameterReference.getDeclaration();
				fail();
			}
		}
	}
}
