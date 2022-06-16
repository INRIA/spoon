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
package spoon.test.constructorcallnewclass;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.DeepRepresentationComparator;
import spoon.test.constructorcallnewclass.testclasses.Foo;
import spoon.test.constructorcallnewclass.testclasses.Panini;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstructorCallTest {
	private List<CtConstructorCall<?>> constructorCalls;
	private List<CtConstructorCall<?>> constructorCallsPanini;

	@BeforeEach
	public void setUp() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/" + Foo.class.getCanonicalName().replace(".", "/") + ".java");
		launcher.addInputResource("./src/test/java/" + Panini.class.getCanonicalName().replace(".", "/") + ".java");
		launcher.setSourceOutputDirectory("./target/spooned");
		launcher.run();
		final Factory factory = launcher.getFactory();
		final CtClass<?> foo = (CtClass<?>) factory.Type().get(Foo.class);
		TreeSet ts = new TreeSet(new DeepRepresentationComparator());
		ts.addAll(foo.getElements(new AbstractFilter<CtConstructorCall<?>>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall<?> element) {
				return true;
			}
		}));
		constructorCalls = new ArrayList(ts);
		final CtType<Panini> panini = factory.Type().get(Panini.class);
		constructorCallsPanini = panini.getElements(new TypeFilter<>(CtConstructorCall.class));
	}

	@Test
	public void testConstructorCallStringWithoutParameters() {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(2);
		assertConstructorCallWithType(String.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(0, constructorCall);
	}

	@Test
	public void testConstructorCallStringWithParameters() {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(1);
		assertConstructorCallWithType(String.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(1, constructorCall);
	}

	@Test
	public void testConstructorCallObjectWithoutParameters() {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(3);
		assertConstructorCallWithType(Foo.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(0, constructorCall);
	}

	@Test
	public void testConstructorCallObjectWithParameters() {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(4);
		assertConstructorCallWithType(Foo.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(1, constructorCall);
	}

	@Test
	public void testConstructorCallWithGenericArray() {
		final CtConstructorCall<?> ctConstructorCall = constructorCallsPanini.get(0);

		assertEquals(1, ctConstructorCall.getType().getActualTypeArguments().size());
		final CtTypeReference<?> implicitArray = ctConstructorCall.getType().getActualTypeArguments().get(0);
		assertTrue(implicitArray.isImplicit());
		final CtArrayTypeReference implicitArrayTyped = (CtArrayTypeReference) implicitArray;
		assertEquals("", implicitArrayTyped.toString());
		assertEquals("AtomicLong[]", implicitArrayTyped.getSimpleName());
		assertTrue(implicitArrayTyped.getComponentType().isImplicit());
		assertEquals("", implicitArrayTyped.getComponentType().prettyprint());
		assertEquals("AtomicLong", implicitArrayTyped.getComponentType().getSimpleName());
	}

	private void assertHasParameters(int sizeExpected, CtConstructorCall<?> constructorCall) {
		if (sizeExpected == 0) {
			assertEquals(sizeExpected, constructorCall.getArguments().size(), "Constructor call without parameter");
		} else {
			assertEquals(sizeExpected, constructorCall.getArguments().size(), "Constructor call with parameters");
		}
	}

	private void assertIsConstructor(CtConstructorCall<?> constructorCall) {
		assertTrue(constructorCall.getExecutable().isConstructor(), "Method must be a constructor");
	}

	private void assertConstructorCallWithType(Class<?> typeExpected, CtConstructorCall<?> constructorCall) {
		assertSame(typeExpected, constructorCall.getType().getActualClass(), "Constructor call is typed by the class of the constructor");
	}

	@Test
	public void testCoreConstructorCall() {
		Launcher spoon = new Launcher();
		// the minimum is setType()
		CtConstructorCall call = spoon.getFactory().Core().createConstructorCall();
		call.setType(spoon.getFactory().Core().createTypeReference().setSimpleName("Foo"));
		assertEquals("new Foo()", call.toString());

		// now with Code factory
		CtConstructorCall call2 = spoon.getFactory().Code().createConstructorCall(spoon.getFactory().Core().createTypeReference().setSimpleName("Bar"));
		assertEquals("new Bar()", call2.toString());
	}

	@Test
	public void testParameterizedConstructorCallOmittedTypeArgsNoClasspath() {
		// contract: omitted type arguments to constructors must be properly resolved if the context allows
		// the expected type to be known
		List<String> expectedTypeArgNames = Arrays.asList("Integer", "String");
		String sourceFile = "./src/test/resources/noclasspath/GenericTypeEmptyDiamond.java";

		CtTypeReference<?> executableType = getConstructorCallTypeFrom("GenericKnownExpectedType", sourceFile);

		assertTrue(executableType.isParameterized());
		assertEquals(expectedTypeArgNames,
				executableType.getActualTypeArguments().stream()
						.map(CtTypeReference::getSimpleName).collect(Collectors.toList()));
		assertTrue(executableType.getActualTypeArguments().stream().allMatch(CtElement::isImplicit));
	}

	@Test
	public void testParameterizedConstructorCallOmittedTypeArgsUnknownExpectedTypeNoClasspath() {
		// contract: even if the expected type is not known for omitted type arguments the type access must be
		// detected as parameterized
		String sourceFile = "./src/test/resources/noclasspath/GenericTypeEmptyDiamond.java";
		CtTypeReference<?> executableType = getConstructorCallTypeFrom("GenericUnknownExpectedType", sourceFile);
		assertTrue(executableType.isParameterized());
		assertTrue(executableType.getActualTypeArguments().stream().allMatch(CtElement::isImplicit));
	}

	@Test
	public void testParameterizedConstructorCallOmittedTypeArgsResolvedTypeNoClasspath() {
		// contract: if a resolved type (here, java.util.ArrayList) is parameterized with empty diamonds in an
		// unresolved method, the resolved type reference should still be parameterized.
		String sourceFile = "./src/test/resources/noclasspath/GenericTypeEmptyDiamond.java";
		CtTypeReference<?> executableType = getConstructorCallTypeFrom("ArrayList", sourceFile);
		assertTrue(executableType.isParameterized());
	}

	@Test
	public void test_addArgumentAt_addsArgumentToSpecifiedPosition() {
		// contract: addArgumentAt should add arguments to the specified position.

		// arrange
		Factory factory = new Launcher().getFactory();
		factory.getEnvironment().setAutoImports(true);
		CtConstructorCall<?> newLinkedHashMap = (CtConstructorCall<?>) factory
                // make it raw on purpose to simplify assertion
				.createCodeSnippetExpression("new java.util.LinkedHashMap()")
				.compile();

		// act
		// LinkedHashMap has multiple constructors, we're going for:
		// LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) by adding
		// arguments a bit haphazardly

		// 10
		newLinkedHashMap.addArgumentAt(0, factory.createLiteral(10))
			// 10, true
			.addArgumentAt(1, factory.createLiteral(true))
			// 10, 1.4, true
			.addArgumentAt(1, factory.createLiteral(1.4));

		// assert
		assertThat(newLinkedHashMap.toString(), equalTo("new LinkedHashMap(10, 1.4, true)"));
	}

	private CtTypeReference<?> getConstructorCallTypeFrom(String simpleName, String sourceFile) {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(sourceFile);
		CtModel model = launcher.buildModel();
		List<CtConstructorCall<?>> calls =
				model.getElements(element -> element.getExecutable().getType().getSimpleName().equals(simpleName));
		assert calls.size() == 1;
		return calls.get(0).getExecutable().getType();
	}
	
	@Test
	public void testConstructorCorrectTyped() {
		// no constructorcall from the input has the simple object type in noclasspathmode
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/constructorcall-type/ConstructorCallWithTypesNotOnClasspath.java");
		CtModel model = launcher.buildModel();
		for (CtConstructorCall<?> ctConstructorCall : model
				.getElements(new TypeFilter<>(CtConstructorCall.class))) {
			assertThat(ctConstructorCall.getExecutable().getType().getSimpleName(), not(equalTo("Object")));
			assertThat(ctConstructorCall.getType(), not(ctConstructorCall.getFactory().Type().objectType()));
		}
	}
}
