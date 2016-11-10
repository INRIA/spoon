package spoon.test.parent;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import spoon.SpoonException;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtVisitable;
import spoon.test.SpoonTestHelpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static spoon.testing.utils.ModelUtils.createFactory;

// check that all setters of the metamodel call setParent
@RunWith(Parameterized.class)
public class ParentContractTest<T extends CtVisitable> {

	private static Factory factory = createFactory();
	private static final List<CtType<? extends CtElement>> allInstantiableMetamodelInterfaces = SpoonTestHelpers.getAllInstantiableMetamodelInterfaces();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		List<Object[]> values = new ArrayList<>();
		for (CtType t : allInstantiableMetamodelInterfaces) {
			if (!(CtReference.class.isAssignableFrom(t.getActualClass()))) {
				values.add(new Object[] { t.getActualClass(), factory.Core().create(t.getActualClass()) });
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

			assertTrue(intf.isInterface());
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

	private CtElement createCompatibleObject(Class c) {
		for(CtType t : allInstantiableMetamodelInterfaces) {
			if (c.isAssignableFrom(t.getActualClass())) {
				return factory.Core().create(t.getActualClass());
			}
		}
		throw new IllegalArgumentException();
	}
	@Test
	public void testContract() throws Throwable {
		// contract: all setters/adders must set the parent (not necessarily the direct parent, can be upper in the parent tree, for instance when injecting blocks
		Object o = instance;
		List<Method> methodsToInvoke = getMethodsToInvoke(toTest);
		for (Method setter : methodsToInvoke) {

			// special case: impossible methods on some objects (they throw an UnsupportedOperationException)
			if (setter.getAnnotation(Deprecated.class) != null) continue;
			if (o instanceof CtAnnotationType && "addMethod".equals(setter.getName())) continue;
			if (CtActualTypeContainer.class.isAssignableFrom(o.getClass())) {
				if ("setActualTypeArguments".equals(setter.getName())) continue;
				if ("addActualTypeArgument".equals(setter.getName())) continue;
			}
			if (o instanceof CtInvocation && "setType".equals(setter.getName())) continue;
			if ((o instanceof CtConstructorCall || CtConstructorCall.class.isAssignableFrom(o.getClass())) && "setType".equals(setter.getName())) continue;
			if (o instanceof CtTypeAccess && "setType".equals(setter.getName())) continue;
			if (o instanceof CtType && "setSuperclass".equals(setter.getName())) continue;


			CtElement argument = createCompatibleObject((Class<? extends CtElement>) setter.getParameters()[0].getType());
			// an empty package is merged with the existing one
			// we have to give it a name
			if (argument instanceof CtPackage) {
				((CtPackage)argument).setSimpleName("foobar");
			}
			try {
				// we create a fresh object
				CtElement receiver = ((CtElement) o).clone();
				// we invoke the setter
				setter.invoke(receiver, new Object[] { argument });
				// we check that setParent has been called
				assertTrue(argument.hasParent(receiver));
			} catch (AssertionError e) {
				Assert.fail("call setParent contract failed for " + setter.toString() + " " + e.toString());
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof UnsupportedOperationException) {
					// ignore
				} else if (e.getCause() instanceof RuntimeException) {
					throw e.getCause();
				} else {
					throw new SpoonException(e.getCause());
				}
			}
		}
	}

}
