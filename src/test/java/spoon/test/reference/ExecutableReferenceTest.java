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
package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
			fail("We shoudn't have a NullPointerException when we call getAllExecutables.");
		}
	}

	@Test
	public void testSpecifyGetAllExecutablesMethod() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses");
		launcher.run();

		final CtInterface<spoon.test.reference.testclasses.Foo> foo = launcher.getFactory().Interface().get(spoon.test.reference.testclasses.Foo.class);
		final List<CtExecutableReference<?>> fooExecutables = foo.getAllExecutables().stream().collect(Collectors.toList());
		assertEquals(1, fooExecutables.size());
		assertEquals(foo.getSuperInterfaces().stream().findFirst().get().getTypeDeclaration().getMethod("m").getReference(),  launcher.getFactory().Interface().get(SuperFoo.class).getMethod("m").getReference());

		final CtClass<Bar> bar = launcher.getFactory().Class().get(Bar.class);
		final List<CtExecutableReference<?>> barExecutables = bar.getAllExecutables().stream().collect(Collectors.toList());
		assertEquals(12 /* object */ + 1 /* constructor */, barExecutables.size());

		final CtInterface<Kuu> kuu = launcher.getFactory().Interface().get(Kuu.class);
		final List<CtExecutableReference<?>> kuuExecutables = kuu.getAllExecutables().stream().collect(Collectors.toList());
		assertEquals(1 /* default method in interface */, kuuExecutables.size());
		assertEquals(kuu.getMethod("m").getReference(), kuuExecutables.get(0));
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
