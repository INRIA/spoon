/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.constructorcallnewclass;


import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.FluentLauncher;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.constructorcallnewclass.testclasses.Bar;
import spoon.test.constructorcallnewclass.testclasses.Foo;
import spoon.test.constructorcallnewclass.testclasses.Foo2;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class NewClassTest {
	private List<CtNewClass<?>> newClasses;

	@BeforeEach
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
	public void testNewClassWithObjectClass() {
		final CtNewClass<?> newClass = newClasses.get(0);
		assertType(Object.class, newClass);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(0, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperClass(Object.class, newClass.getAnonymousClass());

		// contract: createNewClass() returns a default anonymous class
		CtNewClass<?> klass =newClass.getFactory().createNewClass();
		assertEquals("new java.lang.Object() {}", newClass.toString());
	}

	@Test
	public void testNewClassWithInterface() {
		final CtNewClass<?> newClass = newClasses.get(1);
		assertType(Foo.Bar.class, newClass);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(0, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperInterface(Foo.Bar.class, newClass.getAnonymousClass());

		// contract: one can create an anonymous new class from an existing class
		Factory factory = newClass.getFactory();
		CtNewClass<?> klass = factory.createNewClass(factory.Type().get(Foo.class));
		assertEquals("new spoon.test.constructorcallnewclass.testclasses.Foo() {}", klass.toString());

		CtNewClass<?> klass2 = factory.createNewClass(factory.Type().get(Foo.class), factory.createLiteral(42));
		assertEquals("new spoon.test.constructorcallnewclass.testclasses.Foo(42) {}", klass2.toString());

	}

	@Test
	public void testNewClassWithInterfaceGeneric() {
		final CtNewClass<?> newClass = newClasses.get(2);
		assertType(Foo.Tacos.class, newClass);
		assertIsConstructor(newClass.getExecutable());
		assertHasParameters(0, newClass.getArguments());
		assertIsAnonymous(newClass.getAnonymousClass());
		assertSuperInterface(Foo.Tacos.class, newClass.getAnonymousClass());
		CtTypeReference[] ctTypeReferences = newClass.getAnonymousClass().getSuperInterfaces().toArray(new CtTypeReference[0]);
		assertSame(String.class, ctTypeReferences[0].getActualTypeArguments().get(0).getActualClass(), "Super interface is typed by the class of the constructor");
	}

	@Test
	public void testNewClassInterfaceWithParameters() {
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
		assertEquals(0, anonymousClass.getSuperInterfaces().size(), "There isn't a super interface if there is a super class");
		assertSame(expected, anonymousClass.getSuperclass().getActualClass(), "There is a super class if there isn't a super interface");
	}

	private void assertSuperInterface(Class<?> expected, CtClass<?> anonymousClass) {
		assertNull(anonymousClass.getSuperclass(), "There isn't super class if there is a super interface");
		assertSame(expected, anonymousClass.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getActualClass(), "There is a super interface if there isn't super class");
	}

	private void assertIsAnonymous(CtClass<?> anonymousClass) {
		assertTrue(anonymousClass.isAnonymous(), "Class in CtNewClass is anonymous");
	}

	private void assertHasParameters(int sizeExpected, List<CtExpression<?>> arguments) {
		if (sizeExpected == 0) {
			assertEquals(sizeExpected, arguments.size(), "New class without parameter");
		} else {
			assertEquals(sizeExpected, arguments.size(), "New class with parameters");
		}
	}

	private void assertIsConstructor(CtExecutableReference<?> executable) {
		assertTrue(executable.isConstructor(), "Method must be a constructor");
	}

	private void assertType(Class<?> typeExpected, CtNewClass<?> newClass) {
		assertSame(typeExpected, newClass.getType().getActualClass(), "New class is typed by the class of the constructor");
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
	public void testCtNewClassInNoClasspath() {
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
		assertEquals("org.apache.lucene.store.Lock$With", anonymousClass.getSuperclass().getQualifiedName());
		assertEquals("Lock", anonymousClass.getSuperclass().getDeclaringType().getSimpleName());
		assertEquals("org.apache.lucene.store.Lock.With", anonymousClass.getSuperclass().toString());
		assertEquals("1", anonymousClass.getSimpleName());
		assertEquals("2", secondNewClass.getAnonymousClass().getSimpleName());
		assertEquals(1, anonymousClass.getMethods().size());

		canBeBuilt("./target/new-class", 8, true);
	}

	@Test
	public void testBadConstructorCallToAnonymousGenericType() {
		// contract: Spoon should be able to resolve the type of a constructor call to an anonymous
		// subclass of a generic type, when the constructor does not exist.
		// See https://github.com/INRIA/spoon/issues/3913 for details.

		CtModel model = new FluentLauncher()
				.inputResource("./src/test/resources/noclasspath/BadAnonymousClassOfNestedType.java")
				.buildModel();
		CtNewClass<?> newClass = model.filterChildren(CtNewClass.class::isInstance).first();
		CtType<?> anonymousClass = newClass.getAnonymousClass();

		CtType<?> expectedSuperclass = model
				.getUnnamedModule()
				.getFactory()
				.Type()
				.get("BadAnonymousClassOfNestedType$GenericType");
		assertThat(anonymousClass.getQualifiedName(), startsWith("BadAnonymousClassOfNestedType"));
		assertThat(anonymousClass.getSuperclass().getTypeDeclaration(), equalTo(expectedSuperclass));
	}
}
