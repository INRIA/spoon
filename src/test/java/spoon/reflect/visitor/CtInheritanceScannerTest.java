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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
@RunWith(Parameterized.class)
public class CtInheritanceScannerTest<T extends CtVisitable> {

	private static Factory factory = createFactory();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		List<Object[]> values = new ArrayList<>();
		for (Method method : CoreFactory.class.getDeclaredMethods()) {
			if (method.getName().startsWith("create")
					&& method.getParameterCount() == 0
					&& method.getReturnType().getSimpleName().startsWith("Ct")) {
				values.add(new Object[] { method.getReturnType(), method.invoke(factory.Core()) });
			}
		}
		return values;
	}

	@Parameterized.Parameter(0)
	public Class<T> toTest;

	@Parameterized.Parameter(1)
	public T instance;

	/**
	 * Create the list of method we have to call for a class
	 *
	 * @param entry
	 * @return
	 * @throws Exception
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

	/**
	 * A return element is a flow break and a statement
	 */
	@Test
	public void testCtInheritanceScanner() throws Throwable {
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
