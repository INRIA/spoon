package spoon.test.parent;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitable;
import spoon.support.UnsettableProperty;
import spoon.test.SpoonTestHelpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static spoon.testing.utils.ModelUtils.createFactory;

// check that all setters of the metamodel call setParent in a correct manner
@RunWith(Parameterized.class)
public class ParentContractTest<T extends CtVisitable> {

	private static Factory factory = createFactory();
	private static final List<CtType<? extends CtElement>> allInstantiableMetamodelInterfaces = SpoonTestHelpers.getAllInstantiableMetamodelInterfaces();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		List<Object[]> values = new ArrayList<>();
		for (CtType t : allInstantiableMetamodelInterfaces) {
			if (!(CtReference.class.isAssignableFrom(t.getActualClass()))) {
				values.add(new Object[] { t });
			}
		}
		return values;
	}

	@Parameterized.Parameter(0)
	public CtType<?> toTest;

	public static Object createCompatibleObject(CtTypeReference<?> parameterType) {
		Class<?> c = parameterType.getActualClass();
		for(CtType t : allInstantiableMetamodelInterfaces) {
			if (c.isAssignableFrom(t.getActualClass())) {
				CtElement argument = factory.Core().create(t.getActualClass());
				// an empty package is merged with the existing one
				// we have to give it a name
				if (argument instanceof CtPackage) {
					((CtPackage) argument).setSimpleName(argument.getShortRepresentation());
				}
				return argument;

			}
		}
		if (Set.class.isAssignableFrom(c)) {
			// we create one set with one element
			HashSet<Object> objects = new HashSet<>();
			objects.add(createCompatibleObject(parameterType.getActualTypeArguments().get(0)));
			return objects;
		}
		if (Collection.class.isAssignableFrom(c)) {
			// we create one list with one element
			ArrayList<Object> objects = new ArrayList<>();
			objects.add(createCompatibleObject(parameterType.getActualTypeArguments().get(0)));
			return objects;
		}
		throw new IllegalArgumentException("cannot instantiate "+parameterType);
	}
	static int nTotalSetterCalls= 0;

	@Test
	public void testContract() throws Throwable {
		int nSetterCalls= 0;
		int nAssertsOnParent = 0;
		int nAssertsOnParentInList = 0;
		// contract: all setters/adders must set the parent (not necessarily the direct parent, can be upper in the parent tree, for instance when injecting blocks
		Object o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		for (CtMethod<?> setter : SpoonTestHelpers.getAllSetters(toTest)) {

			Object argument = createCompatibleObject(setter.getParameters().get(0).getType());

			try {
				// we create a fresh object
				CtElement receiver = ((CtElement) o).clone();

				// we invoke the setter
				Method actualMethod = setter.getReference().getActualMethod();
				actualMethod.invoke(receiver, new Object[] { argument });
				nSetterCalls++;
				nTotalSetterCalls++;
				// if it's a settable property
				// we check that setParent has been called

				// directly the element
				if (CtElement.class.isInstance(argument)
				  && setter.getAnnotation(UnsettableProperty.class) == null) {
					nAssertsOnParent++;
					assertTrue(((CtElement)argument).hasParent(receiver));
				}

				// the element is in a list
				if (Collection.class.isInstance(argument)
						&& setter.getAnnotation(UnsettableProperty.class) == null) {
					nAssertsOnParentInList++;
					assertTrue(((CtElement)((Collection)argument).iterator().next()).hasParent(receiver));
				}


			} catch (AssertionError e) {
				Assert.fail("call setParent contract failed for " + setter.toString() + " " + e.toString());
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof UnsupportedOperationException) {
					// fail-safe contract: we can always call a setter
					// this simplifies client code which does not have to write defensive if/then or try/catch
					// if the setter does nothing
					// this is now documented by @UnsettableProperty
					throw e;
				} else if (e.getCause() instanceof RuntimeException) {
					throw e.getCause();
				} else {
					throw new SpoonException(e.getCause());
				}
			}
		}
		assertTrue(nSetterCalls > 0);
		assertTrue(nAssertsOnParent > 0 || nAssertsOnParentInList > 0);
	}

}
