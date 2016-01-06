package spoon.test.parent;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtVisitable;
import spoon.test.TestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

// check that all setters of the metamodel call setParent
@RunWith(Parameterized.class)
public class ParentContractTest<T extends CtVisitable> {

	private static Factory factory = TestUtils.createFactory();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		List<Object[]> values = new ArrayList<>();
		for (Method method : CoreFactory.class.getDeclaredMethods()) {
			if (method.getName().startsWith("create")
					&& method.getReturnType().getSimpleName().startsWith("Ct")
					&& !CtReference.class.isAssignableFrom(method.getReturnType())
					){
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
	private List<Method> getMethodsToInvoke(Class<?> entry) throws Exception {
		Queue<Class<?>> tocheck = new LinkedList<>();
		tocheck.add(entry);

		List<Method> toInvoke = new ArrayList<>();
		while (!tocheck.isEmpty()) {
			Class<?> intf = tocheck.poll();

			Assert.assertTrue(intf.isInterface());
			if (!intf.getSimpleName().startsWith("Ct")) {
				continue;
			}

			if (intf.getSimpleName().equals("CtElement")) {
				continue;
			}

			// get all setters with take a CtElement as parameter
			for(Method mth : intf.getDeclaredMethods()) {
				if ((mth.getName().startsWith("set")
						|| mth.getName().startsWith("add"))
						&& mth.getParameterTypes().length==1
						&& CtElement.class.isAssignableFrom(mth.getParameterTypes()[0])
						) {
					if (!toInvoke.contains(mth)) {
						toInvoke.add(mth);
					}
				}
			}

			for (Class<?> aClass : intf.getInterfaces()) {
				tocheck.add(aClass);
			}
		}
		return toInvoke;
	}

	@Test
	public void testContract() throws Throwable {
		Object o = instance;
		for (Method setter : getMethodsToInvoke(toTest)) {

			// special case: impossible methods on some objects (they throw an UnsupportedOperationException)
			if (o instanceof CtAnnotationType && "addMethod".equals(setter.getName())) continue;
			if (o instanceof CtAnnotationType && "addFormalTypeParameter".equals(setter.getName())) continue;
			if (o instanceof CtParameter && "setDefaultExpression".equals(setter.getName())) continue;
			if (o instanceof CtCatchVariable && "setDefaultExpression".equals(setter.getName())) continue;
			if (o instanceof CtConstructor && "setType".equals(setter.getName())) continue;
			if (o instanceof CtInvocation && "setType".equals(setter.getName())) continue;
			if (o instanceof CtConstructorCall && "setType".equals(setter.getName())) continue;
			if (o instanceof CtNewClass && "setType".equals(setter.getName())) continue;
			if (o instanceof CtAnonymousExecutable && ("addParameter".equals(setter.getName()) || "setParameters".equals(setter.getName()))) continue;
			if (o instanceof CtAnonymousExecutable && ("addThrownType".equals(setter.getName()) || "setThrownTypes".equals(setter.getName()))) continue;
			if (o instanceof CtAnonymousExecutable && "setType".equals(setter.getName())) continue;
			if (o instanceof CtTypeAccess && "setType".equals(setter.getName())) continue;

			CtElement mockedArgument = (CtElement) mock(setter.getParameters()[0].getType(),  Mockito.withSettings().extraInterfaces(Comparable.class));
			try {
				// we create a fresh object
				CtElement receiver = (CtElement)factory.Core().clone(o);
				// we invoke the setter
				setter.invoke(receiver, new Object[]{mockedArgument});
				// we check that setParent has been called
				verify(mockedArgument).setParent((CtElement) receiver);
			} catch (AssertionError e) {
				Assert.fail("call setParent contract failed for "+setter.toString()+" "+e.toString());
			}
		}
	}

}
