package spoon.test.constructorcallnewclass;

import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.test.TestUtils;
import spoon.test.constructorcallnewclass.testclasses.Foo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NewClassTest {
	private List<CtNewClass<?>> newClasses;

	@Before
	public void setUp() throws Exception {
		final Factory build = TestUtils.build(Foo.class);
		final CtClass<?> foo = (CtClass<?>) build.Type().get(Foo.class);
		newClasses = foo.getElements(new AbstractFilter<CtNewClass<?>>(CtNewClass.class) {
			@Override
			public boolean matches(CtNewClass<?> element) {
				return true;
			}
		});
	}

	@Test
	public void testNewClassWithObjectClass() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(0);
		assertType(Object.class, newClass);
		assertIsConstructor(newClass);
		assertHasParameters(0, newClass);
		assertIsAnonymous(newClass);
		assertSuperClass(newClass, Object.class);
	}

	@Test
	public void testNewClassWithInterface() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(1);
		assertType(Foo.Bar.class, newClass);
		assertIsConstructor(newClass);
		assertHasParameters(0, newClass);
		assertIsAnonymous(newClass);
		assertSuperInterface(Foo.Bar.class, newClass);
	}

	@Test
	public void testNewClassWithInterfaceGeneric() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(2);
		assertType(Foo.Tacos.class, newClass);
		assertIsConstructor(newClass);
		assertHasParameters(0, newClass);
		assertIsAnonymous(newClass);
		assertSuperInterface(Foo.Tacos.class, newClass);
		assertEquals("Super interface is typed by the class of the constructor", String.class,
				newClass.getAnonymousClass().getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualTypeArguments().get(0).getActualClass());
	}

	@Test
	public void testNewClassInterfaceWithParameters() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(3);
		assertType(Foo.BarImpl.class, newClass);
		assertIsConstructor(newClass);
		assertHasParameters(1, newClass);
		assertIsAnonymous(newClass);
		assertSuperClass(newClass, Foo.BarImpl.class);
	}

	private void assertSuperClass(CtNewClass<?> newClass, Class<?> expected) {
		assertEquals("There isn't a super interface if there is a super class", 0, newClass.getAnonymousClass().getSuperInterfaces().size());
		assertEquals("There is a super class if there isn't a super interface", expected, newClass.getAnonymousClass().getSuperclass().getActualClass());
	}

	private void assertSuperInterface(Class<?> expected, CtNewClass<?> newClass) {
		assertNull("There isn't super class if there is a super interface", newClass.getAnonymousClass().getSuperclass());
		assertEquals("There is a super interface if there isn't super class", expected, newClass.getAnonymousClass().getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass());
	}

	private void assertIsAnonymous(CtNewClass<?> newClass) {
		assertTrue("Class in CtNewClass is anonymous", newClass.getAnonymousClass().isAnonymous());
	}

	private void assertHasParameters(int sizeExpected, CtNewClass<?> newClass) {
		if (sizeExpected == 0) {
			assertEquals("New class without parameter", sizeExpected, newClass.getArguments().size());
		} else {
			assertEquals("New class with parameters", sizeExpected, newClass.getArguments().size());
		}
	}

	private void assertIsConstructor(CtNewClass<?> newClass) {
		assertTrue("Method must be a constructor", newClass.getExecutable().isConstructor());
	}

	private void assertType(Class<?> typeExpected, CtNewClass<?> newClass) {
		assertEquals("New class is typed by the class of the constructor", typeExpected, newClass.getType().getActualClass());
	}
}
