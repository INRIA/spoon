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
package spoon.test.interfaces;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.test.interfaces.testclasses.ExtendsDefaultMethodInterface;
import spoon.test.interfaces.testclasses.ExtendsStaticMethodInterface;
import spoon.test.interfaces.testclasses.InterfaceWithDefaultMethods;
import spoon.test.interfaces.testclasses.RedefinesDefaultMethodInterface;
import spoon.test.interfaces.testclasses.RedefinesStaticMethodInterface;

import java.io.File;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InterfaceTest {

	private Factory factory;

	@Before
	public void setUp() {
		final File testDirectory = new File("./src/test/java/spoon/test/interfaces/testclasses/");

		final Launcher launcher = new Launcher();

		this.factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		SpoonModelBuilder compiler = launcher.createCompiler(this.factory);

		compiler.addInputSource(testDirectory);
		compiler.build();
	}

	@Test
	public void testDefaultMethodInInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(InterfaceWithDefaultMethods.class);

		final CtMethod<?> ctMethod = ctInterface.getMethodsByName("getZonedDateTime").get(0);
		assertTrue("The method in the interface must to be default", ctMethod.isDefaultMethod());

		// contract: the debug toString also shows the implicit modifiers (here "public")
		final String expected =
				"public default java.time.ZonedDateTime getZonedDateTime(java.lang.String zoneString) {"
						+ System.lineSeparator()
						+ "    return java.time.ZonedDateTime.of(getLocalDateTime(), spoon.test.interfaces.testclasses.InterfaceWithDefaultMethods.getZoneId(zoneString));"
						+ System.lineSeparator() + "}";
		assertEquals("The default method must to be well printed", expected, ctMethod.toString());
	}

	@Test
	public void testDefaultMethodInConsumer() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(Consumer.class);

		final CtMethod<?> ctMethod = ctInterface.getMethodsByName("andThen").get(0);
		assertTrue("The method in the interface must to be default", ctMethod.isDefaultMethod());
	}

	@Test
	public void testExtendsDefaultMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(ExtendsDefaultMethodInterface.class);

		assertEquals("Sub interface must have only one method in its interface", 1, ctInterface.getMethods().size());
		assertEquals("Sub interface must have 6 methods in its interface and its super interfaces", 6, ctInterface.getAllMethods().size());

		final CtMethod<?> getZonedDateTimeMethod = ctInterface.getMethodsByName("getZonedDateTime").get(0);
		assertTrue("Method in the sub interface must be a default method", getZonedDateTimeMethod.isDefaultMethod());
		assertSame("Interface of the default method must be the sub interface", ExtendsDefaultMethodInterface.class, getZonedDateTimeMethod.getDeclaringType().getActualClass());
	}

	@Test
	public void testRedefinesDefaultMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(RedefinesDefaultMethodInterface.class);

		assertEquals("Sub interface must have only one method in its interface", 1, ctInterface.getMethods().size());
		assertEquals("Sub interface must have 6 methods in its interface and its super interfaces", 6, ctInterface.getAllMethods().size());

		final CtMethod<?> getZonedDateTimeMethod = ctInterface.getMethodsByName("getZonedDateTime").get(0);
		assertFalse("Method in the sub interface mustn't be a default method", getZonedDateTimeMethod.isDefaultMethod());
		assertSame("Interface of the default method must be the sub interface", RedefinesDefaultMethodInterface.class, getZonedDateTimeMethod.getDeclaringType().getActualClass());
	}

	@Test
	public void testExtendsStaticMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(ExtendsStaticMethodInterface.class);

		assertEquals("Sub interface must have only one method in its interface", 1, ctInterface.getMethods().size());
		assertEquals("Sub interface must have 6 methods in its interface and its super interfaces", 6, ctInterface.getAllMethods().size());

		final CtMethod<?> getZoneIdMethod = ctInterface.getMethodsByName("getZoneId").get(0);
		assertTrue("Method in the sub interface must be a static method", getZoneIdMethod.getModifiers().contains(ModifierKind.STATIC));
		assertSame("Interface of the static method must be the sub interface", ExtendsStaticMethodInterface.class, getZoneIdMethod.getDeclaringType().getActualClass());
	}

	@Test
	public void testRedefinesStaticMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(RedefinesStaticMethodInterface.class);

		assertEquals("Sub interface must have only one method in its interface", 1, ctInterface.getMethods().size());
		assertEquals("Sub interface must have 6+12(from java.lang.Object) methods in its interface and its super interfaces", 6, ctInterface.getAllMethods().size());

		final CtMethod<?> getZoneIdMethod = ctInterface.getMethodsByName("getZoneId").get(0);
		assertFalse("Method in the sub interface mustn't be a static method", getZoneIdMethod.getModifiers().contains(ModifierKind.STATIC));
		assertSame("Interface of the static method must be the sub interface", RedefinesStaticMethodInterface.class, getZoneIdMethod.getDeclaringType().getActualClass());
	}
}
