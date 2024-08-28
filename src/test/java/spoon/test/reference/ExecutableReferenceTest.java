/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.reference;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.InvisibleArrayConstructorImpl;
import spoon.test.reference.testclasses.Bar;
import spoon.test.reference.testclasses.Burritos;
import spoon.test.reference.testclasses.EnumValue;
import spoon.test.reference.testclasses.Kuu;
import spoon.test.reference.testclasses.Stream;
import spoon.test.reference.testclasses.SuperFoo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ExecutableReferenceTest {
	@Test
	public void testCallMethodOfClassNotPresent() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/resources/executable-reference", "--output-type", "nooutput"
		});
		final List<CtExecutableReference<?>> references = Query.getReferences(launcher.getFactory(), new ReferenceTypeFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference<?> reference) {
				return !reference.isConstructor() && super.matches(reference);
			}
		});

		final List<CtInvocation<?>> invocations = Query.getElements(launcher.getFactory(), new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return !element.getExecutable().isConstructor() && super.matches(element);
			}
		});

		assertEquals(4, references.size());
		assertEquals(4, invocations.size());

		// Executable reference with 0 parameter.
		final CtExecutableReference<?> executableZeroParameter = references.get(0);
		assertNotNull(executableZeroParameter.getDeclaringType());
		assertNull(executableZeroParameter.getType());
		assertEquals(0, executableZeroParameter.getParameters().size());
		assertEquals("m()", executableZeroParameter.toString());
		assertEquals("new Bar().m()", invocations.get(0).toString());

		// Executable reference with 1 parameter and return type.
		final CtExecutableReference<?> executableOneParameter = references.get(1);
		assertNotNull(executableOneParameter.getDeclaringType());
		assertNotNull(executableOneParameter.getType());
		assertEquals(1, executableOneParameter.getParameters().size());
		assertNotEquals(executableZeroParameter, executableOneParameter);
		assertEquals("m(int)", executableOneParameter.toString());
		assertEquals("bar.m(1)", invocations.get(1).toString());

		// Executable reference with 2 parameters.
		final CtExecutableReference<?> executableTwoParameters = references.get(2);
		assertNotNull(executableTwoParameters.getDeclaringType());
		assertNull(executableTwoParameters.getType());
		assertEquals(2, executableTwoParameters.getParameters().size());
		assertNotEquals(executableTwoParameters, executableZeroParameter);
		assertNotEquals(executableTwoParameters, executableOneParameter);
		assertEquals("m(int,java.lang.String)", executableTwoParameters.toString());
		assertEquals("new Bar().m(1, \"5\")", invocations.get(2).toString());

		// Static Executable reference.
		final CtExecutableReference<?> staticExecutable = references.get(3);
		assertNotNull(staticExecutable.getDeclaringType());
		assertNull(staticExecutable.getType());
		assertEquals(1, staticExecutable.getParameters().size());
		assertNotEquals(staticExecutable, executableZeroParameter);
		assertNotEquals(staticExecutable, executableOneParameter);
		assertEquals("m(java.lang.String)", staticExecutable.toString());
		assertEquals("Bar.m(\"42\")", invocations.get(3).toString());
	}

	@Test
	public void testSuperClassInGetAllExecutables() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses/");
		launcher.setSourceOutputDirectory("./target/spoon-test");
		launcher.run();

		final CtClass<Burritos> aBurritos = launcher.getFactory().Class().get(Burritos.class);
		final CtMethod<?> aMethod = aBurritos.getMethodsByName("m").get(0);
		try {
			aMethod.getType().getAllExecutables();
		} catch (NullPointerException e) {
			fail("We shouldn't have a NullPointerException when we call getAllExecutables.");
		}
	}

	@Test
	public void testGetAllExecutablesMethodForInterface() {
		// contract: As interfaces doesn't extend object, the Foo interface must have 1 method and no method from object.
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses");
		launcher.run();
		CtInterface<Foo> foo = launcher.getFactory().Interface().get(spoon.test.reference.testclasses.Foo.class);
		Collection<CtExecutableReference<?>> fooExecutables = foo.getAllExecutables();
		assertAll(
				() ->assertEquals(1, fooExecutables.size()),
				() ->assertEquals(foo.getSuperInterfaces().iterator().next().getTypeDeclaration().getMethod("m").getReference(),
						launcher.getFactory().Interface().get(SuperFoo.class).getMethod("m").getReference()));
	}

	@Test
	public void testGetAllExecutablesMethodForClasses() {
		// contract: As classes extend object and the Bar class has 1 method, getAllExecutables for Bar must return 12/13.
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses");
		launcher.run();
		CtClass<Bar> bar = launcher.getFactory().Class().get(Bar.class);
		Collection<CtExecutableReference<?>> barExecutables = bar.getAllExecutables();
		/*
		This assertion is needed because in java.lang.object the method registerNative was removed.
		See https://bugs.openjdk.java.net/browse/JDK-8232801 for details.
		To fit this change and support new jdks and the CI both values are correct.
		In jdk8 object has 12 methods and in newer jdk object has 11
		 */
		assertTrue(barExecutables.size() == 12 || barExecutables.size() == 13);
		CtInterface<Kuu> kuu = launcher.getFactory().Interface().get(Kuu.class);
		List<CtExecutableReference<?>> kuuExecutables = new ArrayList<>(kuu.getAllExecutables());
		assertAll(
				() -> assertEquals(1 /* default method in interface */, kuuExecutables.size()),
				() -> assertEquals(kuu.getMethod("m").getReference(), kuuExecutables.get(0)));
	}

	@Test
	public void testCreateReferenceForAnonymousExecutable() {
		final spoon.Launcher launcher = new spoon.Launcher();
		launcher.addInputResource("src/test/resources/noclasspath/Foo4.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtExecutable<?>>(CtExecutable.class) {
			@Override
			public boolean matches(final CtExecutable<?> exec) {
				try {
					exec.getReference();
				} catch (ClassCastException ex) {
					fail(ex.getMessage());
				}
				return super.matches(exec);
			}
		});
	}

	@Test
	public void testInvokeEnumMethod() {
		final spoon.Launcher launcher = new spoon.Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/Enum.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		CtInvocation invocation = launcher.getModel().getElements(new TypeFilter<CtInvocation>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation element) {
				return super.matches(element) 
					&& "valueOf".equals(element.getExecutable().getSimpleName());
			}
		}).get(0);
		assertNotNull(invocation.getExecutable().getExecutableDeclaration());
	}

	@Test
	public void testLambdaNoClasspath() {
		final Launcher launcher = new Launcher();
		// Throws `IllegalStateException` before PR #1100 due to invalid AST
		// hierarchy.
		launcher.addInputResource("./src/test/resources/noclasspath/org/elasticsearch/action/admin/cluster/node/tasks");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();
	}

	@Test
	public void testHashcodeWorksWithReference() {
		// contract: two distinct CtExecutableReference should have different hashcodes

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses/EnumValue.java");
		launcher.buildModel();

		CtClass enumValue = launcher.getFactory().Class().get(EnumValue.class);

		CtMethod firstMethod = (CtMethod) enumValue.getMethodsByName("asEnum").get(0);
		CtMethod secondMethod = (CtMethod) enumValue.getMethodsByName("unwrap").get(0);

		assertNotNull(firstMethod);
		assertNotNull(secondMethod);

		assertNotEquals(firstMethod, secondMethod);
		assertNotEquals(firstMethod.getReference(), secondMethod.getReference());

		int hashCode1 = firstMethod.hashCode();
		int hashCode2 = secondMethod.hashCode();

		assertNotEquals(hashCode1, hashCode2);

		hashCode1 = firstMethod.getReference().hashCode();
		hashCode2 = secondMethod.getReference().hashCode();

		assertNotEquals(hashCode1, hashCode2);
	}

	@Test
	public void testPbWithStream() {
		// contract: array constructor references are well represented

		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses/Stream.java");
		launcher.buildModel();

		CtClass klass = launcher.getFactory().Class().get(Stream.class);
		List<CtExecutableReference> executableReferenceList = klass.getElements(new TypeFilter<>(CtExecutableReference.class));
		CtExecutableReference lastExecutableReference = executableReferenceList.get(executableReferenceList.size() - 1);
		CtExecutable declaration = lastExecutableReference.getExecutableDeclaration();

		assertNotNull(declaration);
		assertTrue(declaration instanceof InvisibleArrayConstructorImpl);
		String exepectedString = "spoon.test.reference.testclasses.Bar[]::new";
		assertEquals(exepectedString, declaration.toString());
	}

}
