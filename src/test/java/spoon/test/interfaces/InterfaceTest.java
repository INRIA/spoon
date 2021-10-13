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

import java.util.Collection;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.SpoonTestHelpers;
import spoon.test.interfaces.testclasses.ExtendsDefaultMethodInterface;
import spoon.test.interfaces.testclasses.ExtendsStaticMethodInterface;
import spoon.test.interfaces.testclasses.InterfaceWithDefaultMethods;
import spoon.test.interfaces.testclasses.RedefinesDefaultMethodInterface;
import spoon.test.interfaces.testclasses.RedefinesStaticMethodInterface;

import java.io.File;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.test.SpoonTestHelpers.contentEquals;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

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

		// contract: the toString does not show the implicit modifiers (here "public")
		final String expected =
				"default java.time.ZonedDateTime getZonedDateTime(java.lang.String zoneString) {"
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

	@org.junit.jupiter.api.Test
	void testLocalInterfaceExists() {
		// contract: local interfaces and their members are part of the model
		String code = SpoonTestHelpers.wrapLocal(
				"		interface MyInterface {\n" +
						"			static final int A = 1;\n" +
						"			void doNothing();\n" +
						"		}\n"
		);
		CtModel model = SpoonTestHelpers.createModelFromString(code, 16);
		CtBlock<?> block = SpoonTestHelpers.getBlock(model);

		assertThat("The local interface does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		Assertions.assertTrue(statement instanceof CtInterface<?>);
		CtInterface<?> interfaceType = (CtInterface<?>) statement;

		assertThat(interfaceType.isLocalType(), is(true));
		assertThat(interfaceType.getSimpleName(), is("1MyInterface"));
		assertThat(interfaceType.getFields().size(), is(1));
		assertThat(interfaceType.getMethods().size(), is(1));
		MatcherAssert.assertThat(interfaceType.getExtendedModifiers(), contentEquals(
				new CtExtendedModifier(ModifierKind.STATIC, true),
				new CtExtendedModifier(ModifierKind.ABSTRACT, true)
		));
	}

	@org.junit.jupiter.api.Test
	void testPackageLevelInterfaceModifiers() throws Exception {
		// contract: a simple interface has the correct modifiers applied
		// see https://docs.oracle.com/javase/specs/jls/se17/html/jls-9.html#jls-9.1.1
		CtType<?> emptyInterface = build("spoon.test.interfaces.testclasses", "EmptyInterface");
		assertThat(emptyInterface.getExtendedModifiers(), contentEquals(
				new CtExtendedModifier(ModifierKind.ABSTRACT, true),
				new CtExtendedModifier(ModifierKind.PUBLIC, false)
		));
	}

	@Test
	public void testNestedTypesInInterfaceArePublic() {
		// contract: nested types in interfaces are implicitly public
		// (https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.5)

		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/nestedInInterface");
		CtModel model = launcher.buildModel();

		Collection<CtType<?>> types = model.getAllTypes()
				.stream()
				.flatMap(it -> it.getNestedTypes().stream())
				.collect(Collectors.toList());

		assertEquals(4, types.size());

		for (CtType<?> type : types) {
			assertTrue("Nested type " + type.getQualifiedName() + " is not public", type.isPublic());
			CtExtendedModifier modifier = type.getExtendedModifiers()
					.stream()
					.filter(it -> it.getKind() == ModifierKind.PUBLIC)
					.findFirst()
					.get();
			assertTrue(
					"nested type " + type.getQualifiedName() + " has explicit modifier",
					modifier.isImplicit()
			);
		}
	}

	@Test
	public void testNestedTypesInInterfaceAreStatic() {
		// contract: nested types in interfaces are implicitly static
		// (https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.5)

		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/nestedInInterface");
		CtModel model = launcher.buildModel();

		Collection<CtType<?>> types = model.getAllTypes()
				.stream()
				.flatMap(it -> it.getNestedTypes().stream())
				.collect(Collectors.toList());

		assertEquals(4, types.size());

		for (CtType<?> type : types) {
			assertTrue("Nested type " + type.getQualifiedName() + " is not static", type.isStatic());
			CtExtendedModifier modifier = type.getExtendedModifiers()
					.stream()
					.filter(it -> it.getKind() == ModifierKind.STATIC)
					.findFirst()
					.get();
			assertTrue(
					"nested type " + type.getQualifiedName() + " has explicit modifier",
					modifier.isImplicit()
			);
		}
	}

	@Test
	public void testImplicitPublicModifierInNestedInterfaceTypeIsRemoved() {
		// contract: implicit public modifier for nested types is deleted when they are removed from the interface
		Factory factory = createFactory();
		CtInterface<?> ctInterface = factory.Interface().create("foo.Bar");
		CtClass<?> nestedClass = factory.Class().create("foo.Bar$Inner");
		ctInterface.addNestedType(nestedClass);

		assertTrue("Class wasn't made public", nestedClass.isPublic());
		ctInterface.removeNestedType(nestedClass);

		assertFalse("public modifier wasn't removed", nestedClass.isPublic());
	}

	@Test
	public void testImplicitStaticModifierInNestedInterfaceTypeIsRemoved() {
		// contract: implicit static modifier for nested types is deleted when they are removed from the interface
		Factory factory = createFactory();
		CtInterface<?> ctInterface = factory.Interface().create("foo.Bar");
		CtClass<?> nestedClass = factory.Class().create("foo.Bar$Inner");
		ctInterface.addNestedType(nestedClass);

		assertTrue("Class wasn't made static", nestedClass.isStatic());
		ctInterface.removeNestedType(nestedClass);

		assertFalse("static modifier wasn't removed", nestedClass.isStatic());
	}

}
