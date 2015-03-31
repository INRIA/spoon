package spoon.reflect.visitor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by nicolas on 25/02/2015.
 */
@RunWith(Parameterized.class)
public class CtInheritanceScannerTest<T extends CtVisitable> {

	private static Factory factory = TestUtils.createFactory();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		List<Object[]> values = new ArrayList<>();
		for (Method method : CoreFactory.class.getDeclaredMethods()) {
			if (method.getName().startsWith("create") && method.getReturnType().getSimpleName().startsWith("Ct")) {
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
	private List<Method> getMethodToInvoke(Class<?> entry) throws Exception {
		Queue<Class<?>> tocheck = new LinkedList<>();
		tocheck.add(entry);

		List<Method> toInvoke = new ArrayList<>();
		while (!tocheck.isEmpty()) {
			Class<?> intf = tocheck.poll();

			Assert.assertTrue(intf.isInterface());
			if (!intf.getSimpleName().startsWith("Ct")) {
				continue;
			}
			Method mth;
			try {
				mth = CtInheritanceScanner.class.getDeclaredMethod("visit" + intf.getSimpleName(), intf);

				// we should not have a scan method
				try {
					CtInheritanceScanner.class.getDeclaredMethod("scan" + intf.getSimpleName(), intf);
					Assert.fail("should not have both visit" + intf.getSimpleName() + " and scan" + intf.getSimpleName()
							+ " method");
				} catch (NoSuchMethodException ex) {
				}
			} catch (NoSuchMethodException ex) {
				mth = CtInheritanceScanner.class.getDeclaredMethod("scan" + intf.getSimpleName(), intf);
			}
			if (!toInvoke.contains(mth)) {
				toInvoke.add(mth);
			}
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
		for (int i = 0; i < toInvoke.size(); i++) {
			try {
				toInvoke.get(i).invoke(verify(mocked), instance);
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

}
