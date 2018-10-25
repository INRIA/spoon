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
package spoon.test.parent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.reflect.CtModelImpl;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.ModuleFactory;
import spoon.reflect.visitor.CtVisitable;

import java.lang.reflect.Method;
import java.util.Collection;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.parent.ContractOnSettersParametrizedTest.createCompatibleObject;
import static spoon.test.parent.ContractOnSettersParametrizedTest.createReceiverList;
import static spoon.testing.utils.ModelUtils.createFactory;

// contract: setParent does not modifiy the state of the parent
@RunWith(Parameterized.class)
public class SetParentTest<T extends CtVisitable> {

	private static Factory factory = createFactory();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return createReceiverList();
	}

	@Parameterized.Parameter(0)
	public CtType<?> toTest;

	@Test
	public void testContract() throws Throwable {
		Object o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		CtMethod<?> setter = factory.Type().get(CtElement.class).getMethodsByName("setParent").get(0);

		Object argument = createCompatibleObject(setter.getParameters().get(0).getType());

		if (!(argument instanceof CtElement)) {
			// is a primitive type or a list
			throw new AssertionError("impossible, setParent always takes an element");
		}
		// we create a fresh object
		CtElement receiver = ((CtElement) o).clone();

		if ("CtClass".equals(toTest.getSimpleName())
				|| "CtInterface".equals(toTest.getSimpleName())
				|| "CtEnum".equals(toTest.getSimpleName())
				|| "CtAnnotationType".equals(toTest.getSimpleName())
				|| "CtPackage".equals(toTest.getSimpleName())
				) {
			// contract: root package is the parent for those classes
			assertTrue(receiver.getParent() instanceof CtModelImpl.CtRootPackage);
		} else if ("CtModule".equals(toTest.getSimpleName())) {
			// contract: module parent is necessarily the unnamedmodule
			assertTrue(receiver.getParent() instanceof ModuleFactory.CtUnnamedModule);
		} else if ("CtCompilationUnit".equals(toTest.getSimpleName())) {
			// contract: CtCompilationUnit parent is null
			assertNull(receiver.getParent());
		} else {
			// contract: there is no parent before
			try {
				receiver.getParent().hashCode();
				fail(receiver.getParent().getClass().getSimpleName());
			} catch (ParentNotInitializedException normal) {
			}
		}

		Method actualMethod = setter.getReference().getActualMethod();
		CtElement argumentClone = ((CtElement) argument).clone();
		actualMethod.invoke(receiver, new Object[]{argument});

		// contract: the parent has not been changed by a call to setParent on an elemnt
		assertTrue(argument.equals(argumentClone));
		assertNotSame(argument, argumentClone);

	}

}
