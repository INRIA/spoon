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
package spoon.test.invocations;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.invocations.testclasses.Bar;
import spoon.test.invocations.testclasses.Foo;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class InvocationTest {

	@Test
	public void testTypeOfStaticInvocation() {
		SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/invocations/testclasses/", "-o", "./target/spooned/"
		});
		Factory factory = launcher.getFactory();

		CtClass<?> aClass = factory.Class().get(Foo.class);

		final List<CtInvocation<?>> elements = aClass.getElements(new AbstractFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return element.getTarget() != null;
			}
		});

		assertEquals(2, elements.size());
		assertTrue(elements.get(0).getTarget() instanceof CtTypeAccess);
		assertTrue(elements.get(1).getTarget() instanceof CtTypeAccess);
	}

	@Test
	public void testTargetNullForStaticMethod() throws Exception {
		final Factory factory = build(Bar.class);
		final CtClass<Bar> barClass = factory.Class().get(Bar.class);
		final CtMethod<?> staticMethod = barClass.getMethodsByName("staticMethod").get(0);
		final CtExecutableReference<?> reference = factory.Method().createReference(staticMethod);

		try {
			final CtInvocation<?> invocation = factory.Code().createInvocation(null, reference);
			assertNull(invocation.getTarget());
		} catch (NullPointerException e) {
			fail();
		}
	}

	@Test
	public void testIssue1753() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/elasticsearch1753");

		final CtModel model = launcher.buildModel();
		final List<CtExecutable> executables =
				model.getElements(new TypeFilter<>(CtExecutable.class))
						.stream()
						.filter(i -> i.getPosition().getLine() == 190)
						.collect(Collectors.toList());
		assertEquals(1, executables.size());
		final CtExecutable exe = executables.get(0);
		assertNotNull(exe.getReference().getDeclaration());
	}
}
