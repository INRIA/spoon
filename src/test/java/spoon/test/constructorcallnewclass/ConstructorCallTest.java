package spoon.test.constructorcallnewclass;

import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.test.TestUtils;
import spoon.test.constructorcallnewclass.testclasses.Foo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConstructorCallTest {
	private List<CtConstructorCall<?>> constructorCalls;

	@Before
	public void setUp() throws Exception {
		final Factory factory = TestUtils.build(Foo.class);
		final CtClass<?> foo = (CtClass<?>) factory.Type().get(Foo.class);
		constructorCalls = foo.getElements(
			(CtConstructorCall<?> element) -> { return true; }
		);
	}

	@Test
	public void testConstructorCallStringWithoutParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(0);
		assertType(String.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(0, constructorCall);
	}

	@Test
	public void testConstructorCallStringWithParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(1);
		assertType(String.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(1, constructorCall);
	}

	@Test
	public void testConstructorCallObjectWithoutParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(2);
		assertType(Foo.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(0, constructorCall);
	}

	@Test
	public void testConstructorCallObjectWithParameters() throws Exception {
		final CtConstructorCall<?> constructorCall = constructorCalls.get(3);
		assertType(Foo.class, constructorCall);
		assertIsConstructor(constructorCall);
		assertHasParameters(1, constructorCall);
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

	private void assertType(Class<?> typeExpected, CtConstructorCall<?> constructorCall) {
		assertEquals("Constructor call is typed by the class of the constructor", typeExpected, constructorCall.getType().getActualClass());
	}
}
