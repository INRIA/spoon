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
package spoon.test.method;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.factory.Factory;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.method.testclasses.Hierarchy;
import spoon.test.method.testclasses.Tacos;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.test.delete.testclasses.Adobada;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.CtType;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.test.method.testclasses.Methods;
import org.junit.jupiter.api.Test;
import spoon.testing.utils.ModelTest;

import java.util.List;
import java.util.Set;
import java.util.ConcurrentModificationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static spoon.testing.utils.ModelUtils.buildClass;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodTest {

	@Test
	public void testClone() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);
		final CtMethod<?> m2 = adobada.getMethod("m2");

		CtMethod<?> clone = m2.clone();
		clone.setVisibility(ModifierKind.PRIVATE);

		assertEquals(ModifierKind.PUBLIC, m2.getModifiers().iterator().next());
	}

	@Test
	public void testSearchMethodWithGeneric() throws Exception {
		CtType<Tacos> aTacos = buildClass(Tacos.class);
		CtMethod<Object> method1 = aTacos.getMethod("method1", aTacos.getFactory().Type().integerType());
		assertEquals("public <T extends java.lang.Integer> void method1(T t) {" + System.lineSeparator() + "}", method1.toString());
		method1 = aTacos.getMethod("method1", aTacos.getFactory().Type().stringType());
		assertEquals("public <T extends java.lang.String> void method1(T t) {" + System.lineSeparator() + "}", method1.toString());
		method1 = aTacos.getMethod("method1", aTacos.getFactory().Type().objectType());
		assertEquals("public <T> void method1(T t) {" + System.lineSeparator() + "}", method1.toString());
	}

	@Test
	public void testMethodSignature() throws Exception {
		//contract: method signature contains type erasure of parameter types
		CtType<?> aTacos = buildClass(Methods.class);
		int methodCount = 0;
		for (CtMethod<?> method : aTacos.getMethods()) {
			String name = method.getSimpleName();
			String signatureParams;
			if (name.startsWith("object")) {
				signatureParams = "(java.lang.Object)";
			} else if (name.startsWith("string")) {
				signatureParams = "(java.lang.String)";
			} else if (name.startsWith("list")) {
				signatureParams = "(java.util.List)";
			} else {
				throw new AssertionError("Unexpected method " + name);
			}
			assertEquals(name + signatureParams, method.getSignature());
			methodCount++;
		}
		assertTrue(methodCount > 15);
	}

	@Test
	public void testAddSameMethodsTwoTimes() {
		final Factory factory = createFactory();
		final CtClass<Object> tacos = factory.Class().create("Tacos");
		final CtMethod<Void> method = factory.Method().create(tacos, new HashSet<>(), factory.Type().voidType(), "m", new ArrayList<>(), new HashSet<>());
		try {
			tacos.addMethod(method.clone());
		} catch (ConcurrentModificationException e) {
			fail();
		}
	}

	@Test
	public void testGetAllMethods() {
		/* getAllMethods must not throw Exception in no classpath mode */
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("src/test/resources/noclasspath/A3.java");
		l.buildModel();
		Set<CtMethod<?>> methods = l.getFactory().Class().get("A3").getAllMethods();
		assertEquals(1, methods.stream().filter(method -> "foo".equals(method.getSimpleName())).count());
	}

	@Test
	public void testGetAllMethodsAdaptingType() {
		// contract: AbstractTypingContext should not enter in recursive calls when resolving autoreferenced bounding type
		// such as T extends Comparable<? super T>
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("src/test/resources/noclasspath/spring/PropertyComparator.java");
		l.buildModel();

		CtType<?> propertyComparator = l.getModel().getElements(new NamedElementFilter<>(CtType.class, "PropertyComparator")).get(0);
		Set<CtMethod<?>> allMethods = propertyComparator.getAllMethods();

		boolean compareFound = false;
		for (CtMethod<?> method : allMethods) {
			if ("compare".equals(method.getSimpleName())) {
				assertEquals("compare(java.lang.Object,java.lang.Object)", method.getSignature());
				compareFound = true;
			}
		}

		assertTrue(compareFound);
	}

	@Test
	public void test_addParameterAt_addsParameterToSpecifiedPosition() {
		// contract: the parameter should be added at the specified position
		Factory factory = new Launcher().getFactory();

		CtMethod<?> method = factory.createMethod();

		CtParameter<String> first = factory.createParameter();
		CtTypeReference<String> firstType = factory.Type().stringType();
		first.setSimpleName("x");
		first.setType(firstType);

		CtParameter<Integer> second = factory.createParameter();
		CtTypeReference<Integer> secondType = factory.Type().integerType();
		second.setSimpleName("y");
		second.setType(secondType);

		CtParameter<Boolean> third = factory.createParameter();
		CtTypeReference<Boolean> thirdType = factory.Type().booleanType();
		third.setSimpleName("z");
		third.setType(thirdType);

		method.addParameterAt(0, second);
		method.addParameterAt(1, third);
		method.addParameterAt(0, first);

		assertThat(method.getParameters(), equalTo(Arrays.asList(first, second, third)));
	}

	@Test
	public void test_addParameterAt_throwsOutOfBoundsException_whenPositionIsOutOfBounds() {
		// contract: `addParameterAt` should throw an out of bounds exception when the specified position is out of
		// bounds of the parameter collection
		Factory factory = new Launcher().getFactory();
		CtMethod<?> method = factory.createMethod();
		CtParameter<?> paramater = factory.createParameter();

		assertThrows(IndexOutOfBoundsException.class,
				() -> method.addParameterAt(2, paramater));
	}

	@Test
	public void test_addFormalCtTypeParameterAt_addsTypeParameterToSpecifiedPosition() {
		// contract: addFormalCtTypeParameterAt should respect the position provided to it.

		// arrange
		Factory factory = new Launcher().getFactory();

		CtMethod<?> method = factory.createMethod();

		CtTypeParameter first = factory.createTypeParameter();
		first.setSimpleName("T");
		CtTypeParameter second = factory.createTypeParameter();
		second.setSimpleName("E");
		CtTypeParameter third = factory.createTypeParameter();
		third.setSimpleName("C");

		// act
		// add the type parameters out-of-order but in the correct positions
		method.addFormalCtTypeParameterAt(0, second);
		method.addFormalCtTypeParameterAt(0, first);
		method.addFormalCtTypeParameterAt(2, third);

		assertThat(method.getFormalCtTypeParameters(), equalTo(Arrays.asList(first, second, third)));
	}

	@Test
	public void test_addFormalCtTypeParameterAt_throwsOutOfBoundsException_whenPositionIsOutOfBounds() {
		// contract: addFormalCtTypeParameterAt should throw an out ouf bounds exception when the
		// specified position is out of bounds
		Factory factory = new Launcher().getFactory();
		CtMethod<?> method = factory.createMethod();
		CtTypeParameter typeParam = factory.createTypeParameter();

		assertThrows(IndexOutOfBoundsException.class,
				() -> method.addFormalCtTypeParameterAt(1, typeParam));
	}

	@ModelTest("src/test/java/spoon/test/method/testclasses/Hierarchy.java")
	void test_getTopDefinitions_findsTopOnly(Factory factory) {
		// contract: getTopDefinitions should find top-level definitions only
		CtMethod<?> method = factory.Interface().get(Hierarchy.D.class).getMethods().iterator().next();
		List<CtMethod<?>> topDefinitions = new ArrayList<>(method.getTopDefinitions());
		assertThat(topDefinitions.size(), equalTo(2));
		CtMethod<?> m0 = topDefinitions.get(0);
		CtMethod<?> m1 = topDefinitions.get(1);
		// two distinct elements
		assertThat(m0, not(sameInstance(m1)));
		// A1 and A2 declare the top-level methods
		assertThat(m0.getDeclaringType(), not(equalTo(m1.getDeclaringType())));
		assertThat(m0.getDeclaringType().getSimpleName(), anyOf(equalTo("A1"), equalTo("A2")));
		assertThat(m1.getDeclaringType().getSimpleName(), anyOf(equalTo("A1"), equalTo("A2")));
		// top-level definitions don't have top-level definitions
		assertThat(m0.getTopDefinitions().size(), equalTo(0));
		assertThat(m1.getTopDefinitions().size(), equalTo(0));
	}

	@ModelTest("src/test/resources/signature-polymorphic-methods/SignaturePolymorphicMethods.java")
	void testSignaturePolymorphicMethodInvocations(Factory factory) {
		// contract: calls to signature-polymorphic methods should be equal to their declaration signature
		CtType<?> type = factory.Type().get("SignaturePolymorphicMethods");
		Set<CtMethod<?>> methods = type.getMethods();
		assertThat(methods.size(), equalTo(4));
		for (CtMethod<?> method : methods) {
			// MethodHandle#invoke and MethodHandle#invokeExact have the declaration signature (Object[])Object
			CtInvocation<?> invocation = method.getBody().getElements(new TypeFilter<>(CtInvocation.class)).get(0);
			assertThat(invocation.getType(), equalTo(factory.Type().objectType()));
			List<CtTypeReference<?>> parameters = invocation.getExecutable().getParameters();
			assertThat(parameters.size(), equalTo(1));
			assertThat(parameters.get(0), equalTo(factory.Type().createArrayReference(factory.Type().objectType())));
		}
	}
}
