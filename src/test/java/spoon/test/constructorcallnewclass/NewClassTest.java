package spoon.test.constructorcallnewclass;

import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;
import spoon.test.constructorcallnewclass.testclasses.Bar;
import spoon.test.constructorcallnewclass.testclasses.Foo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(0, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperClass(Object.class, newClass.getAnonymousClass());
	}

	@Test
	public void testNewClassWithInterface() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(1);
		assertType(Foo.Bar.class, newClass);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(0, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperInterface(Foo.Bar.class, newClass.getAnonymousClass());
	}

	@Test
	public void testNewClassWithInterfaceGeneric() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(2);
		assertType(Foo.Tacos.class, newClass);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(0, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperInterface(Foo.Tacos.class, newClass.getAnonymousClass());
		assertEquals("Super interface is typed by the class of the constructor", String.class,
				newClass.getAnonymousClass().getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualTypeArguments().get(0).getActualClass());
	}

	@Test
	public void testNewClassInterfaceWithParameters() throws Exception {
		final CtNewClass<?> newClass = newClasses.get(3);
		assertType(Foo.BarImpl.class, newClass);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(1, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperClass(Foo.BarImpl.class, newClass.getAnonymousClass());
	}

	@Test
	public void testNewClassInEnumeration() throws Exception {
		Factory factory = null;
		try {
			factory = TestUtils.build(Bar.class);
		} catch (NullPointerException e) {
			fail();
		}
		final CtClass<?> foo = (CtClass<?>) factory.Type().get(Bar.class);
		final CtNewClass<?> newClass = foo.getElements(new TypeFilter<CtNewClass<?>>(CtNewClass.class)).get(0);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(1, newClass.getArguments());
		assertEquals("\">\"", newClass.getArguments().get(0).toString());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperClass(Bar.class, newClass.getAnonymousClass());
	}

	private void assertSuperClass(Class<?> expected, CtClass<?> anonymousClass) {
		assertEquals("There isn't a super interface if there is a super class", 0, anonymousClass.getSuperInterfaces().size());
		assertEquals("There is a super class if there isn't a super interface", expected, anonymousClass.getSuperclass().getActualClass());
	}

	private void assertSuperInterface(Class<?> expected, CtClass<?> anonymousClass) {
		assertNull("There isn't super class if there is a super interface", anonymousClass.getSuperclass());
		assertEquals("There is a super interface if there isn't super class", expected, anonymousClass.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass());
	}

	private void assertIsAnonymous(CtClass<?> anonymousClass) {
		assertTrue("Class in CtNewClass is anonymous", anonymousClass.isAnonymous());
	}

	private void assertHasParameters(int sizeExpected, List<CtExpression<?>> arguments) {
		if (sizeExpected == 0) {
			assertEquals("New class without parameter", sizeExpected, arguments.size());
		} else {
			assertEquals("New class with parameters", sizeExpected, arguments.size());
		}
	}

	private void assertIsConstructor(CtExecutableReference<?> executable) {
		assertTrue("Method must be a constructor", executable.isConstructor());
	}

	private void assertType(Class<?> typeExpected, CtNewClass<?> newClass) {
		assertEquals("New class is typed by the class of the constructor", typeExpected, newClass.getType().getActualClass());
	}
}
