package spoon.test.constructorcallnewclass;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.constructorcallnewclass.testclasses.Bar;
import spoon.test.constructorcallnewclass.testclasses.Foo;
import spoon.test.constructorcallnewclass.testclasses.Foo2;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class NewClassTest {
	private List<CtNewClass<?>> newClasses;

	@Before
	public void setUp() throws Exception {
		final Factory build = build(Foo.class);
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
		CtTypeReference[] ctTypeReferences = newClass.getAnonymousClass().getSuperInterfaces().toArray(new CtTypeReference[0]);
		assertEquals("Super interface is typed by the class of the constructor", String.class,
				ctTypeReferences[0].getActualTypeArguments().get(0).getActualClass());
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
			factory = build(Bar.class);
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

	@Test
	public void testMoreThan9NewClass() throws Exception {
		final Factory build = build(Foo2.class);
		final CtClass<?> foo = (CtClass<?>) build.Type().get(Foo2.class);
		List<CtNewClass<?>> elements = foo.getElements(new AbstractFilter<CtNewClass<?>>(CtNewClass.class) {
			@Override
			public boolean matches(CtNewClass<?> element) {
				return true;
			}
		});
		assertEquals(13, elements.size());
		assertEquals(Foo2.class.getCanonicalName() + "$12", elements.get(11).getAnonymousClass().getQualifiedName());
		assertEquals(Foo2.class.getCanonicalName() + "$12$1", elements.get(12).getAnonymousClass().getQualifiedName());
	}

	@Test
	public void testCtNewClassInNoClasspath() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/new-class");
		launcher.setSourceOutputDirectory("./target/new-class");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("IndexWriter");
		final List<CtNewClass> ctNewClasses = aClass.getElements(new TypeFilter<>(CtNewClass.class));
		final CtNewClass ctNewClass = ctNewClasses.get(0);
		final CtNewClass secondNewClass = ctNewClasses.get(1);

		final CtClass anonymousClass = ctNewClass.getAnonymousClass();
		assertNotNull(anonymousClass);
		assertNotNull(anonymousClass.getSuperclass());
		assertEquals("With", anonymousClass.getSuperclass().getSimpleName());
		assertEquals("Lock$With", anonymousClass.getSuperclass().getQualifiedName());
		assertEquals("Lock", anonymousClass.getSuperclass().getDeclaringType().getSimpleName());
		assertEquals("Lock.With", anonymousClass.getSuperclass().toString());
		assertEquals("1", anonymousClass.getSimpleName());
		assertEquals("2", secondNewClass.getAnonymousClass().getSimpleName());
		assertEquals(1, anonymousClass.getMethods().size());

		canBeBuilt("./target/new-class", 8, true);
	}
}
