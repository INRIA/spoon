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
package spoon.test.methodreference;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.methodreference.testclasses.AssertJ;
import spoon.test.methodreference.testclasses.Cloud;
import spoon.test.methodreference.testclasses.Foo;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class MethodReferenceTest {
	private static final String TEST_CLASS = "spoon.test.methodreference.testclasses.Foo.";
	private CtClass<?> foo;

	@Before
	public void setUp() {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		launcher.getEnvironment().setComplianceLevel(8);

		final SpoonModelBuilder compiler = launcher.createCompiler(factory);
		launcher.setSourceOutputDirectory("./target/spooned/");
		compiler.addInputSource(new File("./src/test/java/spoon/test/methodreference/testclasses/"));
		compiler.build();
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

		foo = (CtClass<?>) factory.Type().get(Foo.class);
	}

	@Test
	public void testReferenceToAStaticMethod() {
		final String methodReference = TEST_CLASS + "Person::compareByAge";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy(TEST_CLASS + "Person", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertExecutableNamedBy("compareByAge", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAnInstanceMethodOfAParticularObject() {
		final String methodReference = "myComparisonProvider::compareByName";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy("myComparisonProvider", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtVariableRead);
		assertExecutableNamedBy("compareByName", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAnInstanceMethodOfMultiParticularObject() {
		final String methodReference = "tarzan.phone::compareByNumbers";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy("tarzan.phone", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtFieldRead);
		assertExecutableNamedBy("compareByNumbers", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAnInstanceMethodOfAnArbitraryObjectOfAParticularType() {
		final String methodReference = "java.lang.String::compareToIgnoreCase";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Comparator.class, reference.getType());
		assertTargetedBy("java.lang.String", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertExecutableNamedBy("compareToIgnoreCase", reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAConstructor() {
		final String methodReference = TEST_CLASS + "Person::new";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Supplier.class, reference.getType());
		assertTargetedBy(TEST_CLASS + "Person", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertIsConstructorReference(reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAClassParametrizedConstructor() {
		final String methodReference = TEST_CLASS + "Type<java.lang.String>::new";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Supplier.class, reference.getType());
		assertTargetedBy(TEST_CLASS + "Type<java.lang.String>", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertIsConstructorReference(reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testReferenceToAJavaUtilClassConstructor() {
		final String methodReference = "java.util.HashSet<" + TEST_CLASS + "Person>::new";
		final CtExecutableReferenceExpression<?,?> reference = getCtExecutableReferenceExpression(methodReference);

		assertTypedBy(Supplier.class, reference.getType());
		assertTargetedBy("java.util.HashSet<" + TEST_CLASS + "Person>", reference.getTarget());
		assertTrue(reference.getTarget() instanceof CtTypeAccess);
		assertIsConstructorReference(reference.getExecutable());

		assertIsWellPrinted(methodReference, reference);
	}

	@Test
	public void testCompileMethodReferenceGeneratedBySpoon() {
		canBeBuilt(new File("./target/spooned/spoon/test/methodreference/testclasses/"), 8);
	}

	@Test
	public void testNoClasspathExecutableReferenceExpression() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/resources/executable-reference-expression/Bar.java", "-o", "./target/spooned"
		});
		final CtExecutableReferenceExpression<?, ?> element = Query
				.getElements(launcher.getFactory(), new TypeFilter<CtExecutableReferenceExpression<?, ?>>(CtExecutableReferenceExpression.class)).get(0);

		assertEquals("isInstance", element.getExecutable().getSimpleName());
		assertNotNull(element.getExecutable().getDeclaringType());
		assertEquals("Tacos", element.getExecutable().getDeclaringType().getSimpleName());
		assertEquals("elemType::isInstance", element.toString());
	}

	@Test
	public void testNoClasspathSuperExecutable() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/noclasspath/superclass/UnknownSuperClass.java");
		launcher.buildModel();
		final CtModel model = launcher.getModel();

		final CtTypeReference overrideRef = launcher.getFactory().
				Annotation().createReference(Override.class);

		// call `getSuperClass()` indirectly using `getOverridingExecutable()`

		// some consistency checks...
		assertEquals(1, model.getElements(
				new NamedElementFilter<>(CtMethod.class,"a")).size());
		assertEquals(1, model.getElements(
				new NamedElementFilter<>(CtMethod.class,"b")).size());
		assertEquals(1, model.getElements(
				new NamedElementFilter<>(CtMethod.class,"toString")).size());

		// get super method of a class not available in classpath
		final CtMethod bMethod = model.getElements(
				new NamedElementFilter<>(CtMethod.class,"b")).get(0);
		assertNotNull(bMethod.getAnnotation(overrideRef));
		assertNull(bMethod.getReference().getOverridingExecutable());

		// get super method of a class available in classpath (Object)
		final CtMethod toStringMethod = model.getElements(
				new NamedElementFilter<>(CtMethod.class,"toString")).get(0);
		assertNotNull(toStringMethod.getAnnotation(overrideRef));
		assertNotNull(toStringMethod.getReference().getOverridingExecutable());
	}
	
	@Test
	public void testGetGenericMethodFromReference() throws Exception {
		CtType<?> classCloud = ModelUtils.buildClass(Cloud.class);
		CtMethod<?> ctMethod = classCloud.getMethodsByName("method").get(0);
		CtExecutableReference<?> execRef = ctMethod.getReference();
		Method method = execRef.getActualMethod();
		assertNotNull(method);
		assertEquals("method", method.getName());

		CtClass<?> classSun = classCloud.getFactory().Class().get("spoon.test.methodreference.testclasses.Sun");
		CtExecutableReference<?> execRef2 = classSun.filterChildren(new TypeFilter<>(CtInvocation.class))
				.select(((CtInvocation i)-> "method".equals(i.getExecutable().getSimpleName())))
				.map((CtInvocation i)->i.getExecutable())
				.first();
		assertNotNull(execRef2);
		Method method2 = execRef2.getActualMethod();
		assertNotNull(method2);
		assertEquals("method", method2.getName());
	}
	
	@Test
	public void testGetGenericExecutableReference() throws Exception {
		CtType<?> classCloud = ModelUtils.buildClass(Cloud.class);
		List<CtMethod<?>> methods = classCloud.getMethodsByName("method");
		assertThat(methods.size(), is(3));

		int n = 0;
		for (CtMethod<?> method1 : classCloud.getMethodsByName("method")) {
			CtExecutableReference<?> execRef = method1.getReference();
			Method method = execRef.getActualMethod();
			assertNotNull(method);
			assertEquals("method", method.getName());
			List<CtParameter<?>> parameters = method1.getParameters();
			assertThat(parameters.size(), is(2));

			//check that we have found the method with correct parameters
			for (int i = 0; i < parameters.size(); i++) {
				CtTypeReference<?> paramTypeRef = parameters.get(i).getType();
				Class<?> paramClass = paramTypeRef.getTypeErasure().getActualClass();
				assertSame(paramClass, method.getParameterTypes()[i]);
				//
				CtType<?> paramType = paramTypeRef.getDeclaration();
				//contract: declaration of parameter type can be found
				assertNotNull(paramType);
				//contract: reference to found parameter type is equal to origin reference
				CtTypeReference otherParamTypeRef = paramType.getReference();
				assertEquals(paramTypeRef, otherParamTypeRef);
				//contract: reference to type can be still dereferred
				assertSame(paramType, paramType.getReference().getDeclaration());

				n++;
			}
			assertSame(method1, execRef.getDeclaration());
		}

		assertThat(n, is(2*3));
	}

	private void assertTypedBy(Class<?> expected, CtTypeReference<?> type) {
		assertSame("Method reference must be typed.", expected, type.getActualClass());
	}

	private void assertTargetedBy(String expected, CtExpression<?> target) {
		assertNotNull("Method reference must have a target expression.", target);
		assertEquals("Target reference correspond to the enclosing class.", expected, target.toString());
	}

	private void assertIsConstructorReference(CtExecutableReference<?> executable) {
		assertExecutableNamedBy("<init>", executable);
	}

	private void assertExecutableNamedBy(String expected, CtExecutableReference<?> executable) {
		assertNotNull("Method reference must reference an executable.", executable);
		assertEquals("Method reference must reference the right executable.", expected, executable.getSimpleName());
	}

	private void assertIsWellPrinted(String methodReference, CtExecutableReferenceExpression<?,?> reference) {
		assertEquals("Method reference must be well printed", methodReference, reference.toString());
	}

	private CtExecutableReferenceExpression<?,?> getCtExecutableReferenceExpression(final String methodReference) {
		return foo.getElements(new AbstractFilter<CtExecutableReferenceExpression<?,?>>(CtExecutableReferenceExpression.class) {
			@Override
			public boolean matches(CtExecutableReferenceExpression<?,?> element) {
				return (methodReference).equals(element.toString());
			}
		}).get(0);
	}

	@Test
	public void testReferenceBuilderWithComplexGenerics() throws Exception {
		CtType<?> classCloud = ModelUtils.buildClass(AssertJ.class);
		List<CtMethod<?>> methods = classCloud.getMethodsByName("assertThat");
		assertThat(methods.size(), is(1));

		CtMethod method1 = methods.get(0);

		CtExecutableReference<?> execRef = method1.getReference();
		Method method = execRef.getActualMethod();
		assertNotNull(method);
		assertEquals("assertThat", method.getName());
		List<CtParameter<?>> parameters = method1.getParameters();
		assertThat(parameters.size(), is(1));

		//check that we have found the method with correct parameters
		CtTypeReference<?> paramTypeRef = parameters.get(0).getType();
		Class<?> paramClass = paramTypeRef.getTypeErasure().getActualClass();
		assertSame(paramClass, method.getParameterTypes()[0]);

		assertSame(method1, execRef.getDeclaration());
	}
}
