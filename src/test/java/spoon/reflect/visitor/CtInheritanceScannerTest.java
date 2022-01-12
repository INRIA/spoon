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
package spoon.reflect.visitor;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static spoon.testing.utils.ModelUtils.createFactory;

/**
 *
 * Tests the main contract of CtInheritanceScanner
 *
 * Can be called with
 * $ mvn test -D test=spoon.reflect.visitor.CtInheritanceScannerTest
 *
 * Created by nicolas on 25/02/2015.
 */
public class CtInheritanceScannerTest<T extends CtVisitable> {


	@TestFactory
	public List<DynamicTest> createTestcases() throws Exception {
		Factory factory = createFactory();
		List<DynamicTest> methods = new ArrayList<>();
		for (Method method : CoreFactory.class.getDeclaredMethods()) {
			if (method.getName().startsWith("create")
					&& method.getParameterCount() == 0
					&& method.getReturnType().getSimpleName().startsWith("Ct")) {
				methods.add(DynamicTest.dynamicTest(method.getName(),
						(() -> testCtInheritanceScanner(method.getReturnType(),
								(T) method.invoke(factory.Core())))));
			}
		}
		return methods;
	}


	/**
	 * Create the list of method we have to call for a class
	 */
	private List<Method> getMethodToInvoke(Class<?> entry) {
		Queue<Class<?>> tocheck = new LinkedList<>();
		tocheck.add(entry);

		List<Method> toInvoke = new ArrayList<>();
		while (!tocheck.isEmpty()) {
			Class<?> intf = tocheck.poll();

			assertTrue(intf.isInterface());
			if (!intf.getSimpleName().startsWith("Ct")) {
				continue;
			}
			Method mth = null;

			// if a method visitX exists, it must be invoked
			try {
				mth = CtInheritanceScanner.class.getDeclaredMethod("visit" + intf.getSimpleName(), intf);
				if (mth.getAnnotation(Deprecated.class) != null) {
					// if the method visitX exists with a deprecated annotation, it mustn't be invoked.
					mth = null;
				}
			} catch (NoSuchMethodException ex) {
				// no such method, nothing
			}
			if (mth != null && !toInvoke.contains(mth)) {
				toInvoke.add(mth);
			}

			// if a method scanX exists, it must be invoked
			try {
				mth = CtInheritanceScanner.class.getDeclaredMethod("scan" + intf.getSimpleName(), intf);
				if (mth.getAnnotation(Deprecated.class) != null) {
					// if the method scanX exists with a deprecated annotation, it mustn't be invoked.
					mth = null;
				}
			} catch (NoSuchMethodException ex) {
				// no such method, nothing
			}
			if (mth != null && !toInvoke.contains(mth)) {
				toInvoke.add(mth);
			}

			// recursion
			for (Class<?> aClass : intf.getInterfaces()) {
				tocheck.add(aClass);
			}
		}
		return toInvoke;
	}

	public void testCtInheritanceScanner(Class<?> toTest, T instance) throws Throwable {
		CtInheritanceScanner mocked = mock(CtInheritanceScanner.class);
		List<Method> toInvoke = getMethodToInvoke(toTest);
		// we invoke super for all method we attempt to call
		for (Method method : toInvoke) {
			method.invoke(Mockito.doCallRealMethod().when(mocked), instance);
		}
		instance.accept(mocked);

		// verify we call all methods
		for (Method aToInvoke : toInvoke) {
			try {
				aToInvoke.invoke(verify(mocked), instance);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof AssertionError) {
					fail("visit" + instance.getClass().getSimpleName().replaceAll("Impl$", "") + " does not call " + aToInvoke.getName());
				} else {
					throw e.getTargetException();
				}
			}
		}
	}
}
