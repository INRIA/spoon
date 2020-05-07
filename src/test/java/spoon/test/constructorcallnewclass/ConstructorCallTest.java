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

import org.junit.Before;
import org.junit.Test;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ConstructorCallTest {
	private List<CtConstructorCall<?>> constructorCalls;
	private List<CtConstructorCall<?>> constructorCallsPanini;

	@Before
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
			assertEquals("Constructor call without parameter", sizeExpected, constructorCall.getArguments().size());
		} else {
			assertEquals("Constructor call with parameters", sizeExpected, constructorCall.getArguments().size());
		}
	}

	private void assertIsConstructor(CtConstructorCall<?> constructorCall) {
		assertTrue("Method must be a constructor", constructorCall.getExecutable().isConstructor());
	}

	private void assertConstructorCallWithType(Class<?> typeExpected, CtConstructorCall<?> constructorCall) {
		assertSame("Constructor call is typed by the class of the constructor", typeExpected, constructorCall.getType().getActualClass());
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
}
