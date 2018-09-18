package spoon.test.parent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.SpoonException;
import spoon.support.modelobs.ActionBasedChangeListenerImpl;
import spoon.support.modelobs.action.Action;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

/**
 * check that all setters of the metamodel do the right things:
 * - call setParent
 * - trigger a change event
  */
@RunWith(Parameterized.class)
public class ContractOnSettersParametrizedTest<T extends CtVisitable> {

	private static Factory factory = createFactory();
	private static final List<CtType<? extends CtElement>> allInstantiableMetamodelInterfaces = SpoonTestHelpers.getAllInstantiableMetamodelInterfaces();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return createReceiverList();
	}

	public static Collection<Object[]> createReceiverList() {
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

	class ModelChangeListener extends ActionBasedChangeListenerImpl {
		int nbCallsToOnAction = 0;
		List changedElements = new ArrayList();
		@Override
		public void onAction(Action action) {
			super.onAction(action);
			changedElements.add(action.getContext().getElementWhereChangeHappens());
			nbCallsToOnAction++;
		}
	}

	ModelChangeListener changeListener = new ModelChangeListener();

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
		factory.getEnvironment().setModelChangeListener(changeListener);
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

				int nBefore = changeListener.nbCallsToOnAction;
				changeListener.changedElements = new ArrayList();

				// here we actually call the setter
				actualMethod.invoke(receiver, new Object[] { argument });

				int nAfter = changeListener.nbCallsToOnAction;

				// contract: at least one change event is well fired (sometimes it is more than one for complex setters)
				assertTrue(actualMethod.getName(), nBefore < nAfter);

				nSetterCalls++;
				nTotalSetterCalls++;
				// if it's a settable property
				// we check that setParent has been called

				// directly the element
				if (argument instanceof CtElement
				  && setter.getAnnotation(UnsettableProperty.class) == null) {
					nAssertsOnParent++;
					assertTrue(((CtElement)argument).hasParent(receiver));
				}

				// the element is in a list
				if (argument instanceof Collection
						&& setter.getAnnotation(UnsettableProperty.class) == null) {
					nAssertsOnParentInList++;
					assertTrue(((CtElement)((Collection)argument).iterator().next()).hasParent(receiver));
				}


			} catch (AssertionError e) {
				System.err.println("one contract failed for " + setter.toString());
				throw e;
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
