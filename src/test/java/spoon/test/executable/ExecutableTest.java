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
package spoon.test.executable;

import org.junit.Test;
import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.executable.testclasses.A;
import spoon.test.executable.testclasses.Pozole;
import spoon.test.executable.testclasses.WithEnum;
import spoon.test.main.MainTest;
import spoon.testing.utils.ModelUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExecutableTest {
	@Test
	public void testInfoInsideAnonymousExecutable() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/executable/testclasses/AnonymousExecutableSample.java");
		launcher.run();

		final List<CtAnonymousExecutable> anonymousExecutables = Query.getElements(launcher.getFactory(), new TypeFilter<>(CtAnonymousExecutable.class));

		assertEquals(2, anonymousExecutables.size());

		for (CtAnonymousExecutable anonymousExecutable : anonymousExecutables) {
			assertEquals("", anonymousExecutable.getSimpleName());
			assertEquals(launcher.getFactory().Type().VOID_PRIMITIVE, anonymousExecutable.getType());
			assertEquals(0, anonymousExecutable.getParameters().size());
			assertEquals(0, anonymousExecutable.getThrownTypes().size());
		}
	}

	@Test
	public void testBlockInExecutable() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);
		assertTrue(aPozole.getMethod("m").getBody().getStatement(1) instanceof CtBlock);
	}

	@Test
	public void testGetReference() throws Exception {
		final CtType<A> aClass = ModelUtils.buildClass(A.class);

		String methodName = "getInt1";
		CtExecutableReference<?> methodRef = aClass.getMethod(methodName).getReference();
		assertFalse(methodRef.isFinal());
		assertTrue(methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());

		methodName = "getInt2";
		methodRef = aClass.getMethod(methodName).getReference();
		assertTrue(methodRef.isFinal());
		assertTrue(methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());

		methodName = "getInt3";
		methodRef = aClass.getMethod(methodName).getReference();
		assertTrue(methodRef.isFinal());
		assertFalse(methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());

		methodName = "getInt4";
		methodRef = aClass.getMethod(methodName).getReference();
		assertFalse(methodRef.isFinal());
		assertFalse(methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());
	}

	@Test
	public void testShadowValueOf() {
		// contract: the valueOf method should be correctly retrieved in shadow mode
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/executable/testclasses/WithEnum.java");
		CtModel ctModel = launcher.buildModel();
		List<CtExecutableReference> listValueOf = ctModel.
				filterChildren(new TypeFilter<>(CtExecutableReference.class)).
				filterChildren((Filter<CtExecutableReference>) element -> {
					return "valueOf".equals(element.getSimpleName());
				}).list();

		assertEquals(1, listValueOf.size());
		CtExecutableReference valueOf = listValueOf.get(0);

		Launcher launcherShadow = new Launcher();
		CtType<?> ctType = launcher.getFactory().Type().get(WithEnum.class);
		List<CtExecutableReference> listShadowValueOf = ctType.filterChildren(new TypeFilter<>(CtExecutableReference.class))
				.filterChildren((Filter<CtExecutableReference>) element -> {
					return "valueOf".equals(element.getSimpleName());
				}).list();
		assertEquals(1, listShadowValueOf.size());
		CtExecutableReference shadowValueOf = listShadowValueOf.get(0);

		assertEquals(valueOf, shadowValueOf);
		assertEquals(valueOf.getDeclaration(), shadowValueOf.getDeclaration());
		new ContractVerifier(shadowValueOf.getParent(CtPackage.class)).checkShadow();
	}
}
