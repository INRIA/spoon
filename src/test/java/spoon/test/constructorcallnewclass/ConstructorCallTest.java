package spoon.test.constructorcallnewclass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.DeepRepresentationComparator;
import spoon.test.constructorcallnewclass.testclasses.Foo;
import spoon.test.constructorcallnewclass.testclasses.Panini;

public class ConstructorCallTest {
	private List<CtConstructorCall<?>> constructorCalls;
	private List<CtConstructorCall<?>> constructorCallsPanini;

	@Before
	public void setUp() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/" + Foo.class.getCanonicalName().replace(".", "/") + ".java");
		launcher.addInputResource("./src/test/java/" + Panini.class.getCanonicalName().replace(".", "/") + ".java");
		launcher.setSourceOutputDirectory("./target/spooned");
		launcher.run();
		final Factory factory = launcher.getFactory();
		final CtClass<?> foo = (CtClass<?>) factory.Type().get(Foo.class);
		TreeSet ts = new TreeSet(new DeepRepresentationComparator());
		ts.addAll(foo.getElements(new AbstractFilter<CtConstructorCall<?>>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall<?> element) {
				return true;
			}
		}));
		constructorCalls = new ArrayList(ts);
		final CtType<Panini> panini = factory.Type().get(Panini.class);
		constructorCallsPanini = panini.getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class));
	}

	@Test
	public void testConstructorCallStringWithoutParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(0);
		assertConstructorCallWithType(String.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(0, constructorCall);
	}

	@Test
	public void testConstructorCallStringWithParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(1);
		assertConstructorCallWithType(String.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(1, constructorCall);
	}

	@Test
	public void testConstructorCallObjectWithoutParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(2);
		assertConstructorCallWithType(Foo.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(0, constructorCall);
	}

	@Test
	public void testConstructorCallObjectWithParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(3);
		assertConstructorCallWithType(Foo.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(1, constructorCall);
	}

	@Test
	public void testConstructorCallWithGenericArray() throws Exception {
		final CtConstructorCall<?> ctConstructorCall = constructorCallsPanini.get(0);

		assertEquals(1, ctConstructorCall.getType().getActualTypeArguments().size());
		final CtTypeReference<?> implicitArray = ctConstructorCall.getType().getActualTypeArguments().get(0);
		assertTrue(implicitArray.isImplicit());
		final CtArrayTypeReference implicitArrayTyped = (CtArrayTypeReference) implicitArray;
		assertEquals("", implicitArrayTyped.toString());
		assertEquals("Array", implicitArrayTyped.getSimpleName());
		assertTrue(implicitArrayTyped.getComponentType().isImplicit());
		assertEquals("", implicitArrayTyped.getComponentType().toString());
		assertEquals("AtomicLong", implicitArrayTyped.getComponentType().getSimpleName());
	}

	private void assertHasParameters(int sizeExpected, CtConstructorCall<?> constructorCall) {
		if (sizeExpected == 0) {
			assertEquals("Constructor call without parameter", sizeExpected, constructorCall.getArguments().size());
		} else {
			assertEquals("Constructor call with parameters", sizeExpected, constructorCall.getArguments().size());
		}
	}

	private void assertIsConstructor(CtConstructorCall<?> constructorCall) {
		assertTrue("Method must be a constructor", constructorCall.getExecutable().isConstructor());
	}

	private void assertConstructorCallWithType(Class<?> typeExpected, CtConstructorCall<?> constructorCall) {
		assertEquals("Constructor call is typed by the class of the constructor", typeExpected, constructorCall.getType().getActualClass());
	}
}
